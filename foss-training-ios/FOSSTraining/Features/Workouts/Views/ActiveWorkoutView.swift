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
                // Header Bar (Duration & Set Count)
                HStack {
                    VStack(alignment: .leading) {
                        Text(viewModel.training.name)
                            .font(.headline)
                        Text(formatDuration(viewModel.elapsedSeconds))
                            .font(.title2.monospacedDigit().weight(.bold))
                            .foregroundStyle(theme.selectedAccent.color)
                    }

                    Spacer()

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
                .padding()
                .background(theme.surfaceStyle.cardBackgroundColor)

                // Rest Timer Banner
                if viewModel.isRestTimerActive {
                    HStack {
                        Image(systemName: "timer")
                        Text("Rest: \(viewModel.restTimerSecondsRemaining)s")
                            .font(.subheadline.monospacedDigit().weight(.bold))
                        Spacer()
                        Button("Skip") {
                            viewModel.isRestTimerActive = false
                        }
                        .font(.caption.weight(.bold))
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color.orange.opacity(0.2))
                    .foregroundStyle(.orange)
                }

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
                                }
                            )
                        }
                    }
                    .padding()
                }
                .background(theme.surfaceStyle.backgroundColor)
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

    private func formatDuration(_ seconds: Int) -> String {
        let m = seconds / 60
        let s = seconds % 60
        return String(format: "%02d:%02d", m, s)
    }
}

private struct ExerciseWorkoutBlock: View {
    @Environment(\.theme) private var theme
    let exerciseItem: SessionExerciseItem
    let onToggleSet: (Int) -> Void
    let onUpdateValues: (Int, Double, Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(exerciseItem.exerciseName)
                .font(.headline)
                .foregroundStyle(.primary)

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
                SetInputRow(set: set) {
                    onToggleSet(set.setNumber)
                }
            }
        }
        .themedCard()
    }
}

private struct SetInputRow: View {
    @Environment(\.theme) private var theme
    let set: ResistanceSet
    let onToggle: () -> Void

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
    }
}

private struct FinishWorkoutSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @Bindable var viewModel: ActiveWorkoutViewModel
    let onFinished: () -> Void

    var body: some View {
        NavigationStack {
            Form {
                Section("Perceived Exertion (RPE: 1 - 10)") {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("RPE: \(String(format: "%.1f", viewModel.selectedRpe))")
                                .font(.headline)
                                .foregroundStyle(theme.selectedAccent.color)
                            Spacer()
                        }
                        Slider(value: $viewModel.selectedRpe, in: 1...10, step: 0.5)
                            .tint(theme.selectedAccent.color)
                    }
                }

                Section("Workout Notes") {
                    TextField("How did the session feel?", text: $viewModel.completionNotes, axis: .vertical)
                        .lineLimit(3...5)
                }

                Section {
                    Button {
                        Task {
                            await viewModel.finishWorkout()
                            dismiss()
                            onFinished()
                        }
                    } label: {
                        Text("Save & Complete Session")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                }
            }
            .navigationTitle("Finish Workout")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Cancel") { dismiss() }
                }
            }
        }
    }
}
