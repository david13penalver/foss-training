import SwiftUI

public struct SessionListView: View {
    @Environment(\.theme) private var theme
    @State private var viewModel: SessionListViewModel

    public init(sessionRepository: SessionRepository, trainingRepository: TrainingRepository) {
        self._viewModel = State(initialValue: SessionListViewModel(
            sessionRepository: sessionRepository,
            trainingRepository: trainingRepository
        ))
    }

    public var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading {
                    ProgressView()
                } else if viewModel.sessions.isEmpty {
                    ContentUnavailableView(
                        "No Workout Templates",
                        systemImage: "list.bullet.rectangle",
                        description: Text("Create your first training template to get started.")
                    )
                } else {
                    List {
                        ForEach(viewModel.sessions) { session in
                            SessionCardRow(session: session) {
                                Task { await viewModel.startWorkout(from: session) }
                            }
                            .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                        }
                    }
                    .scrollContentBackground(.hidden)
                    .background(theme.surfaceStyle.backgroundColor)
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Templates")
            .task {
                await viewModel.loadSessions()
            }
            .fullScreenCover(item: $viewModel.launchedTraining) { training in
                ActiveWorkoutHostView(training: training)
            }
        }
    }
}

private struct SessionCardRow: View {
    @Environment(\.theme) private var theme
    let session: Session
    let onStart: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Text(session.name)
                    .font(.headline)
                Spacer()
                if let dur = session.estimatedDurationMinutes {
                    Label("\(dur) min", systemImage: "clock")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
            }

            if let desc = session.description {
                Text(desc)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }

            Text("\(session.exercises.count) exercises")
                .font(.caption)
                .foregroundStyle(.secondary)

            Button(action: onStart) {
                HStack {
                    Image(systemName: "play.fill")
                    Text("Start This Workout")
                        .fontWeight(.bold)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 10)
                .background(theme.selectedAccent.color)
                .foregroundStyle(theme.selectedAccent.badgeTextColor)
                .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            }
            .buttonStyle(.plain)
            .padding(.top, 4)
        }
        .padding(.vertical, 6)
    }
}

// Temporary host sheet for launched training
struct ActiveWorkoutHostView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    let training: Training

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("Workout in Progress")
                    .font(.title2.weight(.bold))

                Text(training.name)
                    .font(.headline)
                    .themedBadge()

                Text("Go to the 'Workouts' tab to track sets, reps, and RPE.")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                    .padding()

                Button("Dismiss") {
                    dismiss()
                }
                .buttonStyle(.borderedProminent)
                .tint(theme.selectedAccent.color)
            }
            .padding()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(theme.surfaceStyle.backgroundColor)
        }
    }
}
