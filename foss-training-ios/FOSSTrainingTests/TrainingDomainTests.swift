import Testing
import Foundation
@testable import FOSSTraining

@Suite("SPEC-03: Training Domain State Machine & Volume Invariant Tests")
struct TrainingDomainTests {

    private func createPlannedTraining() -> Training {
        let sets = [
            ResistanceSet(setNumber: 1, setType: .warmUp, weightKg: 60, repetitions: 10, restSeconds: 60, isCompleted: false),
            ResistanceSet(setNumber: 2, setType: .normal, weightKg: 100, repetitions: 5, restSeconds: 120, isCompleted: false)
        ]
        let exercise = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 10,
            exerciseName: "Barbell Squat",
            part: .main,
            restSeconds: 120,
            sets: sets
        )
        return Training(
            id: 1,
            name: "Leg Day Alpha",
            description: "Heavy compound squat focus",
            trainingDate: Date(),
            status: .planned,
            loggedExercises: [exercise]
        )
    }

    // MARK: - Lifecycle State Machine Transitions

    @Test("Starting a PLANNED training transitions to IN_PROGRESS and sets startTime")
    func testStartWorkoutTransition() throws {
        var training = createPlannedTraining()
        #expect(training.status == .planned)
        #expect(training.startTime == nil)

        let startDate = Date(timeIntervalSince1970: 1000)
        try training.start(at: startDate)

        #expect(training.status == .inProgress)
        #expect(training.startTime == startDate)
    }

    @Test("Pausing and resuming toggles between PAUSED and IN_PROGRESS without resetting startTime")
    func testPauseAndResumeWorkout() throws {
        var training = createPlannedTraining()
        let startDate = Date(timeIntervalSince1970: 1000)
        try training.start(at: startDate)

        try training.pause()
        #expect(training.status == .paused)
        #expect(training.startTime == startDate)

        try training.resume()
        #expect(training.status == .inProgress)
        #expect(training.startTime == startDate)
    }

    @Test("Completing workout transitions to COMPLETED, sets endTime, records overallRpe and notes")
    func testCompleteWorkoutTransition() throws {
        var training = createPlannedTraining()
        let startDate = Date(timeIntervalSince1970: 1000)
        let endDate = Date(timeIntervalSince1970: 4600) // 3600s later
        try training.start(at: startDate)

        try training.complete(rpe: 8.5, notes: "Felt strong on all sets", at: endDate)
        #expect(training.status == .completed)
        #expect(training.endTime == endDate)
        #expect(training.overallRpe == 8.5)
        #expect(training.notes == "Felt strong on all sets")
        #expect(training.durationSeconds == 3600)
    }

    @Test("Completing from PAUSED state is permitted")
    func testCompleteFromPaused() throws {
        var training = createPlannedTraining()
        let startDate = Date(timeIntervalSince1970: 1000)
        let endDate = Date(timeIntervalSince1970: 2000)
        try training.start(at: startDate)
        try training.pause()

        try training.complete(rpe: 7.0, at: endDate)
        #expect(training.status == .completed)
        #expect(training.endTime == endDate)
    }

    @Test("Cancelling workout sets status to CANCELLED and records endTime")
    func testCancelWorkout() throws {
        var training = createPlannedTraining()
        let cancelDate = Date(timeIntervalSince1970: 500)
        try training.cancel(at: cancelDate)

        #expect(training.status == .cancelled)
        #expect(training.endTime == cancelDate)
    }

    // MARK: - Invalid State Transitions

    @Test("Invalid transitions throw TrainingStateError.invalidTransition")
    func testInvalidStateTransitions() {
        var planned = createPlannedTraining()

        // Cannot pause or resume a planned workout
        #expect(throws: TrainingStateError.invalidTransition(from: .planned, to: .paused)) {
            try planned.pause()
        }
        #expect(throws: TrainingStateError.invalidTransition(from: .planned, to: .inProgress)) {
            try planned.resume()
        }
        #expect(throws: TrainingStateError.invalidTransition(from: .planned, to: .completed)) {
            try planned.complete(rpe: 8.0)
        }

        // Complete a training, then verify terminal state protections
        var completedTraining = createPlannedTraining()
        try! completedTraining.start()
        try! completedTraining.complete(rpe: 8.0)

        #expect(throws: TrainingStateError.invalidTransition(from: .completed, to: .inProgress)) {
            try completedTraining.start()
        }
        #expect(throws: TrainingStateError.invalidTransition(from: .completed, to: .paused)) {
            try completedTraining.pause()
        }
        #expect(throws: TrainingStateError.invalidTransition(from: .completed, to: .completed)) {
            try completedTraining.complete(rpe: 8.0)
        }
        #expect(throws: TrainingStateError.invalidTransition(from: .completed, to: .cancelled)) {
            try completedTraining.cancel()
        }

        // Cancel a training, then verify terminal state protections
        var cancelledTraining = createPlannedTraining()
        try! cancelledTraining.cancel()

        #expect(throws: TrainingStateError.invalidTransition(from: .cancelled, to: .inProgress)) {
            try cancelledTraining.start()
        }
    }

    @Test("RPE validation rejects values < 1.0 or > 10.0")
    func testRpeValidation() throws {
        var training = createPlannedTraining()
        try training.start()

        #expect(throws: TrainingStateError.invalidRpe(value: 0.5)) {
            try training.complete(rpe: 0.5)
        }

        #expect(throws: TrainingStateError.invalidRpe(value: 10.5)) {
            try training.complete(rpe: 10.5)
        }
    }

    // MARK: - Volume Invariant & Set Logging

    @Test("Strict volume calculation: incomplete sets contribute zero kg")
    func testVolumeInvariants() {
        var training = createPlannedTraining()
        #expect(training.totalVolumeKg == 0.0)
        #expect(training.totalCompletedSets == 0)
        #expect(training.totalSets == 2)
        #expect(training.completionPercentage == 0.0)

        // Complete first set: 60kg * 10 reps = 600kg
        training.toggleSet(exerciseId: 10, setNumber: 1)
        #expect(training.totalVolumeKg == 600.0)
        #expect(training.totalCompletedSets == 1)
        #expect(training.completionPercentage == 50.0)

        // Complete second set: 100kg * 5 reps = 500kg -> Total: 1100kg
        training.toggleSet(exerciseId: 10, setNumber: 2)
        #expect(training.totalVolumeKg == 1100.0)
        #expect(training.totalCompletedSets == 2)
        #expect(training.completionPercentage == 100.0)

        // Untoggle first set
        training.toggleSet(exerciseId: 10, setNumber: 1)
        #expect(training.totalVolumeKg == 500.0)
        #expect(training.totalCompletedSets == 1)
    }

    @Test("Set manipulation: add, update, and remove sets")
    func testSetManipulation() {
        var training = createPlannedTraining()

        // Update set
        let updatedSet = ResistanceSet(
            setNumber: 1,
            setType: .dropSet,
            weightKg: 70,
            repetitions: 12,
            isCompleted: true
        )
        training.updateSet(exerciseId: 10, set: updatedSet)
        #expect(training.loggedExercises[0].sets[0].weightKg == 70)
        #expect(training.loggedExercises[0].sets[0].repetitions == 12)
        #expect(training.loggedExercises[0].sets[0].setType == .dropSet)

        // Add set
        let newSet = ResistanceSet(setNumber: 3, weightKg: 110, repetitions: 3, isCompleted: true)
        training.addSet(to: 10, set: newSet)
        #expect(training.totalSets == 3)
        #expect(training.loggedExercises[0].sets.count == 3)

        // Delete set (with re-indexing)
        training.removeSet(exerciseId: 10, setNumber: 2)
        #expect(training.totalSets == 2)
        #expect(training.loggedExercises[0].sets[1].setNumber == 2)

        // Graceful no-ops on non-existent exercise
        training.toggleSet(exerciseId: 999, setNumber: 1)
        training.updateSet(exerciseId: 999, set: updatedSet)
        training.addSet(to: 999, set: newSet)
        training.removeSet(exerciseId: 999, setNumber: 1)
    }

    @Test("Formatted duration helper formatting")
    func testFormattedDuration() throws {
        var training = createPlannedTraining()
        #expect(training.durationSeconds == 0)
        #expect(training.formattedDuration == "00:00:00")

        let start = Date().addingTimeInterval(-100)
        try training.start(at: start)
        #expect(training.durationSeconds >= 100)

        let fixedStart = Date(timeIntervalSince1970: 1000)
        let fixedEnd = Date(timeIntervalSince1970: 4625) // 3625s = 1h 0m 25s
        training.startTime = fixedStart
        try training.complete(rpe: 8.0, at: fixedEnd)

        #expect(training.durationSeconds == 3625)
        #expect(training.formattedDuration == "01:00:25")
    }

    @Test("TrainingStateError descriptions")
    func testErrorDescriptions() {
        let err1 = TrainingStateError.invalidTransition(from: .planned, to: .completed)
        #expect(err1.errorDescription?.contains("Scheduled") == true)
        #expect(err1.errorDescription?.contains("Completed") == true)

        let err2 = TrainingStateError.invalidRpe(value: 12.0)
        #expect(err2.errorDescription?.contains("12.0") == true)

        let err3 = TrainingStateError.emptyWorkout
        #expect(err3.errorDescription?.contains("no logged exercises") == true)
    }
}
