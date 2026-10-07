import SwiftUI

public struct WorkoutDashboardView: View {
    @Environment(\.theme) private var theme
    private let trainingRepository: TrainingRepository

    @State private var trainings: [Training] = []
    @State private var isLoading: Bool = false
    @State private var selectedTraining: Training? = nil

    public init(trainingRepository: TrainingRepository) {
        self.trainingRepository = trainingRepository
    }

    public var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    ProgressView()
                } else if trainings.isEmpty {
                    ContentUnavailableView(
                        "No Workouts Yet",
                        systemImage: "flame",
                        description: Text("Go to the Templates tab and start a workout to begin tracking.")
                    )
                } else {
                    List {
                        ForEach(trainings) { tr in
                            WorkoutHistoryCard(training: tr) {
                                selectedTraining = tr
                            }
                            .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                        }
                    }
                    .scrollContentBackground(.hidden)
                    .background(theme.surfaceStyle.backgroundColor)
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Workouts")
            .task {
                await loadTrainings()
            }
            .fullScreenCover(item: $selectedTraining) { tr in
                ActiveWorkoutView(training: tr, trainingRepository: trainingRepository) {
                    Task { await loadTrainings() }
                }
            }
        }
    }

    private func loadTrainings() async {
        isLoading = true
        do {
            self.trainings = try await trainingRepository.getTrainings()
        } catch {}
        isLoading = false
    }
}

private struct WorkoutHistoryCard: View {
    @Environment(\.theme) private var theme
    let training: Training
    let onOpen: () -> Void

    var body: some View {
        Button(action: onOpen) {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(training.name)
                        .font(.headline)
                        .foregroundStyle(.primary)
                    Spacer()
                    StatusBadge(status: training.status)
                }

                HStack {
                    Label("\(training.totalCompletedSets)/\(training.totalSets) sets", systemImage: "checklist")
                    Spacer()
                    if training.totalVolumeKg > 0 {
                        Text("\(Int(training.totalVolumeKg)) kg volume")
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                }
                .font(.caption)
                .foregroundStyle(.secondary)
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(.plain)
    }
}

private struct StatusBadge: View {
    @Environment(\.theme) private var theme
    let status: TrainingStatus

    var body: some View {
        Text(status.displayName)
            .font(.caption2.weight(.bold))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(bgColor)
            .foregroundStyle(fgColor)
            .clipShape(Capsule())
    }

    private var bgColor: Color {
        switch status {
        case .inProgress: return theme.selectedAccent.color.opacity(0.2)
        case .completed: return Color.green.opacity(0.2)
        case .paused: return Color.orange.opacity(0.2)
        default: return Color.secondary.opacity(0.2)
        }
    }

    private var fgColor: Color {
        switch status {
        case .inProgress: return theme.selectedAccent.color
        case .completed: return Color.green
        case .paused: return Color.orange
        default: return Color.secondary
        }
    }
}
