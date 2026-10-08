import SwiftUI
import Combine

public struct ActiveWorkoutView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @State private var viewModel: ActiveWorkoutViewModel
    private let onDismiss: () -> Void

    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    public init(training: Training, trainingRepository: TrainingRepository, onDismiss: @escaping () -> Void = {}) {
        self._viewModel = State(initialValue: ActiveWorkoutViewModel(training: training, trainingRepository: trainingRepository))
        self.onDismiss = onDismiss
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Sticky HUD Bar (Stopwatch, Progress, and Controls)
                VStack(spacing: 8) {
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(viewModel.training.name)
                                .font(.headline)
                                .lineLimit(1)
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(viewModel.isTimerRunning ? theme.selectedAccent.color : Color.orange)
                                    .frame(width: 8, height: 8)
                                Text(viewModel.formattedElapsed)
                                    .font(.title2.monospacedDigit().weight(.bold))
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                        }

                        Spacer()

                        HStack(spacing: 10) {
                            // Pause / Resume Toggle
                            Button {
                                Task {
                                    if viewModel.isTimerRunning {
                                        await viewModel.pauseWorkout()
                                    } else {
                                        await viewModel.resumeWorkout()
                                    }
                                }
                            } label: {
                                Image(systemName: viewModel.isTimerRunning ? "pause.fill" : "play.fill")
                                    .font(.subheadline.weight(.bold))
                                    .frame(width: 36, height: 36)
                                    .background(theme.surfaceStyle.tertiaryBackgroundColor)
                                    .clipShape(Circle())
                            }

                            // Finish Workout Button
                            Button {
                                viewModel.isCompleting = true
                            } label: {
                                Text("Finish")
                                    .font(.subheadline.weight(.bold))
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 8)
                                    .background(theme.selectedAccent.color)
                                    .foregroundStyle(theme.selectedAccent.badgeTextColor)
                                    .clipShape(Capsule())
                            }
                        }
                    }

                    // Progress Gauge
                    VStack(alignment: .leading, spacing: 4) {
                        ProgressView(value: viewModel.training.completionPercentage, total: 100.0)
                            .tint(theme.selectedAccent.color)

                        HStack {
                            Text("\(viewModel.training.totalCompletedSets) of \(viewModel.training.totalSets) sets completed")
                                .font(.caption2)
                                .foregroundStyle(.secondary)
                            Spacer()
                            Text("\(Int(viewModel.training.completionPercentage))%")
                                .font(.caption2.weight(.semibold))
                                .foregroundStyle(theme.selectedAccent.color)
                        }
                    }
                }
                .padding()
                .background(theme.surfaceStyle.cardBackgroundColor)

                // Exercises & Sets List
                ScrollView {
                    VStack(spacing: 16) {
                        ForEach(viewModel.training.loggedExercises) { exerciseItem in
                            ExerciseWorkoutBlock(
                                exerciseItem: exerciseItem,
                                onToggleSet: { setNum in
                                    Task { await viewModel.toggleSetCompleted(exerciseId: exerciseItem.exerciseId, setNumber: setNum) }
                                },
                                onUpdateValues: { setNum, weight, reps in
                                    Task { await viewModel.updateSetValues(exerciseId: exerciseItem.exerciseId, setNumber: setNum, weight: weight, reps: reps) }
                                },
                                onAddSet: {
                                    Task { await viewModel.addSet(to: exerciseItem.exerciseId) }
                                },
                                onDeleteSet: { setNum in
                                    Task { await viewModel.deleteSet(exerciseId: exerciseItem.exerciseId, setNumber: setNum) }
                                }
                            )
                        }
                    }
                    .padding()
                    .padding(.bottom, viewModel.isRestTimerActive ? 80 : 20)
                }
                .background(theme.surfaceStyle.backgroundColor)
            }
            .safeAreaInset(edge: .bottom) {
                if viewModel.isRestTimerActive {
                    RestTimerBannerView(viewModel: viewModel)
                        .padding(.horizontal)
                        .padding(.bottom, 8)
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Close") {
                        dismiss()
                        onDismiss()
                    }
                }
            }
            .sheet(isPresented: $viewModel.isCompleting) {
                FinishWorkoutSheet(viewModel: viewModel) {
                    dismiss()
                    onDismiss()
                }
            }
            .onReceive(timer) { _ in
                viewModel.tickElapsed()
            }
        }
    }
}

private struct ExerciseWorkoutBlock: View {
    @Environment(\.theme) private var theme
    let exerciseItem: SessionExerciseItem
    let onToggleSet: (Int) -> Void
    let onUpdateValues: (Int, Double, Int) -> Void
    let onAddSet: () -> Void
    let onDeleteSet: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text(exerciseItem.exerciseName)
                    .font(.headline)
                    .foregroundStyle(.primary)

                Spacer()

                Text("\(exerciseItem.restSeconds)s rest")
                    .font(.caption2.weight(.bold))
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(theme.surfaceStyle.tertiaryBackgroundColor)
                    .clipShape(Capsule())
            }

            // Table Header
            HStack {
                Text("SET").frame(width: 36, alignment: .leading)
                Text("KG").frame(width: 70)
                Text("REPS").frame(width: 60)
                Spacer()
                Text("DONE").frame(width: 44)
            }
            .font(.caption2.weight(.bold))
            .foregroundStyle(.secondary)

            // Sets Rows
            ForEach(exerciseItem.sets) { set in
                SetInputRow(
                    set: set,
                    onToggle: { onToggleSet(set.setNumber) },
                    onDelete: { onDeleteSet(set.setNumber) }
                )
            }

            // Add Set Action
            Button(action: onAddSet) {
                HStack {
                    Image(systemName: "plus.circle.fill")
                    Text("Add Set")
                }
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(theme.selectedAccent.color)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.vertical, 6)
            }
            .buttonStyle(.plain)
        }
        .themedCard()
    }
}

private struct SetInputRow: View {
    @Environment(\.theme) private var theme
    let set: ResistanceSet
    let onToggle: () -> Void
    let onDelete: () -> Void

    var body: some View {
        HStack {
            Text("\(set.setNumber)")
                .font(.subheadline.monospacedDigit().weight(.bold))
                .frame(width: 36, alignment: .leading)

            Text("\(Int(set.weightKg)) kg")
                .font(.subheadline.monospacedDigit())
                .frame(width: 70)
                .padding(.vertical, 6)
                .background(theme.surfaceStyle.tertiaryBackgroundColor)
                .clipShape(RoundedRectangle(cornerRadius: 6))

            Text("\(set.repetitions)")
                .font(.subheadline.monospacedDigit())
                .frame(width: 60)
                .padding(.vertical, 6)
                .background(theme.surfaceStyle.tertiaryBackgroundColor)
                .clipShape(RoundedRectangle(cornerRadius: 6))

            Spacer()

            Button(action: onToggle) {
                ZStack {
                    RoundedRectangle(cornerRadius: 8)
                        .fill(set.isCompleted ? theme.selectedAccent.color : Color.secondary.opacity(0.2))
                        .frame(width: 38, height: 32)

                    if set.isCompleted {
                        Image(systemName: "checkmark")
                            .font(.subheadline.weight(.bold))
                            .foregroundStyle(theme.selectedAccent.badgeTextColor)
                    }
                }
            }
            .buttonStyle(.plain)
            .sensoryFeedback(.success, trigger: set.isCompleted)
        }
        .contextMenu {
            Button(role: .destructive, action: onDelete) {
                Label("Delete Set", systemImage: "trash")
            }
        }
    }
}
