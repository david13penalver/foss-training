import Foundation

public struct Exercise: Identifiable, Codable, Hashable, Sendable {
    public let id: Int
    public var name: String
    public var description: String?
    public var images: [String]
    public var video: String?
    public var primaryCategory: ExerciseCategory
    public var secondaryCategories: [ExerciseCategory]

    // Category specifics
    public var primaryMuscleGroup: String?
    public var secondaryMuscleGroups: [String]
    public var movementPattern: MovementPattern?
    public var enduranceType: String?
    public var mobilityType: String?
    public var targetJoints: [String]

    // Equipment and difficulty
    public var equipmentRequired: [EquipmentCategory]
    public var difficultyLevel: DifficultyLevel

    // Instructions
    public var stepByStepInstructions: [String]
    public var commonMistakes: [String]
    public var safetyTips: [String]
    public var tags: [String]
    public var isActive: Bool
    public var createdAt: Date?
    public var updatedAt: Date?

    public init(
        id: Int,
        name: String,
        description: String? = nil,
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
        createdAt: Date? = nil,
        updatedAt: Date? = nil
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.images = images
        self.video = video
        self.primaryCategory = primaryCategory
        self.secondaryCategories = secondaryCategories
        self.primaryMuscleGroup = primaryMuscleGroup
        self.secondaryMuscleGroups = secondaryMuscleGroups
        self.movementPattern = movementPattern
        self.enduranceType = enduranceType
        self.mobilityType = mobilityType
        self.targetJoints = targetJoints
        self.equipmentRequired = equipmentRequired
        self.difficultyLevel = difficultyLevel
        self.stepByStepInstructions = stepByStepInstructions
        self.commonMistakes = commonMistakes
        self.safetyTips = safetyTips
        self.tags = tags
        self.isActive = isActive
        self.createdAt = createdAt
        self.updatedAt = updatedAt
    }

    public func validate() throws(ExerciseValidationError) {
        if name.trimmingCharacters(in: .whitespacesAndNewlines).count < 2 {
            throw .nameTooShort
        }

        switch primaryCategory {
        case .resistance:
            if movementPattern == nil {
                throw .missingMovementPattern
            }
        case .endurance:
            if enduranceType == nil || enduranceType?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty == true {
                throw .missingEnduranceType
            }
        case .mobility:
            let hasMobilityType = mobilityType != nil && !mobilityType!.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
            let hasTargetJoints = !targetJoints.isEmpty
            if !hasMobilityType && !hasTargetJoints {
                throw .missingMobilityDetails
            }
        }
    }
}

public enum ExerciseValidationError: LocalizedError, Equatable, Sendable {
    case nameTooShort
    case missingMovementPattern
    case missingEnduranceType
    case missingMobilityDetails

    public var errorDescription: String? {
        switch self {
        case .nameTooShort:
            return "Exercise name must be at least 2 characters long."
        case .missingMovementPattern:
            return "Resistance exercises require a movement pattern."
        case .missingEnduranceType:
            return "Endurance exercises require an endurance type."
        case .missingMobilityDetails:
            return "Mobility exercises require target joints or a mobility type."
        }
    }
}
