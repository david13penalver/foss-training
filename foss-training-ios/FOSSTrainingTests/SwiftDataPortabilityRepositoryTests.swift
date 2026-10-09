import Foundation
import SwiftData
import Testing
@testable import FOSSTraining

private final class SDPortabilityMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (HTTPURLResponse, Data))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host() == "sd-portability-mock.local"
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = SDPortabilityMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: URLError(.badServerResponse))
            return
        }
        do {
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            client?.urlProtocol(self, didLoad: data)
            client?.urlProtocolDidFinishLoading(self)
        } catch {
            client?.urlProtocol(self, didFailWithError: error)
        }
    }

    override func stopLoading() {}
}

private func createSDPortabilityMockClient(url: URL) -> NetworkClient {
    let config = URLSessionConfiguration.ephemeral
    config.protocolClasses = [SDPortabilityMockURLProtocol.self]
    let session = URLSession(configuration: config)
    return NetworkClient(baseURLString: url.absoluteString, session: session)
}

@Suite("SwiftData Portability Repository Tests")
@MainActor
struct SwiftDataPortabilityRepositoryTests {

    private func createTestContainer() throws -> ModelContainer {
        let schema = Schema([
            SDExercise.self,
            SDSession.self,
            SDSessionExercise.self,
            SDResistanceSet.self,
            SDTrainingProgram.self,
            SDProgramWorkout.self,
            SDTraining.self,
            SDBodyweightEntry.self,
            SDAthleteProfile.self
        ])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: true)
        return try ModelContainer(for: schema, configurations: [config])
    }

    @Test("SwiftDataPortability: generateBackup exports all 5 aggregates")
    func testGenerateBackup() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)

        // Seed data
        let ex = Exercise(id: 10, name: "Deadlift", primaryCategory: .resistance)
        let ses = Session(id: 20, name: "Pull Day", exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 10, exerciseName: "Deadlift", sets: [
                ResistanceSet(setNumber: 1, weightKg: 180.0, repetitions: 5, isCompleted: true)
            ])
        ])
        let prog = TrainingProgram(id: 30, name: "Hypertrophy Block", durationWeeks: 6)
        let tr = Training(id: 40, name: "Heavy Pull", status: .completed)
        let bw = BodyweightEntry(id: 50, weightKg: 83.0, measuredDate: Date(), notes: "Morning")

        _ = try await SwiftDataExerciseRepository(modelContext: context).saveExercise(ex)
        _ = try await SwiftDataSessionRepository(modelContext: context).saveSession(ses)
        _ = try await SwiftDataTrainingProgramRepository(modelContext: context).saveProgram(prog)
        context.insert(SDTraining.fromDomain(tr))
        context.insert(SDBodyweightEntry.fromDomain(bw))
        try context.save()

        let backup = try await repo.generateBackup()
        #expect(backup.exportVersion == "1.0")
        #expect(backup.exercises.contains { $0.id == 10 })
        #expect(backup.sessions.contains { $0.id == 20 })
        #expect(backup.programs.contains { $0.id == 30 })
        #expect(backup.trainings.contains { $0.id == 40 })
        #expect(backup.bodyweightEntries.contains { $0.id == 50 })

        // Test backwards-compatibility alias
        let legacyBackup = try await repo.exportFullBackup()
        #expect(legacyBackup.exportVersion == "1.0")
    }

    @Test("SwiftDataPortability: restoreBackup with OVERWRITE mode")
    func testRestoreOverwrite() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)

        // Seed initial exercise with name "Old Squat"
        let initialEx = Exercise(id: 1, name: "Old Squat", primaryCategory: .resistance)
        _ = try await SwiftDataExerciseRepository(modelContext: context).saveExercise(initialEx)

        // Seed initial training with ID 10
        let initialTr = Training(id: 10, name: "Old Training", status: .completed)
        context.insert(SDTraining.fromDomain(initialTr))

        // Seed initial bodyweight with ID 4 to test overwrite branch
        context.insert(SDBodyweightEntry.fromDomain(BodyweightEntry(id: 4, weightKg: 80.0, measuredDate: Date())))
        try context.save()

        // Incoming backup has same ID 1 with name "New Squat" and same training ID 10 with name "New Training"
        let updatedEx = Exercise(id: 1, name: "New Squat", primaryCategory: .resistance)
        let updatedTr = Training(id: 10, name: "New Training", status: .completed)
        let ses = Session(id: 2, name: "Legs", exercises: [])
        let prog = TrainingProgram(id: 3, name: "Strength", durationWeeks: 4)
        let bw = BodyweightEntry(id: 4, weightKg: 85.0, measuredDate: Date())

        let backup = FullBackupData(
            exercises: [updatedEx],
            sessions: [ses],
            programs: [prog],
            trainings: [updatedTr],
            bodyweightEntries: [bw]
        )

        let summary = try await repo.restoreBackup(backup, mode: .overwrite)
        #expect(summary.exercisesImported == 1)
        #expect(summary.sessionsImported == 1)
        #expect(summary.programsImported == 1)
        #expect(summary.trainingsImported == 1)
        #expect(summary.bodyweightImported == 1)
        #expect(summary.totalImported == 5)

        let fetchedEx = try await SwiftDataExerciseRepository(modelContext: context).getExercise(id: 1)
        #expect(fetchedEx?.name == "New Squat")

        let fetchedTrainings = try await SwiftDataTrainingRepository(modelContext: context).getTrainings()
        #expect(fetchedTrainings.first { $0.id == 10 }?.name == "New Training")
    }

    @Test("SwiftDataPortability: restoreBackup with SKIP_EXISTING mode")
    func testRestoreSkipExisting() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)

        // Seed initial records with ID 1
        let initialEx = Exercise(id: 1, name: "Preserved Exercise", primaryCategory: .resistance)
        _ = try await SwiftDataExerciseRepository(modelContext: context).saveExercise(initialEx)
        _ = try await SwiftDataSessionRepository(modelContext: context).saveSession(Session(id: 1, name: "Preserved Ses", exercises: []))
        _ = try await SwiftDataTrainingProgramRepository(modelContext: context).saveProgram(TrainingProgram(id: 1, name: "Preserved Prog", durationWeeks: 4))
        context.insert(SDTraining.fromDomain(Training(id: 1, name: "Preserved Tr", status: .completed)))
        context.insert(SDBodyweightEntry.fromDomain(BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: Date())))
        try context.save()

        // Incoming backup has ID 1 (already exists, skipped) and new ID 2 (inserted) for all 5 aggregates
        let backup = FullBackupData(
            exercises: [
                initialEx,
                Exercise(id: 2, name: "Brand New Exercise", primaryCategory: .resistance)
            ],
            sessions: [
                Session(id: 1, name: "Ignored Ses", exercises: []),
                Session(id: 2, name: "Brand New Session", exercises: [])
            ],
            programs: [
                TrainingProgram(id: 1, name: "Ignored Prog", durationWeeks: 4),
                TrainingProgram(id: 2, name: "Brand New Program", durationWeeks: 2)
            ],
            trainings: [
                Training(id: 1, name: "Ignored Tr", status: .completed),
                Training(id: 2, name: "Brand New Training", status: .completed)
            ],
            bodyweightEntries: [
                BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: Date()),
                BodyweightEntry(id: 2, weightKg: 82.0, measuredDate: Date())
            ]
        )
        let summary = try await repo.restoreBackup(backup, mode: .skipExisting)

        #expect(summary.exercisesImported == 1) // Only new ID 2 inserted
        #expect(summary.sessionsImported == 1)
        #expect(summary.programsImported == 1)
        #expect(summary.trainingsImported == 1)
        #expect(summary.bodyweightImported == 1)
        #expect(summary.totalImported == 5)

        let fetched1 = try await SwiftDataExerciseRepository(modelContext: context).getExercise(id: 1)
        #expect(fetched1?.name == "Preserved Exercise")

        let fetched2 = try await SwiftDataExerciseRepository(modelContext: context).getExercise(id: 2)
        #expect(fetched2?.name == "Brand New Exercise")
    }

    @Test("SwiftDataPortability: restoreBackup with MERGE mode generates new IDs for collisions")
    func testRestoreMerge() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)

        // Seed initial records
        _ = try await SwiftDataExerciseRepository(modelContext: context).saveExercise(Exercise(id: 1, name: "Original Ex", primaryCategory: .resistance))
        _ = try await SwiftDataSessionRepository(modelContext: context).saveSession(Session(id: 1, name: "Original Ses", exercises: []))
        _ = try await SwiftDataTrainingProgramRepository(modelContext: context).saveProgram(TrainingProgram(id: 1, name: "Original Prog", durationWeeks: 4))
        context.insert(SDTraining.fromDomain(Training(id: 1, name: "Original Tr", status: .completed)))
        _ = try await SwiftDataAthleteRepository(modelContext: context).logBodyweight(entry: BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: Date()))
        try context.save()

        // Incoming backup has ID 1 (collision) and ID 999 (no collision) for all aggregates
        let backup = FullBackupData(
            exercises: [
                Exercise(id: 1, name: "Merged Ex", primaryCategory: .resistance),
                Exercise(id: 999, name: "Fresh Ex", primaryCategory: .resistance)
            ],
            sessions: [
                Session(id: 1, name: "Merged Ses", exercises: []),
                Session(id: 999, name: "Fresh Ses", exercises: [])
            ],
            programs: [
                TrainingProgram(id: 1, name: "Merged Prog", durationWeeks: 4),
                TrainingProgram(id: 999, name: "Fresh Prog", durationWeeks: 2)
            ],
            trainings: [
                Training(id: 1, name: "Merged Tr", status: .completed),
                Training(id: 999, name: "Fresh Tr", status: .completed)
            ],
            bodyweightEntries: [
                BodyweightEntry(id: 1, weightKg: 81.0, measuredDate: Date()),
                BodyweightEntry(id: 999, weightKg: 75.0, measuredDate: Date())
            ]
        )

        let summary = try await repo.restoreBackup(backup, mode: .merge)
        #expect(summary.totalImported == 10)

        let allEx = try await SwiftDataExerciseRepository(modelContext: context).getExercises(category: nil, search: nil)
        #expect(allEx.count == 3) // Original Ex (1), Fresh Ex (999), Merged Ex (new id)
        #expect(allEx.contains { $0.id == 1 && $0.name == "Original Ex" })
        #expect(allEx.contains { $0.id == 999 && $0.name == "Fresh Ex" })
        #expect(allEx.contains { $0.name == "Merged Ex" && $0.id != 1 && $0.id != 999 })

        let allSes = try await SwiftDataSessionRepository(modelContext: context).getSessions()
        #expect(allSes.count == 3)

        let allProgs = try await SwiftDataTrainingProgramRepository(modelContext: context).getPrograms()
        #expect(allProgs.count == 3)

        let allTrainings = try await SwiftDataTrainingRepository(modelContext: context).getTrainings()
        #expect(allTrainings.count == 3)

        let allBw = try await SwiftDataAthleteRepository(modelContext: context).getBodyweightHistory()
        #expect(allBw.count == 3)
    }

    @Test("SwiftDataPortability: migrateToRemoteServer success and nil fallbacks")
    func testMigrateToRemoteServer() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let serverUrl = URL(string: "https://sd-portability-mock.local")!

        let repoWithMock = SwiftDataPortabilityRepository(modelContext: context) { url in
            createSDPortabilityMockClient(url: url)
        }

        // Success with values
        SDPortabilityMockURLProtocol.requestHandler = { request in
            let json = """
            {
                "exercisesImported": 1,
                "sessionsImported": 2,
                "programsImported": 3,
                "trainingsImported": 4,
                "bodyweightsImported": 5
            }
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }
        let summary = try await repoWithMock.migrateToRemoteServer(serverUrl: serverUrl)
        #expect(summary.totalImported == 15)

        // Nil fields fallback ?? 0
        SDPortabilityMockURLProtocol.requestHandler = { request in
            let json = "{}".data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }
        let emptySummary = try await repoWithMock.migrateToRemoteServer(serverUrl: serverUrl)
        #expect(emptySummary.totalImported == 0)
    }

    @Test("SwiftDataPortability: default clientFactory fallback")
    func testDefaultClientFactoryFallback() throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)
        let client = repo.clientFactory(URL(string: "https://example.com")!)
        _ = client
    }

    @Test("SwiftDataPortability: CSV export, CSV import, and purgeLocalDatabase")
    func testCsvAndPurge() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)

        // Seed training
        let tr = Training(id: 1, name: "Morning Workout", status: .completed)
        context.insert(SDTraining.fromDomain(tr))
        try context.save()

        let csv = try await repo.exportWorkoutsCsv()
        #expect(csv.contains("Morning Workout"))

        // Legacy exportWorkoutsCSV alias
        let legacyCsv = try await repo.exportWorkoutsCSV()
        #expect(legacyCsv.contains("Morning Workout"))

        // Import CSV
        let imported = try await repo.importWorkoutsCsv(csv)
        #expect(imported.trainingsImported >= 1)

        // Purge
        try await repo.purgeLocalDatabase()
        let afterPurgeTrainings = try await SwiftDataTrainingRepository(modelContext: context).getTrainings()
        #expect(afterPurgeTrainings.isEmpty)

        let afterPurgeExercises = try await SwiftDataExerciseRepository(modelContext: context).getExercises(category: nil, search: nil)
        #expect(afterPurgeExercises.isEmpty)
    }

    @Test("SwiftDataPortability: legacy importFullBackup alias")
    func testLegacyImportFullBackup() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataPortabilityRepository(modelContext: context)

        let backup = FullBackupData(exercises: [Exercise(id: 5, name: "Bench", primaryCategory: .resistance)])
        let count = try await repo.importFullBackup(payload: backup)
        #expect(count == 1)
    }
}
