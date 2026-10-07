import Testing
import Foundation
@testable import FOSSTraining

@Suite("SPEC-01: Exercise Domain & Validation Tests")
struct ExerciseDomainTests {

    @Test("Valid resistance exercise passes validation")
    func testValidResistanceExerciseValidation() throws {
        let exercise = Exercise(
            id: 1,
            name: "Barbell Squat",
            primaryCategory: .resistance,
            primaryMuscleGroup: "Quadriceps",
            movementPattern: .squat,
            equipmentRequired: [.barbell],
            difficultyLevel: .intermediate
        )

        #expect(throws: Never.self) {
            try exercise.validate()
        }
    }

    @Test("Exercise with name shorter than 2 characters throws nameTooShort")
    func testExerciseNameTooShortThrows() {
        let exercise = Exercise(
            id: 2,
            name: "A",
            primaryCategory: .resistance,
            movementPattern: .squat
        )

        #expect(throws: ExerciseValidationError.nameTooShort) {
            try exercise.validate()
        }

        let emptyExercise = Exercise(
            id: 3,
            name: "   ",
            primaryCategory: .resistance,
            movementPattern: .squat
        )

        #expect(throws: ExerciseValidationError.nameTooShort) {
            try emptyExercise.validate()
        }
    }

    @Test("Resistance exercise missing movement pattern throws missingMovementPattern")
    func testResistanceExerciseMissingMovementPatternThrows() {
        let exercise = Exercise(
            id: 4,
            name: "Cable Fly",
            primaryCategory: .resistance,
            movementPattern: nil
        )

        #expect(throws: ExerciseValidationError.missingMovementPattern) {
            try exercise.validate()
        }
    }

    @Test("Endurance exercise requires endurance type")
    func testEnduranceExerciseValidation() {
        let invalidEndurance = Exercise(
            id: 5,
            name: "Interval Run",
            primaryCategory: .endurance,
            enduranceType: nil
        )

        #expect(throws: ExerciseValidationError.missingEnduranceType) {
            try invalidEndurance.validate()
        }

        let validEndurance = Exercise(
            id: 6,
            name: "Zone 2 Cycling",
            primaryCategory: .endurance,
            enduranceType: "Cycling"
        )

        #expect(throws: Never.self) {
            try validEndurance.validate()
        }
    }

    @Test("Mobility exercise requires mobility type or target joints")
    func testMobilityExerciseValidation() {
        let invalidMobility = Exercise(
            id: 7,
            name: "Hip Opener",
            primaryCategory: .mobility,
            mobilityType: nil,
            targetJoints: []
        )

        #expect(throws: ExerciseValidationError.missingMobilityDetails) {
            try invalidMobility.validate()
        }

        let validMobilityWithJoints = Exercise(
            id: 8,
            name: "Ankle Dorsiflexion Mobilization",
            primaryCategory: .mobility,
            targetJoints: ["Ankle"]
        )

        #expect(throws: Never.self) {
            try validMobilityWithJoints.validate()
        }
    }

    @Test("Soft delete flag defaults to true")
    func testExerciseActiveState() {
        var exercise = Exercise(
            id: 9,
            name: "Romanian Deadlift",
            primaryCategory: .resistance,
            movementPattern: .hinge
        )

        #expect(exercise.isActive == true)
        exercise.isActive = false
        #expect(exercise.isActive == false)
    }
}
