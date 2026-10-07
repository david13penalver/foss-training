import SwiftUI

public struct ExerciseListView: View {
    private let exerciseRepository: ExerciseRepository
    @Environment(\.theme) private var theme
    @State private var viewModel: ExerciseListViewModel
    @State private var isPresentingCreateSheet: Bool = false
    @State private var exerciseToEdit: Exercise? = nil

    public init(exerciseRepository: ExerciseRepository) {
        self.exerciseRepository = exerciseRepository
        self._viewModel = State(initialValue: ExerciseListViewModel(exerciseRepository: exerciseRepository))
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Category Filter Chips
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        FilterChip(
                            title: "All",
                            isSelected: viewModel.selectedCategory == nil,
                            tintColor: theme.selectedAccent.color
                        ) {
                            Task { await viewModel.selectCategory(nil) }
                        }

                        ForEach(ExerciseCategory.allCases) { category in
                            FilterChip(
                                title: category.displayName,
                                icon: category.systemIcon,
                                isSelected: viewModel.selectedCategory == category,
                                tintColor: theme.selectedAccent.color
                            ) {
                                Task { await viewModel.selectCategory(category) }
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                }
                .background(theme.surfaceStyle.cardBackgroundColor)

                if viewModel.isLoading {
                    Spacer()
                    ProgressView()
                    Spacer()
                } else if viewModel.exercises.isEmpty {
                    ContentUnavailableView(
                        "No Exercises Found",
                        systemImage: "dumbbell",
                        description: Text("Try adjusting your filters or search terms.")
                    )
                } else {
                    List {
                        ForEach(viewModel.exercises) { exercise in
                            NavigationLink {
                                ExerciseDetailView(exercise: exercise)
                            } label: {
                                ExerciseRow(exercise: exercise)
                            }
                            .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                            .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                                Button(role: .destructive) {
                                    Task { await viewModel.deleteExercise(id: exercise.id) }
                                } label: {
                                    Label("Delete", systemImage: "trash")
                                }
                            }
                            .swipeActions(edge: .leading) {
                                Button {
                                    exerciseToEdit = exercise
                                } label: {
                                    Label("Edit", systemImage: "pencil")
                                }
                                .tint(theme.selectedAccent.color)
                            }
                        }
                    }
                    .scrollContentBackground(.hidden)
                    .background(theme.surfaceStyle.backgroundColor)
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Exercises")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        isPresentingCreateSheet = true
                    } label: {
                        Image(systemName: "plus")
                            .fontWeight(.bold)
                    }
                    .tint(theme.selectedAccent.color)
                }
            }
            .searchable(text: $viewModel.searchText, prompt: "Search exercise or muscle...")
            .onChange(of: viewModel.searchText) { _, _ in
                Task { await viewModel.loadExercises() }
            }
            .sheet(isPresented: $isPresentingCreateSheet) {
                ExerciseEditorSheet(exerciseRepository: exerciseRepository) {
                    Task { await viewModel.loadExercises() }
                }
            }
            .sheet(item: $exerciseToEdit) { ex in
                ExerciseEditorSheet(exerciseRepository: exerciseRepository, exerciseToEdit: ex) {
                    Task { await viewModel.loadExercises() }
                }
            }
            .task {
                await viewModel.loadExercises()
            }
        }
    }
}

private struct FilterChip: View {
    let title: String
    var icon: String? = nil
    let isSelected: Bool
    let tintColor: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                if let icon = icon {
                    Image(systemName: icon)
                }
                Text(title)
            }
            .font(.subheadline.weight(isSelected ? .bold : .medium))
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .background(isSelected ? tintColor : Color.secondary.opacity(0.15))
            .foregroundStyle(isSelected ? Color.black : Color.primary)
            .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }
}

private struct ExerciseRow: View {
    let exercise: Exercise

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(exercise.name)
                    .font(.headline)
                    .foregroundStyle(.primary)
                Spacer()
                Text(exercise.primaryCategory.displayName)
                    .themedBadge()
            }

            if let muscle = exercise.primaryMuscleGroup {
                Text(muscle)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(.vertical, 4)
    }
}

public struct ExerciseDetailView: View {
    @Environment(\.theme) private var theme
    let exercise: Exercise

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Header Card
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Label(exercise.primaryCategory.displayName, systemImage: exercise.primaryCategory.systemIcon)
                            .themedBadge()
                        Spacer()
                        Text(exercise.difficultyLevel.displayName)
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(.secondary)
                    }

                    Text(exercise.name)
                        .font(.title2.weight(.bold))

                    if let desc = exercise.description {
                        Text(desc)
                            .font(.body)
                            .foregroundStyle(.secondary)
                    }
                }
                .themedCard()

                // Target Anatomy
                if let muscle = exercise.primaryMuscleGroup {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Target Anatomy")
                            .font(.headline)

                        HStack {
                            Text("Primary: \(muscle)")
                                .font(.subheadline.weight(.semibold))
                        }

                        if !exercise.secondaryMuscleGroups.isEmpty {
                            Text("Secondary: \(exercise.secondaryMuscleGroups.joined(separator: ", "))")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                    .themedCard()
                }

                // Equipment
                if !exercise.equipmentRequired.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Equipment Required")
                            .font(.headline)
                        Text(exercise.equipmentRequired.map(\.displayName).joined(separator: ", "))
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .themedCard()
                }
            }
            .padding()
        }
        .background(theme.surfaceStyle.backgroundColor)
        .navigationBarTitleDisplayMode(.inline)
    }
}
