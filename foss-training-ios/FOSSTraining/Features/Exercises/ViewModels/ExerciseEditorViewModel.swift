import SwiftUI

@Observable
@MainActor
public final class ExerciseEditorViewModel {
    private let exerciseRepository: ExerciseRepository
    public let existingExerciseId: Int?

    public var name: String = ""
    public var descriptionText: String = ""
    public var primaryCategory: ExerciseCategory = .resistance
    public var primaryMuscleGroup: String = ""
    public var secondaryMuscleGroupsText: String = ""
    public var movementPattern: MovementPattern? = .squat
    public var enduranceType: String = ""
    public var mobilityType: String = ""
    public var targetJointsText: String = ""
    public var selectedEquipment: Set<EquipmentCategory> = [.barbell]
    public var difficultyLevel: DifficultyLevel = .intermediate
    public var stepByStepInstructions: [String] = []
    public var newInstructionText: String = ""

    public var isSaving: Bool = false
    public var errorMessage: String? = nil

    public init(exerciseRepository: ExerciseRepository, exerciseToEdit: Exercise? = nil) {
        self.exerciseRepository = exerciseRepository
        self.existingExerciseId = exerciseToEdit?.id

        if let ex = exerciseToEdit {
            self.name = ex.name
            self.descriptionText = ex.description ?? ""
            self.primaryCategory = ex.primaryCategory
            self.primaryMuscleGroup = ex.primaryMuscleGroup ?? ""
            self.secondaryMuscleGroupsText = ex.secondaryMuscleGroups.joined(separator: ", ")
            self.movementPattern = ex.movementPattern
            self.enduranceType = ex.enduranceType ?? ""
            self.mobilityType = ex.mobilityType ?? ""
            self.targetJointsText = ex.targetJoints.joined(separator: ", ")
            self.selectedEquipment = Set(ex.equipmentRequired)
            self.difficultyLevel = ex.difficultyLevel
            self.stepByStepInstructions = ex.stepByStepInstructions
        }
    }

    public func addInstruction() {
        let trimmed = newInstructionText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        stepByStepInstructions.append(trimmed)
        newInstructionText = ""
    }

    public func removeInstruction(at offsets: IndexSet) {
        stepByStepInstructions.remove(atOffsets: offsets)
    }

    public func save() async -> Bool {
        isSaving = true
        errorMessage = nil

        let secondaryMuscles = secondaryMuscleGroupsText
            .split(separator: ",")
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }

        let targetJoints = targetJointsText
            .split(separator: ",")
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }

        let exercise = Exercise(
            id: existingExerciseId ?? Int(Date().timeIntervalSince1970),
            name: name.trimmingCharacters(in: .whitespacesAndNewlines),
            description: descriptionText.isEmpty ? nil : descriptionText,
            primaryCategory: primaryCategory,
            primaryMuscleGroup: primaryMuscleGroup.isEmpty ? nil : primaryMuscleGroup,
            secondaryMuscleGroups: secondaryMuscles,
            movementPattern: primaryCategory == .resistance ? movementPattern : nil,
            enduranceType: primaryCategory == .endurance ? (enduranceType.isEmpty ? nil : enduranceType) : nil,
            mobilityType: primaryCategory == .mobility ? (mobilityType.isEmpty ? nil : mobilityType) : nil,
            targetJoints: targetJoints,
            equipmentRequired: Array(selectedEquipment),
            difficultyLevel: difficultyLevel,
            stepByStepInstructions: stepByStepInstructions,
            isActive: true
        )

        do {
            try exercise.validate()
            _ = try await exerciseRepository.saveExercise(exercise)
            isSaving = false
            return true
        } catch let err as ExerciseValidationError {
            self.errorMessage = err.localizedDescription
            self.isSaving = false
            return false
        } catch {
            self.errorMessage = error.localizedDescription
            self.isSaving = false
            return false
        }
    }
}
