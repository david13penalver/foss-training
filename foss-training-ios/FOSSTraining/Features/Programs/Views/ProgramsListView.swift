import SwiftUI

public struct ProgramsListView: View {
    @Environment(\.theme) private var theme
    private let programRepository: TrainingProgramRepository
    private let sessionRepository: SessionRepository

    @State private var viewModel: ProgramsListViewModel
    @State private var programToEdit: TrainingProgram? = nil
    @State private var isCreatingNewProgram: Bool = false
    @State private var programToDelete: TrainingProgram? = nil
    @State private var showDeleteAlert: Bool = false

    public init(
        programRepository: TrainingProgramRepository,
        sessionRepository: SessionRepository
    ) {
        self.programRepository = programRepository
        self.sessionRepository = sessionRepository
        self._viewModel = State(initialValue: ProgramsListViewModel(
            programRepository: programRepository
        ))
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Level filter bar
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        filterChip(title: "All", isSelected: viewModel.selectedLevel == nil) {
                            viewModel.selectedLevel = nil
                        }

                        ForEach(ProgramLevel.allCases, id: \.self) { level in
                            filterChip(title: level.displayName, isSelected: viewModel.selectedLevel == level) {
                                viewModel.selectedLevel = level
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                .background(Color.black.opacity(0.15))

                Group {
                    if viewModel.isLoading && viewModel.programs.isEmpty {
                        ProgressView()
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else if viewModel.filteredPrograms.isEmpty {
                        ContentUnavailableView(
                            viewModel.searchText.isEmpty ? "No Training Programs" : "No Matching Programs",
                            systemImage: viewModel.searchText.isEmpty ? "calendar.badge.clock" : "magnifyingglass",
                            description: Text(viewModel.searchText.isEmpty ? "Create periodized mesocycles to plan your progression." : "Try adjusting your search query or level filter.")
                        )
                    } else {
                        List {
                            ForEach(viewModel.filteredPrograms) { program in
                                ZStack {
                                    NavigationLink {
                                        ProgramDetailView(
                                            programRepository: programRepository,
                                            sessionRepository: sessionRepository,
                                            program: program
                                        )
                                    } label: {
                                        EmptyView()
                                    }
                                    .opacity(0)

                                    ProgramCardView(
                                        program: program,
                                        adherence: viewModel.adherences[program.id],
                                        onClone: {
                                            Task { await viewModel.cloneProgram(id: program.id) }
                                        },
                                        onDelete: {
                                            programToDelete = program
                                            showDeleteAlert = true
                                        }
                                    )
                                }
                                .listRowInsets(EdgeInsets(top: 6, leading: 16, bottom: 6, trailing: 16))
                                .listRowSeparator(.hidden)
                                .listRowBackground(Color.clear)
                            }
                        }
                        .listStyle(.plain)
                    }
                }
            }
            .navigationTitle("Programs")
            .searchable(text: $viewModel.searchText, prompt: "Search training programs")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        isCreatingNewProgram = true
                    } label: {
                        Image(systemName: "plus")
                            .font(.headline)
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                }
            }
            .sheet(isPresented: $isCreatingNewProgram) {
                ProgramEditorSheet(
                    programRepository: programRepository,
                    sessionRepository: sessionRepository
                )
            }
            .sheet(item: $programToEdit) { program in
                ProgramEditorSheet(
                    programRepository: programRepository,
                    sessionRepository: sessionRepository,
                    programToEdit: program
                )
            }
            .alert("Delete Program", isPresented: $showDeleteAlert, presenting: programToDelete) { program in
                Button("Delete", role: .destructive) {
                    Task { await viewModel.deleteProgram(id: program.id) }
                }
                Button("Cancel", role: .cancel) {}
            } message: { program in
                Text("Are you sure you want to delete '\(program.name)'? This action cannot be undone.")
            }
            .task {
                await viewModel.loadPrograms()
            }
            .refreshable {
                await viewModel.loadPrograms()
            }
        }
    }

    private func filterChip(title: String, isSelected: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(title)
                .font(.caption.weight(isSelected ? .bold : .medium))
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(isSelected ? theme.selectedAccent.color : Color.white.opacity(0.08))
                .foregroundStyle(isSelected ? .white : .secondary)
                .clipShape(Capsule())
        }
    }
}
