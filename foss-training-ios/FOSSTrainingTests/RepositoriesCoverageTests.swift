import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

@Suite("SwiftData Repositories Branch & Edge Case Coverage Tests")
@MainActor
struct RepositoriesCoverageTests {

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

    @Test("SwiftDataExerciseRepository: update existing exercise, secondary search, tags search, muscle search, not found delete")
    func testExerciseRepositoryBranches() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        var exercise = Exercise(
            id: 10,
            name: "Cable Crossover",
            primaryCategory: .resistance,
            secondaryCategories: [.endurance],
            primaryMuscleGroup: "Chest",
            secondaryMuscleGroups: ["Triceps"],
            movementPattern: .push,
            tags: ["PecFly"]
        )

        _ = try await repo.saveExercise(exercise)

        // Update existing exercise
        exercise.name = "Cable Crossover Updated"
        exercise.description = "Updated description"
        _ = try await repo.saveExercise(exercise)

        let fetched = try await repo.getExercise(id: 10)
        #expect(fetched?.name == "Cable Crossover Updated")
        #expect(fetched?.description == "Updated description")

        // Search by primary muscle
        let chestSearch = try await repo.getExercises(category: nil, search: "Chest")
        #expect(!chestSearch.isEmpty)

        // Search by secondary muscle
        let tricepsSearch = try await repo.getExercises(category: nil, search: "Triceps")
        #expect(!tricepsSearch.isEmpty)

        // Search by tag
        let tagSearch = try await repo.getExercises(category: nil, search: "PecFly")
        #expect(!tagSearch.isEmpty)

        // Filter by secondary category
        let endSearch = try await repo.getExercises(category: .endurance, search: nil)
        #expect(!endSearch.isEmpty)

        // Delete non-existent ID
        try await repo.deleteExercise(id: 9999)

