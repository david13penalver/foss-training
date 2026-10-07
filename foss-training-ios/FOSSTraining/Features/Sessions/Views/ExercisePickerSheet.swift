import SwiftUI

public struct ExercisePickerSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme

    public let exercises: [Exercise]
    public let part: SessionPartEnum
    public let onSelect: (Exercise) -> Void

    @State private var searchText: String = ""
    @State private var selectedCategory: ExerciseCategory?

    public init(
        exercises: [Exercise],
        part: SessionPartEnum,
        onSelect: @escaping (Exercise) -> Void
    ) {
        self.exercises = exercises
        self.part = part
        self.onSelect = onSelect
    }

    private var filteredExercises: [Exercise] {
        exercises.filter { ex in
            let matchesCategory = selectedCategory == nil || ex.primaryCategory == selectedCategory
            let query = searchText.trimmingCharacters(in: .whitespacesAndNewlines)
            let matchesSearch = query.isEmpty || ex.name.localizedCaseInsensitiveContains(query) || (ex.primaryMuscleGroup?.localizedCaseInsensitiveContains(query) == true)
            return matchesCategory && matchesSearch
        }
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Category Filter Pills
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        CategoryFilterChip(
                            title: "All",
                            isSelected: selectedCategory == nil,
                            theme: theme
                        ) {
                            selectedCategory = nil
                        }

                        ForEach(ExerciseCategory.allCases) { cat in
                            CategoryFilterChip(
                                title: cat.displayName,
                                isSelected: selectedCategory == cat,
                                theme: theme
                            ) {
                                selectedCategory = (selectedCategory == cat) ? nil : cat
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }

                // Exercise List
                if filteredExercises.isEmpty {
                    ContentUnavailableView(
                        "No Exercises Found",
                        systemImage: "magnifyingglass",
                        description: Text("Try searching with different keywords or categories.")
                    )
                } else {
                    List {
                        ForEach(filteredExercises) { exercise in
                            Button {
                                onSelect(exercise)
                                dismiss()
                            } label: {
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text(exercise.name)
                                            .font(.headline)
                                            .foregroundStyle(Color.primary)
                                        if let muscle = exercise.primaryMuscleGroup {
                                            Text(muscle)
                                                .font(.caption)
                                                .foregroundStyle(Color.secondary)
                                        }
                                    }

                                    Spacer()

                                    Text(exercise.primaryCategory.displayName)
                                        .font(.caption2)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(theme.selectedAccent.color.opacity(0.15))
                                        .foregroundStyle(theme.selectedAccent.color)
                                        .clipShape(Capsule())

                                    Image(systemName: "plus.circle.fill")
                                        .foregroundStyle(theme.selectedAccent.color)
                                }
                                .padding(.vertical, 4)
                            }
                            .buttonStyle(.plain)
                            .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                        }
                    }
                    .scrollContentBackground(.hidden)
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Add to \(part.displayName)")
            .navigationBarTitleDisplayMode(.inline)
            .searchable(text: $searchText, prompt: "Search exercises...")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
}

private struct CategoryFilterChip: View {
    let title: String
    let isSelected: Bool
    let theme: ThemeManager
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.caption)
                .fontWeight(isSelected ? .semibold : .regular)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(isSelected ? theme.selectedAccent.color : theme.surfaceStyle.cardBackgroundColor)
                .foregroundStyle(isSelected ? theme.selectedAccent.badgeTextColor : Color.primary)
                .clipShape(Capsule())
                .overlay(
                    Capsule()
                        .stroke(isSelected ? Color.clear : Color.white.opacity(0.1), lineWidth: 1)
                )
        }
        .buttonStyle(.plain)
    }
}
