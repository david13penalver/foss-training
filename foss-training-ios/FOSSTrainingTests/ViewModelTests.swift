import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

@Suite("ViewModel State Transition Tests")
@MainActor
struct ViewModelTests {

    private func createTestContainer() throws -> ModelContainer {
        let schema = Schema([
            SDExercise.self,
            SDSession.self,
            SDSessionExercise.self,
            SDResistanceSet.self,
            SDTraining.self,
            SDBodyweightEntry.self
        ])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: true)
        return try ModelContainer(for: schema, configurations: [config])
    }

    @Test("ExerciseListViewModel loads and filters correctly")
    func testExerciseListViewModel() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        _ = try await repo.saveExercise(Exercise(id: 1, name: "Pull-Up", primaryCategory: .resistance))
        _ = try await repo.saveExercise(Exercise(id: 2, name: "Running", primaryCategory: .endurance))

        let vm = ExerciseListViewModel(exerciseRepository: repo)
        await vm.loadExercises()
        #expect(vm.exercises.count == 2)

        await vm.selectCategory(.endurance)
        #expect(vm.selectedCategory == .endurance)
        #expect(vm.exercises.count == 1)
        #expect(vm.exercises.first?.name == "Running")
    }

    @Test("ActiveWorkoutViewModel toggles set completion and calculates timer")
    func testActiveWorkoutViewModel() async throws {
        let container = try createTestContainer()
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let initialTraining = Training(
            id: 100,
            name: "Upper Body",
            status: .inProgress,
            loggedExercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 80, repetitions: 8, isCompleted: false)]
                )
            ]
        )
        // insert into modelContext
        let sd = SDTraining.fromDomain(initialTraining)
        container.mainContext.insert(sd)
        try container.mainContext.save()

        let vm = ActiveWorkoutViewModel(training: initialTraining, trainingRepository: trainingRepo)

        #expect(vm.isTimerRunning == true)
        vm.tickElapsed()
        #expect(vm.elapsedSeconds == 1)

        await vm.toggleSetCompleted(exerciseId: 1, setNumber: 1)
        #expect(vm.training.loggedExercises.first?.sets.first?.isCompleted == true)
        #expect(vm.isRestTimerActive == true)

        vm.selectedRpe = 9.0
        vm.completionNotes = "Personal record achieved"
        await vm.finishWorkout()

        #expect(vm.isFinished == true)
        #expect(vm.training.status == .completed)
        #expect(vm.training.overallRpe == 9.0)
    }

    @Test("ExerciseListViewModel: delete exercise")
    func testExerciseListViewModelDelete() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        _ = try await repo.saveExercise(Exercise(id: 10, name: "Leg Press", primaryCategory: .resistance))
        let vm = ExerciseListViewModel(exerciseRepository: repo)
        await vm.loadExercises()
        #expect(vm.exercises.count == 1)

        await vm.deleteExercise(id: 10)
        #expect(vm.exercises.isEmpty)

        let reloaded = try await repo.getExercises(category: nil, search: nil)
        #expect(reloaded.isEmpty)
    }

    @Test("ExerciseEditorViewModel: create valid exercise")
    func testExerciseEditorViewModelCreateValid() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let vm = ExerciseEditorViewModel(exerciseRepository: repo)
        vm.name = "Incline Dumbbell Press"
        vm.primaryCategory = .resistance
        vm.movementPattern = .push
        vm.primaryMuscleGroup = "Upper Chest"
        vm.newInstructionText = "Set bench to 30 degrees"
        vm.addInstruction()

        let success = await vm.save()
        #expect(success == true)
        #expect(vm.errorMessage == nil)

        let saved = try await repo.getExercises(category: .resistance, search: "Incline")
        #expect(saved.count == 1)
        #expect(saved.first?.name == "Incline Dumbbell Press")
        #expect(saved.first?.stepByStepInstructions.count == 1)
    }

    @Test("ExerciseEditorViewModel: validation failure on empty name")
    func testExerciseEditorViewModelValidationFailure() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let vm = ExerciseEditorViewModel(exerciseRepository: repo)
        vm.name = ""
        vm.primaryCategory = .resistance
        vm.movementPattern = .push

        let success = await vm.save()
        #expect(success == false)
        #expect(vm.errorMessage != nil)
    }
}

