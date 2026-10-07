import SwiftUI

public struct SessionListView: View {
    @Environment(\.theme) private var theme
    private let sessionRepository: SessionRepository
    private let trainingRepository: TrainingRepository
    private let exerciseRepository: ExerciseRepository

    @State private var viewModel: SessionListViewModel
    @State private var sessionToEdit: Session?
    @State private var isCreatingNewSession: Bool = false
    @State private var sessionToDelete: Session?
    @State private var showDeleteAlert: Bool = false

    public init(
        sessionRepository: SessionRepository,
        trainingRepository: TrainingRepository,
        exerciseRepository: ExerciseRepository
    ) {
        self.sessionRepository = sessionRepository
        self.trainingRepository = trainingRepository
        self.exerciseRepository = exerciseRepository
        self._viewModel = State(initialValue: SessionListViewModel(
            sessionRepository: sessionRepository,
            trainingRepository: trainingRepository
        ))
    }

    public var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading && viewModel.sessions.isEmpty {
                    ProgressView()
                } else if viewModel.filteredSessions.isEmpty {
                    ContentUnavailableView(
                        viewModel.searchText.isEmpty ? "No Workout Templates" : "No Matching Templates",
                        systemImage: viewModel.searchText.isEmpty ? "list.bullet.rectangle" : "magnifyingglass",
                        description: Text(viewModel.searchText.isEmpty ? "Create your first training template to get started." : "Check your search terms or create a new template.")
                    )
                } else {
                    List {
                        ForEach(viewModel.filteredSessions) { session in
                            SessionCardView(
                                session: session,
                                onStart: {
                                    Task { await viewModel.startWorkout(from: session) }
                                },
                                onEdit: {
                                    sessionToEdit = session
                                },
                                onClone: {
                                    Task { await viewModel.cloneSession(id: session.id) }
                                },
                                onDelete: {
                                    sessionToDelete = session
                                    showDeleteAlert = true
                                }
                            )
                            .listRowInsets(EdgeInsets(top: 6, leading: 16, bottom: 6, trailing: 16))
                            .listRowSeparator(.hidden)
                            .listRowBackground(Color.clear)
                        }
                    }
                    .listStyle(.plain)
                    .scrollContentBackground(.hidden)
                    .refreshable {
                        await viewModel.loadSessions()
                    }
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Templates")
            .searchable(text: $viewModel.searchText, prompt: "Search templates...")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        isCreatingNewSession = true
                    } label: {
                        Image(systemName: "plus")
                            .fontWeight(.semibold)
                    }
                }
            }
            .task {
                await viewModel.loadSessions()
            }
            .sheet(isPresented: $isCreatingNewSession) {
                SessionEditorSheet(
                    sessionRepository: sessionRepository,
                    exerciseRepository: exerciseRepository,
                    sessionToEdit: nil
                )
                .onDisappear {
                    Task { await viewModel.loadSessions() }
                }
            }
            .sheet(item: $sessionToEdit) { session in
                SessionEditorSheet(
                    sessionRepository: sessionRepository,
                    exerciseRepository: exerciseRepository,
                    sessionToEdit: session
                )
                .onDisappear {
                    Task { await viewModel.loadSessions() }
                }
            }
            .confirmationDialog(
                "Delete Template",
                isPresented: $showDeleteAlert,
                presenting: sessionToDelete
            ) { session in
                Button("Delete \"\(session.name)\"", role: .destructive) {
                    Task { await viewModel.deleteSession(id: session.id) }
                }
            } message: { session in
                Text("Are you sure you want to delete this template? Past completed workouts will not be affected.")
            }
            .fullScreenCover(item: $viewModel.launchedTraining) { training in
                ActiveWorkoutHostView(training: training)
            }
        }
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
