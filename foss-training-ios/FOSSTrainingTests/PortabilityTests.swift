import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

@Suite("SwiftData Portability & Migration Tests")
@MainActor
struct PortabilityTests {

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

    @Test("Full backup export and import cycle")
    func testFullBackupCycle() async throws {
        let container = try createTestContainer()
        let portabilityRepo = SwiftDataPortabilityRepository(modelContext: container.mainContext)
        let exerciseRepo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)
        let athleteRepo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        _ = try await exerciseRepo.saveExercise(Exercise(id: 99, name: "Overhead Press", primaryCategory: .resistance))
        _ = try await sessionRepo.saveSession(Session(id: 1, name: "Shoulders", exercises: []))
        let tr = Training(id: 1, name: "Shoulder Workout", status: .completed)
        container.mainContext.insert(SDTraining.fromDomain(tr))
        _ = try await athleteRepo.logBodyweight(entry: BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: Date()))
        try container.mainContext.save()

        let exportPayload = try await portabilityRepo.exportFullBackup()
        #expect(exportPayload.exportVersion == "1.0")
        #expect(exportPayload.exercises.contains { $0.id == 99 })

        // Create fresh second container and import
        let container2 = try createTestContainer()
        let portabilityRepo2 = SwiftDataPortabilityRepository(modelContext: container2.mainContext)
        let exerciseRepo2 = SwiftDataExerciseRepository(modelContext: container2.mainContext)

        let importedCount = try await portabilityRepo2.importFullBackup(payload: exportPayload)
        #expect(importedCount >= 1)

        let importedExercise = try await exerciseRepo2.getExercise(id: 99)
        #expect(importedExercise?.name == "Overhead Press")
    }

    @Test("Workouts CSV export formatting")
    func testWorkoutsCSVExport() async throws {
        let container = try createTestContainer()
        let portabilityRepo = SwiftDataPortabilityRepository(modelContext: container.mainContext)

        let csv = try await portabilityRepo.exportWorkoutsCSV()
        #expect(csv.contains("training_id,training_name,date,exercise,set_number,weight_kg,reps,rpe,completed"))
    }
}