        // Not found exercise
        let notFound = try await repo.getExercise(id: 9999)
        #expect(notFound == nil)
    }

    @Test("SwiftDataSessionRepository: update existing session, delete session, not found cases")
    func testSessionRepositoryBranches() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataSessionRepository(modelContext: container.mainContext)

        var session = Session(
            id: 20,
            name: "Push Day",
            exercises: [
                SessionExerciseItem(orderIndex: 0, exerciseId: 1, exerciseName: "Bench")
            ]
        )

        _ = try await repo.saveSession(session)

        // Update existing
        session.name = "Heavy Push Day"
        _ = try await repo.saveSession(session)

        let fetched = try await repo.getSession(id: 20)
        #expect(fetched?.name == "Heavy Push Day")

        // Delete existing session
        try await repo.deleteSession(id: 20)
        let deleted = try await repo.getSession(id: 20)
        #expect(deleted == nil)

        // Delete non-existent session
        try await repo.deleteSession(id: 9999)
    }

    @Test("SwiftDataTrainingRepository: pause, resume, cancel, complete with notes, set updates, delete set, error handling")
    func testTrainingRepositoryBranches() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        // createTrainingFromSession 404 error
        await #expect(throws: Error.self) {
            try await trainingRepo.createTrainingFromSession(sessionId: 999)
        }

        // Create valid session template
        let session = Session(
            id: 30,
            name: "Leg Day",
            exercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 101,
                    exerciseName: "Squat",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 100, repetitions: 5)]
                )
            ]
        )
        _ = try await sessionRepo.saveSession(session)

        let training = try await trainingRepo.createTrainingFromSession(sessionId: 30)

        // start
        _ = try await trainingRepo.startTraining(id: training.id)

        // pause
        let paused = try await trainingRepo.pauseTraining(id: training.id)
        #expect(paused.status == .paused)

        // resume
        let resumed = try await trainingRepo.resumeTraining(id: training.id)
        #expect(resumed.status == .inProgress)

        // logSet
        let newSet = ResistanceSet(setNumber: 2, weightKg: 105, repetitions: 5)
        _ = try await trainingRepo.logSet(trainingId: training.id, exerciseId: 101, set: newSet)

        // logSet with wrong exerciseId throws
        await #expect(throws: Error.self) {
            try await trainingRepo.logSet(trainingId: training.id, exerciseId: 999, set: newSet)
        }

        // updateSet
        var updatedSet = newSet
        updatedSet.weightKg = 110
        updatedSet.isCompleted = true
        _ = try await trainingRepo.updateSet(trainingId: training.id, exerciseId: 101, set: updatedSet)

        // updateSet not found throws
        let notFoundSet = ResistanceSet(setNumber: 99, weightKg: 50, repetitions: 1)
        await #expect(throws: Error.self) {
            try await trainingRepo.updateSet(trainingId: training.id, exerciseId: 101, set: notFoundSet)
        }

        // deleteSet
        try await trainingRepo.deleteSet(trainingId: training.id, exerciseId: 101, setNumber: 2)

        // completeTraining with notes
        let completed = try await trainingRepo.completeTraining(id: training.id, overallRpe: 8.5, notes: "Felt strong")
        #expect(completed.status == .completed)
        #expect(completed.notes == "Felt strong")
        #expect(completed.overallRpe == 8.5)

        // cancelTraining
        let training2 = try await trainingRepo.createTrainingFromSession(sessionId: 30)
        let cancelled = try await trainingRepo.cancelTraining(id: training2.id)
        #expect(cancelled.status == .cancelled)

        // 404 checks
        await #expect(throws: Error.self) {
            try await trainingRepo.startTraining(id: 9999)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.pauseTraining(id: 9999)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.resumeTraining(id: 9999)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.completeTraining(id: 9999, overallRpe: nil, notes: nil)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.cancelTraining(id: 9999)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.logSet(trainingId: 9999, exerciseId: 101, set: newSet)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.updateSet(trainingId: 9999, exerciseId: 101, set: newSet)
        }
        await #expect(throws: Error.self) {
            try await trainingRepo.deleteSet(trainingId: 9999, exerciseId: 101, setNumber: 1)
        }

        let notFoundTr = try await trainingRepo.getTraining(id: 9999)
        #expect(notFoundTr == nil)
    }

    @Test("SwiftDataAthleteRepository: deleteBodyweight not found, SDBodyweightEntry mapping")
    func testAthleteRepositoryBranches() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        let entry = BodyweightEntry(id: 1, weightKg: 82.5, measuredDate: Date(), notes: "Morning")
        _ = try await repo.logBodyweight(entry: entry)

        let all = try await repo.getBodyweightHistory()
        #expect(all.count == 1)

        // Delete existing
        try await repo.deleteBodyweight(id: all.first!.id)
        let afterDelete = try await repo.getBodyweightHistory()
        #expect(afterDelete.isEmpty)

        // Delete not found
        try await repo.deleteBodyweight(id: 9999)

        // SDBodyweightEntry mapping
        let sd = SDBodyweightEntry.fromDomain(entry)
        let domain = sd.toDomain()
        #expect(domain.weightKg == 82.5)
    }

    @Test("SwiftDataPortabilityRepository: exportWorkoutsCSV with exercises")
    func testPortabilityRepositoryCSVBranches() async throws {
        let container = try createTestContainer()
        let portability = SwiftDataPortabilityRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let tr = Training(
            id: 1,
            name: "Upper Power",
            status: .completed,
            loggedExercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    sets: [
                        ResistanceSet(setNumber: 1, weightKg: 100, repetitions: 5, rpe: 8.0, isCompleted: true)
                    ]
                )
            ]
        )
        container.mainContext.insert(SDTraining.fromDomain(tr))
        try container.mainContext.save()

        let csv = try await portability.exportWorkoutsCSV()
        #expect(csv.contains("Bench Press"))
        #expect(csv.contains("100.0"))
    }

    @Test("ExerciseCatalogSeed: seed when already seeded exits early")
    func testExerciseCatalogSeedEarlyExit() throws {
        let container = try createTestContainer()
        // Seed first time
        ExerciseCatalogSeed.seedInitialDataIfNeeded(context: container.mainContext)
        // Seed second time (hits guard count == 0 else return)
        ExerciseCatalogSeed.seedInitialDataIfNeeded(context: container.mainContext)
    }

    @Test("SwiftData Models fallback raw values for enums")
    func testSwiftDataModelsRawValueFallbacks() {
        // SDExercise fallback
        let sdEx = SDExercise(
            id: 999,
            name: "Unknown Ex",
            primaryCategory: .resistance,
            difficultyLevel: .beginner
        )
        sdEx.primaryCategoryRaw = "INVALID_CAT"
        sdEx.difficultyLevelRaw = "INVALID_DIFF"
        let domainEx = sdEx.toDomain()
        #expect(domainEx.primaryCategory == .resistance)
        #expect(domainEx.difficultyLevel == .beginner)

        // SDResistanceSet fallback
        let sdSet = SDResistanceSet(
            setNumber: 1,
            setType: .normal
        )
        sdSet.setTypeRaw = "INVALID_SET_TYPE"
        let domainSet = sdSet.toDomain()
        #expect(domainSet.setType == .normal)

        // SDSessionExercise fallback
        let sdItem = SDSessionExercise(
            orderIndex: 0,
            exerciseId: 1,
            exerciseName: "Ex",
            part: .main
        )
        sdItem.partRaw = "INVALID_PART"
        let domainItem = sdItem.toDomain()
        #expect(domainItem.part == .main)

        // SDTraining fallback
        let sdTr = SDTraining(
            id: 999,
            name: "Unknown Tr",
            status: .planned
        )
        sdTr.statusRaw = "INVALID_STATUS"
        let domainTr = sdTr.toDomain()
        #expect(domainTr.status == .planned)
    }

    @Test("SwiftDataSessionRepository clone non-existent session and clone with existing sessions")
    func testSessionCloneBranches() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)

        // Non-existent clone throws 404
        await #expect(throws: Error.self) {
            try await sessionRepo.cloneSession(id: 9999)
        }

        // Save session then clone
        let s = Session(id: 10, name: "Legs", exercises: [])
        _ = try await sessionRepo.saveSession(s)
        let cloned = try await sessionRepo.cloneSession(id: 10)
        #expect(cloned.name == "Legs (Copy)")
        #expect(cloned.id == 11)
    }

    @Test("SwiftDataTrainingRepository createTrainingFromSession with invalid raw part and setType")
    func testTrainingRepoInvalidRawEnumFallbacks() async throws {
        let container = try createTestContainer()
        let invalidSet = SDResistanceSet(setNumber: 1, setType: .normal)
        invalidSet.setTypeRaw = "INVALID_SET"
        let invalidEx = SDSessionExercise(orderIndex: 0, exerciseId: 1, exerciseName: "Ex", part: .main, sets: [invalidSet])
        invalidEx.partRaw = "INVALID_PART"
        let session = SDSession(
            id: 88,
            name: "Invalid Raw Template",
            exercises: [invalidEx]
        )
        container.mainContext.insert(session)
        try container.mainContext.save()

        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)
        let created = try await trainingRepo.createTrainingFromSession(sessionId: 88)
        #expect(created.loggedExercises.first?.part == .main)
        #expect(created.loggedExercises.first?.sets.first?.setType == .normal)
    }

    @Test("SwiftDataExerciseRepository primaryMuscleGroup nil search evaluation")
    func testExerciseRepoNilPrimaryMuscleSearch() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let ex = Exercise(id: 500, name: "Jumping Jacks", primaryCategory: .endurance, primaryMuscleGroup: nil)
        _ = try await repo.saveExercise(ex)

        let results = try await repo.getExercises(category: nil, search: "glutes")
        #expect(results.isEmpty)
    }

    @Test("SwiftDataAthleteRepository log multiple bodyweight entries for nextId max")
    func testAthleteRepoNextIdMax() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        let e1 = try await repo.logBodyweight(entry: BodyweightEntry(id: 0, weightKg: 75.0, measuredDate: Date()))
        #expect(e1.id == 1)

        let e2 = try await repo.logBodyweight(entry: BodyweightEntry(id: 0, weightKg: 75.5, measuredDate: Date()))
        #expect(e2.id == 2)
    }
}
