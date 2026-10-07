import SwiftUI

@Observable
@MainActor
public final class ExerciseListViewModel {
    private let exerciseRepository: ExerciseRepository

    public var exercises: [Exercise] = []
    public var selectedCategory: ExerciseCategory? = nil
    public var searchText: String = ""
    public var isLoading: Bool = false
    public var errorMessage: String?

    public init(exerciseRepository: ExerciseRepository) {
        self.exerciseRepository = exerciseRepository
    }

    public func loadExercises() async {
        isLoading = true
        errorMessage = nil
        do {
            self.exercises = try await exerciseRepository.getExercises(category: selectedCategory, search: searchText)
        } catch {
            self.errorMessage = error.localizedDescription
        }
        isLoading = false
    }

    public func selectCategory(_ category: ExerciseCategory?) async {
        if selectedCategory == category {
            selectedCategory = nil
        } else {
            selectedCategory = category
        }
        await loadExercises()
    }
}
