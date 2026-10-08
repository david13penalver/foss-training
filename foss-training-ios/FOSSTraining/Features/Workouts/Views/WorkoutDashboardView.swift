import SwiftUI

public struct WorkoutDashboardView: View {
    @Environment(\.theme) private var theme
    private let trainingRepository: TrainingRepository
    @State private var viewModel: WorkoutDashboardViewModel

    public init(trainingRepository: TrainingRepository) {
        self.trainingRepository = trainingRepository
        self._viewModel = State(initialValue: WorkoutDashboardViewModel(trainingRepository: trainingRepository))
    }

    public var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading && viewModel.trainings.isEmpty {
                    ProgressView()
                } else if viewModel.trainings.isEmpty {
                    ContentUnavailableView(
                        "No Workouts Yet",
                        systemImage: "flame",
                        description: Text("Go to the Templates tab and tap 'Start Workout' to begin your first session.")
                    )
                } else {
                    List {
                        // Active Workout Sticky Banner
                        if let active = viewModel.activeWorkout {
                            Section {
                                Button {
                                    viewModel.selectedTraining = active
                                } label: {
                                    HStack(spacing: 12) {
                                        Circle()
                                            .fill(theme.selectedAccent.color)
                                            .frame(width: 10, height: 10)

                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(active.status == .inProgress ? "WORKOUT IN PROGRESS" : "WORKOUT PAUSED")
                                                .font(.caption2.weight(.bold))
                                                .foregroundStyle(theme.selectedAccent.color)
                                            Text(active.name)
                                                .font(.headline)
                                                .foregroundStyle(.primary)
                                        }

                                        Spacer()

                                        Text("Resume")
                                            .font(.subheadline.weight(.bold))
                                            .padding(.horizontal, 14)
                                            .padding(.vertical, 6)
                                            .background(theme.selectedAccent.color)
                                            .foregroundStyle(theme.selectedAccent.badgeTextColor)
                                            .clipShape(Capsule())
                                    }
                                    .padding(.vertical, 4)
                                }
                                .listRowBackground(theme.selectedAccent.color.opacity(0.12))
                            }
                        }

                        // Planned Upcoming Sessions
                        if !viewModel.plannedTrainings.isEmpty {
                            Section("Planned Workouts") {
                                ForEach(viewModel.plannedTrainings) { tr in
                                    WorkoutHistoryCardView(training: tr) {
                                        viewModel.selectedTraining = tr
                                    }
                                    .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                                }
                            }
                        }

                        // Completed Workout History Timeline
                        Section("Workout History") {
                            if viewModel.filteredCompletedTrainings.isEmpty {
                                Text("No matching historical workouts found.")
                                    .font(.subheadline)
                                    .foregroundStyle(.secondary)
                                    .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                            } else {
                                ForEach(viewModel.filteredCompletedTrainings) { tr in
                                    WorkoutHistoryCardView(training: tr) {
                                        viewModel.selectedTraining = tr
                                    }
                                    .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                                }
                            }
                        }
                    }
                    .scrollContentBackground(.hidden)
                    .background(theme.surfaceStyle.backgroundColor)
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Workouts")
            .searchable(text: $viewModel.searchText, prompt: "Search past workouts...")
            .refreshable {
                await viewModel.loadTrainings()
            }
            .task {
                await viewModel.loadTrainings()
            }
            .fullScreenCover(item: $viewModel.selectedTraining) { tr in
                ActiveWorkoutView(
                    training: tr,
                    trainingRepository: trainingRepository
                ) {
                    Task { await viewModel.loadTrainings() }
                }
            }
        }
    }
}
