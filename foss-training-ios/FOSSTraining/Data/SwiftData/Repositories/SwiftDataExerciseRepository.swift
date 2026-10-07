import Foundation
import SwiftData

@MainActor
public final class SwiftDataExerciseRepository: ExerciseRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func getExercises(category: ExerciseCategory?, search: String?) async throws -> [Exercise] {
        let descriptor = FetchDescriptor<SDExercise>(
            predicate: #Predicate<SDExercise> { $0.isActive },
            sortBy: [SortDescriptor(\.name)]
        )

        let allSD = try modelContext.fetch(descriptor)
        var filtered = allSD.map { $0.toDomain() }

        if let category = category {
            filtered = filtered.filter { $0.primaryCategory == category || $0.secondaryCategories.contains(category) }
        }

        if let search = search, !search.trimmingCharacters(in: .whitespaces).isEmpty {
            let lower = search.lowercased()
            filtered = filtered.filter { exercise in
                exercise.name.lowercased().contains(lower) ||
                (exercise.primaryMuscleGroup?.lowercased().contains(lower) ?? false) ||
                exercise.secondaryMuscleGroups.contains { $0.lowercased().contains(lower) } ||
                exercise.tags.contains { $0.lowercased().contains(lower) }
            }
        }

        return filtered
    }

    public func getExercise(id: Int) async throws -> Exercise? {
        let descriptor = FetchDescriptor<SDExercise>(predicate: #Predicate<SDExercise> { $0.id == id })
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func saveExercise(_ exercise: Exercise) async throws -> Exercise {
        let descriptor = FetchDescriptor<SDExercise>(predicate: #Predicate<SDExercise> { $0.id == exercise.id })
        if let existing = try modelContext.fetch(descriptor).first {
            existing.name = exercise.name
            existing.exerciseDescription = exercise.description
            existing.primaryCategoryRaw = exercise.primaryCategory.rawValue
            existing.secondaryCategoriesRaw = exercise.secondaryCategories.map(\.rawValue)
            existing.primaryMuscleGroup = exercise.primaryMuscleGroup
            existing.secondaryMuscleGroups = exercise.secondaryMuscleGroups
            existing.movementPatternRaw = exercise.movementPattern?.rawValue
            existing.equipmentRequiredRaw = exercise.equipmentRequired.map(\.rawValue)
            existing.difficultyLevelRaw = exercise.difficultyLevel.rawValue
            existing.tags = exercise.tags
            existing.updatedAt = Date()
            try modelContext.save()
            return existing.toDomain()
        } else {
            let newSD = SDExercise.fromDomain(exercise)
            modelContext.insert(newSD)
            try modelContext.save()
            return newSD.toDomain()
        }
    }

    public func deleteExercise(id: Int) async throws {
        let descriptor = FetchDescriptor<SDExercise>(predicate: #Predicate<SDExercise> { $0.id == id })
        if let existing = try modelContext.fetch(descriptor).first {
            existing.isActive = false
            existing.updatedAt = Date()
            try modelContext.save()
        }
    }
}
