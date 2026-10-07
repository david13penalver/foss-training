import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

@Suite("SwiftData Repository Tests")
@MainActor
struct SwiftDataRepositoryTests {

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

    @Test("SwiftDataExerciseRepository: Save, Fetch, Filter, Soft Delete")
    func testExerciseRepositoryCRUD() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let exercise = Exercise(
            id: 201,
            name: "Incline Dumbbell Press",
            primaryCategory: .resistance,
            primaryMuscleGroup: "Upper Chest",
            movementPattern: .push,
            difficultyLevel: .intermediate
        )

        _ = try await repo.saveExercise(exercise)

        let fetched = try await repo.getExercise(id: 201)
        #expect(fetched != nil)
        #expect(fetched?.name == "Incline Dumbbell Press")

        let resistanceOnly = try await repo.getExercises(category: .resistance, search: nil)
        #expect(resistanceOnly.contains { $0.id == 201 })

        let mobilityOnly = try await repo.getExercises(category: .mobility, search: nil)
        #expect(!mobilityOnly.contains { $0.id == 201 })

        try await repo.deleteExercise(id: 201)
        let afterDelete = try await repo.getExercises(category: nil, search: nil)
        #expect(!afterDelete.contains { $0.id == 201 })
    }

    @Test("SwiftDataSessionRepository: Save, Fetch, Clone")
    func testSessionRepository() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataSessionRepository(modelContext: container.mainContext)

        let session = Session(
            id: 10,
            name: "Push Day Template",
            estimatedDurationMinutes: 45,
            exercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 80, repetitions: 8)]
                )
            ]
        )

        _ = try await repo.saveSession(session)
        let list = try await repo.getSessions()
        #expect(list.count == 1)
        #expect(list.first?.name == "Push Day Template")

        let cloned = try await repo.cloneSession(id: 10)
        #expect(cloned.name == "Push Day Template (Copy)")
        #expect(cloned.id != 10)

        let updatedList = try await repo.getSessions()
        #expect(updatedList.count == 2)
    }

    @Test("SwiftDataTrainingRepository: Create from session, Start, Log sets, Complete")
    func testTrainingRepositoryLifecycle() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let template = Session(
            id: 5,
            name: "Leg Workout",
            exercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 10,
                    exerciseName: "Squat",
                    sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5)]
                )
            ]
        )
        _ = try await sessionRepo.saveSession(template)

        // 1. Create training from session
        var training = try await trainingRepo.createTrainingFromSession(sessionId: 5)
        #expect(training.status == .planned)
        #expect(training.loggedExercises.count == 1)

        // 2. Start training
        training = try await trainingRepo.startTraining(id: training.id)
        #expect(training.status == .inProgress)
        #expect(training.startTime != nil)

        // 3. Log a set
        let newSet = ResistanceSet(setNumber: 2, setType: .normal, weightKg: 105, repetitions: 5, isCompleted: true)
        _ = try await trainingRepo.logSet(trainingId: training.id, exerciseId: 10, set: newSet)

        let reloaded = try await trainingRepo.getTraining(id: training.id)
        #expect(reloaded?.loggedExercises.first?.sets.count == 2)

        // 4. Complete training
        let completed = try await trainingRepo.completeTraining(id: training.id, overallRpe: 8.5, notes: "Felt strong")
        #expect(completed.status == .completed)
        #expect(completed.overallRpe == 8.5)
        #expect(completed.notes == "Felt strong")
    }
}
