import Testing
import Foundation
@testable import FOSSTraining

@Suite("SPEC-02: Session Domain & Validation Tests")
struct SessionDomainTests {

    private func createValidSession() -> Session {
        let sets = [
            ResistanceSet(setNumber: 1, setType: .warmUp, weightKg: 60, repetitions: 10, restSeconds: 60),
            ResistanceSet(setNumber: 2, setType: .normal, weightKg: 100, repetitions: 5, restSeconds: 120)
        ]
        let exercise = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 1,
            exerciseName: "Barbell Bench Press",
            part: .main,
            restSeconds: 90,
            sets: sets
        )
        return Session(
            id: 10,
            name: "Push Hypertrophy",
            description: "Chest, shoulders, triceps",
            notes: "Keep shoulders retracted",
            estimatedDurationMinutes: 60,
            exercises: [exercise]
        )
    }

    @Test("Valid session passes domain validation")
    func testValidSessionValidation() throws {
        let session = createValidSession()
        #expect(throws: Never.self) {
            try session.validate()
        }
    }

    @Test("Session with empty or short name throws nameTooShort")
    func testSessionNameTooShort() {
        var s1 = createValidSession()
        s1.name = "A"
        #expect(throws: SessionValidationError.nameTooShort) {
            try s1.validate()
        }

        var s2 = createValidSession()
        s2.name = "   "
        #expect(throws: SessionValidationError.nameTooShort) {
            try s2.validate()
        }
    }

    @Test("Session with name longer than 100 characters throws nameTooLong")
    func testSessionNameTooLong() {
        var session = createValidSession()
        session.name = String(repeating: "W", count: 101)
        #expect(throws: SessionValidationError.nameTooLong) {
            try session.validate()
        }
    }

    @Test("Session with duration <= 0 throws invalidDuration")
    func testSessionInvalidDuration() {
        var s1 = createValidSession()
        s1.estimatedDurationMinutes = 0
        #expect(throws: SessionValidationError.invalidDuration) {
            try s1.validate()
        }

        var s2 = createValidSession()
        s2.estimatedDurationMinutes = -15
        #expect(throws: SessionValidationError.invalidDuration) {
            try s2.validate()
        }

        // nil duration is permitted
        var s3 = createValidSession()
        s3.estimatedDurationMinutes = nil
        #expect(throws: Never.self) {
            try s3.validate()
        }
    }

    @Test("Session with no exercises throws noExercises")
    func testSessionNoExercises() {
        var session = createValidSession()
        session.exercises = []
        #expect(throws: SessionValidationError.noExercises) {
            try session.validate()
        }
    }

    @Test("Session with exercise having zero sets throws missingSets")
    func testSessionMissingSets() {
        var session = createValidSession()
        let emptyExercise = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 2,
            exerciseName: "Incline Dumbbell Press",
            part: .main,
            restSeconds: 90,
            sets: []
        )
        session.exercises.append(emptyExercise)

        #expect(throws: SessionValidationError.missingSets(exerciseName: "Incline Dumbbell Press")) {
            try session.validate()
        }
    }

    @Test("SessionValidationError errorDescription strings")
    func testValidationErrorDescriptions() {
        #expect(SessionValidationError.nameTooShort.errorDescription != nil)
        #expect(SessionValidationError.nameTooLong.errorDescription != nil)
        #expect(SessionValidationError.invalidDuration.errorDescription != nil)
        #expect(SessionValidationError.noExercises.errorDescription != nil)
        #expect(SessionValidationError.missingSets(exerciseName: "Squat").errorDescription?.contains("Squat") == true)
    }

    @Test("Session part filtering (warmUp, main, coolDown) and total sets")
    func testSessionPartFilteringAndAggregates() {
        let warmUp1 = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 1,
            exerciseName: "Arm Circles",
            part: .warmUp,
            sets: [ResistanceSet(setNumber: 1, setType: .warmUp, weightKg: 0, repetitions: 15)]
        )
        let warmUp2 = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 5,
            exerciseName: "Neck Rolls",
            part: .warmUp,
            sets: [ResistanceSet(setNumber: 1, setType: .warmUp, weightKg: 0, repetitions: 10)]
        )
        let main1 = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 2,
            exerciseName: "Overhead Press",
            part: .main,
            sets: [
                ResistanceSet(setNumber: 1, setType: .normal, weightKg: 50, repetitions: 8),
                ResistanceSet(setNumber: 2, setType: .normal, weightKg: 50, repetitions: 8)
            ]
        )
        let main2 = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 3,
            exerciseName: "Lateral Raise",
            part: .main,
            sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 12, repetitions: 12)]
        )
        let coolDown1 = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 4,
            exerciseName: "Doorway Stretch",
            part: .coolDown,
            sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 0, repetitions: 1)]
        )
        let coolDown2 = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 6,
            exerciseName: "Hamstring Stretch",
            part: .coolDown,
            sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 0, repetitions: 1)]
        )

        let session = Session(
            id: 20,
            name: "Upper Mobility & Power",
            exercises: [warmUp1, warmUp2, main1, main2, coolDown1, coolDown2]
        )

        #expect(session.warmUpExercises.count == 2)
        #expect(session.warmUpExercises.first?.exerciseName == "Neck Rolls")
        #expect(session.warmUpExercises.last?.exerciseName == "Arm Circles")

        #expect(session.mainExercises.count == 2)
        // Should sort by orderIndex: main2 (0) before main1 (1)
        #expect(session.mainExercises.first?.exerciseName == "Lateral Raise")
        #expect(session.mainExercises.last?.exerciseName == "Overhead Press")

        #expect(session.coolDownExercises.count == 2)
        #expect(session.coolDownExercises.first?.exerciseName == "Hamstring Stretch")
        #expect(session.coolDownExercises.last?.exerciseName == "Doorway Stretch")

        #expect(session.totalSetsCount == 7)
        #expect(main1.totalVolumeKg == 800.0)
    }
}
