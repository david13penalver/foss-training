import SwiftUI

public struct FinishWorkoutSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @Bindable var viewModel: ActiveWorkoutViewModel
    let onFinished: () -> Void

    @State private var showCancelConfirmation: Bool = false

    public init(viewModel: ActiveWorkoutViewModel, onFinished: @escaping () -> Void) {
        self.viewModel = viewModel
        self.onFinished = onFinished
    }

    private var rpeDescription: String {
        switch viewModel.selectedRpe {
        case 1.0..<5.0:
            return "Light / Active Recovery"
        case 5.0..<7.0:
            return "Moderate Effort"
        case 7.0..<8.5:
            return "Hard / 2–3 Reps in Reserve"
        case 8.5..<10.0:
            return "Very Hard / 1 Rep in Reserve"
        default:
            return "Maximum Effort / 0 RIR"
        }
    }

    public var body: some View {
        NavigationStack {
            Form {
                // Workout Metrics Summary
                Section("Summary") {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Total Volume")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                            Text("\(Int(viewModel.training.totalVolumeKg)) kg")
                                .font(.title3.weight(.bold))
                                .foregroundStyle(theme.selectedAccent.color)
                        }
                        Spacer()
                        VStack(alignment: .center, spacing: 4) {
                            Text("Sets Completed")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                            Text("\(viewModel.training.totalCompletedSets) / \(viewModel.training.totalSets)")
                                .font(.title3.weight(.bold))
                        }
                        Spacer()
                        VStack(alignment: .trailing, spacing: 4) {
                            Text("Duration")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                            Text(viewModel.formattedElapsed)
                                .font(.title3.weight(.bold).monospacedDigit())
                        }
                    }
                    .padding(.vertical, 4)
                }

                // Rate of Perceived Exertion (RPE)
                Section("Perceived Exertion (RPE 1 - 10)") {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("RPE \(String(format: "%.1f", viewModel.selectedRpe))")
                                .font(.headline)
                                .foregroundStyle(theme.selectedAccent.color)
                            Spacer()
                            Text(rpeDescription)
                                .font(.caption.weight(.semibold))
                                .foregroundStyle(.secondary)
                        }

                        Slider(value: $viewModel.selectedRpe, in: 1.0...10.0, step: 0.5)
                            .tint(theme.selectedAccent.color)
                    }
                }

                // Notes
                Section("Workout Notes") {
                    TextField("How did the session feel? Any pain or breakthroughs?", text: $viewModel.completionNotes, axis: .vertical)
                        .lineLimit(3...6)
                }

                // Save Action
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

                // Destructive Cancel Workout
                Section {
                    Button(role: .destructive) {
                        showCancelConfirmation = true
                    } label: {
                        Text("Discard Workout")
                            .frame(maxWidth: .infinity)
                    }
                }
            }
            .navigationTitle("Finish Workout")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Back to Workout") { dismiss() }
                }
            }
            .confirmationDialog(
                "Are you sure you want to discard this workout?",
                isPresented: $showCancelConfirmation,
                titleVisibility: .visible
            ) {
                Button("Discard Workout", role: .destructive) {
                    Task {
                        await viewModel.cancelWorkout()
                        dismiss()
                        onFinished()
                    }
                }
                Button("Cancel", role: .cancel) {}
            } message: {
                Text("All logged sets and timer progress for this session will be cancelled.")
            }
        }
    }
}
