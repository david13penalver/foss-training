import Testing
import Foundation
@testable import FOSSTraining

@Suite("Domain Model & Business Invariant Tests")
struct DomainTests {

    @Test("Exercise creation and default properties")
    func testExerciseCreation() {
        let exercise = Exercise(
            id: 101,
            name: "Barbell Deadlift",
            primaryCategory: .resistance,
            primaryMuscleGroup: "Hamstrings",
            movementPattern: .hinge,
            equipmentRequired: [.barbell],
            difficultyLevel: .advanced
        )

        #expect(exercise.id == 101)
        #expect(exercise.name == "Barbell Deadlift")
        #expect(exercise.primaryCategory == .resistance)
        #expect(exercise.movementPattern == .hinge)
        #expect(exercise.isActive == true)
        #expect(exercise.difficultyLevel == .advanced)
    }

    @Test("ResistanceSet volume calculation: weight * reps")
    func testResistanceSetVolume() {
        let set = ResistanceSet(
            setNumber: 1,
            setType: .normal,
            weightKg: 100.0,
            repetitions: 8,
            rpe: 8.5
        )

        #expect(set.volumeKg == 800.0)
        #expect(set.isCompleted == false)
    }

    @Test("Training status transition permissions")
    func testTrainingStatusTransitions() {
        #expect(TrainingStatus.planned.canStart == true)
        #expect(TrainingStatus.paused.canStart == true)
        #expect(TrainingStatus.inProgress.canStart == false)

        #expect(TrainingStatus.inProgress.canComplete == true)
        #expect(TrainingStatus.paused.canComplete == true)
        #expect(TrainingStatus.completed.canComplete == false)
    }

    @Test("Training aggregate volume and completed sets")
    func testTrainingAggregates() {
        let sets = [
            ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5, isCompleted: true),
            ResistanceSet(setNumber: 2, setType: .normal, weightKg: 100, repetitions: 5, isCompleted: true),
            ResistanceSet(setNumber: 3, setType: .normal, weightKg: 100, repetitions: 5, isCompleted: false)
        ]

        let exerciseItem = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 1,
            exerciseName: "Squat",
            sets: sets
        )

        let training = Training(
            id: 1,
            name: "Leg Day",
            loggedExercises: [exerciseItem]
        )

        #expect(training.totalSets == 3)
        #expect(training.totalCompletedSets == 2)
        // 100*5 + 100*5 = 1000kg (set 3 is incomplete so doesn't count towards completed volume)
        #expect(training.totalVolumeKg == 1000.0)
    }
}
