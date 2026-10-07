import Foundation
import SwiftData

@Model
public final class SDExercise {
    @Attribute(.unique) public var id: Int
    public var name: String
    public var exerciseDescription: String?
    public var images: [String]
    public var video: String?
    public var primaryCategoryRaw: String
    public var secondaryCategoriesRaw: [String]
    public var primaryMuscleGroup: String?
    public var secondaryMuscleGroups: [String]
    public var movementPatternRaw: String?
    public var enduranceType: String?
    public var mobilityType: String?
    public var targetJoints: [String]
    public var equipmentRequiredRaw: [String]
    public var difficultyLevelRaw: String
    public var stepByStepInstructions: [String]
    public var commonMistakes: [String]
    public var safetyTips: [String]
    public var tags: [String]
    public var isActive: Bool
    public var createdAt: Date
    public var updatedAt: Date

    public init(
        id: Int,
        name: String,
        exerciseDescription: String? = nil,
        images: [String] = [],
        video: String? = nil,
        primaryCategory: ExerciseCategory,
        secondaryCategories: [ExerciseCategory] = [],
        primaryMuscleGroup: String? = nil,
        secondaryMuscleGroups: [String] = [],
        movementPattern: MovementPattern? = nil,
        enduranceType: String? = nil,
        mobilityType: String? = nil,
        targetJoints: [String] = [],
        equipmentRequired: [EquipmentCategory] = [],
        difficultyLevel: DifficultyLevel = .beginner,
        stepByStepInstructions: [String] = [],
        commonMistakes: [String] = [],
        safetyTips: [String] = [],
        tags: [String] = [],
        isActive: Bool = true,
        createdAt: Date = Date(),
        updatedAt: Date = Date()
    ) {
        self.id = id
        self.name = name
        self.exerciseDescription = exerciseDescription
        self.images = images
        self.video = video
        self.primaryCategoryRaw = primaryCategory.rawValue
        self.secondaryCategoriesRaw = secondaryCategories.map(\.rawValue)
        self.primaryMuscleGroup = primaryMuscleGroup
        self.secondaryMuscleGroups = secondaryMuscleGroups
        self.movementPatternRaw = movementPattern?.rawValue
        self.enduranceType = enduranceType
        self.mobilityType = mobilityType
        self.targetJoints = targetJoints
        self.equipmentRequiredRaw = equipmentRequired.map(\.rawValue)
        self.difficultyLevelRaw = difficultyLevel.rawValue
        self.stepByStepInstructions = stepByStepInstructions
        self.commonMistakes = commonMistakes
        self.safetyTips = safetyTips
        self.tags = tags
        self.isActive = isActive
        self.createdAt = createdAt
        self.updatedAt = updatedAt
    }

    public func toDomain() -> Exercise {
        Exercise(
            id: id,
            name: name,
            description: exerciseDescription,
            images: images,
            video: video,
            primaryCategory: ExerciseCategory(rawValue: primaryCategoryRaw) ?? .resistance,
            secondaryCategories: secondaryCategoriesRaw.compactMap { ExerciseCategory(rawValue: $0) },
            primaryMuscleGroup: primaryMuscleGroup,
            secondaryMuscleGroups: secondaryMuscleGroups,
            movementPattern: movementPatternRaw.flatMap { MovementPattern(rawValue: $0) },
            enduranceType: enduranceType,
            mobilityType: mobilityType,
            targetJoints: targetJoints,
            equipmentRequired: equipmentRequiredRaw.compactMap { EquipmentCategory(rawValue: $0) },
            difficultyLevel: DifficultyLevel(rawValue: difficultyLevelRaw) ?? .beginner,
            stepByStepInstructions: stepByStepInstructions,
            commonMistakes: commonMistakes,
            safetyTips: safetyTips,
            tags: tags,
            isActive: isActive,
            createdAt: createdAt,
            updatedAt: updatedAt
        )
    }

    public static func fromDomain(_ exercise: Exercise) -> SDExercise {
        SDExercise(
            id: exercise.id,
            name: exercise.name,
            exerciseDescription: exercise.description,
            images: exercise.images,
            video: exercise.video,
            primaryCategory: exercise.primaryCategory,
            secondaryCategories: exercise.secondaryCategories,
            primaryMuscleGroup: exercise.primaryMuscleGroup,
            secondaryMuscleGroups: exercise.secondaryMuscleGroups,
            movementPattern: exercise.movementPattern,
            enduranceType: exercise.enduranceType,
            mobilityType: exercise.mobilityType,
            targetJoints: exercise.targetJoints,
            equipmentRequired: exercise.equipmentRequired,
            difficultyLevel: exercise.difficultyLevel,
            stepByStepInstructions: exercise.stepByStepInstructions,
            commonMistakes: exercise.commonMistakes,
            safetyTips: exercise.safetyTips,
            tags: exercise.tags,
            isActive: exercise.isActive,
            createdAt: exercise.createdAt ?? Date(),
            updatedAt: exercise.updatedAt ?? Date()
        )
    }
}
