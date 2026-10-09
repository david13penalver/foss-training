import Foundation
import SwiftData
import Testing
@testable import FOSSTraining

@Suite("SwiftData Athlete Repository Tests")
@MainActor
struct SwiftDataAthleteRepositoryTests {

    private func makeContainer() throws -> ModelContainer {
        let schema = Schema([
            SDAthleteProfile.self,
            SDBodyweightEntry.self
        ])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: true)
        return try ModelContainer(for: schema, configurations: [config])
    }

    @Test("SwiftDataAthleteRepository: profile lifecycle (create, read, update, domain mapping)")
    func testProfileLifecycle() async throws {
        let container = try makeContainer()
        let repo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        // Initial getProfile is nil
        let initial = try await repo.getProfile()
        #expect(initial == nil)

        // Save profile
        let profile = AthleteProfile(
            id: 1,
            displayName: "David",
            gender: .male,
            dateOfBirth: Date(timeIntervalSince1970: 600000),
            heightCm: 182.0,
            experienceLevel: .advanced,
            targetGoal: "Powerlifting DOTS 450",
            preferredUnit: .kg
        )

        let saved = try await repo.saveProfile(profile)
        #expect(saved.displayName == "David")
        #expect(saved.heightCm == 182.0)

        // Retrieve saved profile
        let fetched = try await repo.getProfile()
        #expect(fetched != nil)
        #expect(fetched?.id == 1)
        #expect(fetched?.displayName == "David")
        #expect(fetched?.gender == .male)
        #expect(fetched?.experienceLevel == .advanced)
        #expect(fetched?.targetGoal == "Powerlifting DOTS 450")
        #expect(fetched?.preferredUnit == .kg)

        // Update profile in-place
        var updated = fetched!
        updated.displayName = "David P"
        updated.heightCm = 183.0
        updated.experienceLevel = .intermediate

        let resaved = try await repo.saveProfile(updated)
        #expect(resaved.displayName == "David P")

        let refetched = try await repo.getProfile()
        #expect(refetched?.displayName == "David P")
        #expect(refetched?.heightCm == 183.0)

        // Default AthleteProfile
        let defaultProf = AthleteProfile.default
        #expect(defaultProf.displayName == "Athlete")
        #expect(defaultProf.preferredUnit == .kg)

        // SDAthleteProfile direct model mapping fallback
        let sdModel = SDAthleteProfile(
            id: 2,
            displayName: "Fallback",
            genderRaw: "UNKNOWN",
            dateOfBirth: nil,
            heightCm: 170.0,
            experienceLevelRaw: "INVALID",
            targetGoal: nil,
            preferredUnitRaw: "INVALID"
        )
        let domainFromInvalid = sdModel.toDomain()
        #expect(domainFromInvalid.gender == .male)
        #expect(domainFromInvalid.experienceLevel == .intermediate)
        #expect(domainFromInvalid.preferredUnit == .kg)
    }

    @Test("SwiftDataAthleteRepository: bodyweight logging, history, limit and delete")
    func testBodyweightCRUD() async throws {
        let container = try makeContainer()
        let repo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        let now = Date()
        let entry1 = try await repo.logBodyweight(
            weightKg: 82.5,
            date: now.addingTimeInterval(-86400),
            notes: "Day 1"
        )
        #expect(entry1.id == 1)
        #expect(entry1.weightKg == 82.5)

        let entry2 = try await repo.logBodyweight(
            weightKg: 81.8,
            date: now,
            notes: "Day 2"
        )
        #expect(entry2.id == 2)
        #expect(entry2.weightKg == 81.8)

        // Get full history
        let history = try await repo.getBodyweightHistory()
        #expect(history.count == 2)
        #expect(history[0].id == 1)
        #expect(history[1].id == 2)

        // Get limited entries
        let limited = try await repo.getBodyweightEntries(limit: 1)
        #expect(limited.count == 1)
        #expect(limited[0].id == 2)

        let unlimited = try await repo.getBodyweightEntries(limit: nil)
        #expect(unlimited.count == 2)

        // Delete entry 1
        try await repo.deleteBodyweight(id: 1)
        let remaining = try await repo.getBodyweightHistory()
        #expect(remaining.count == 1)
        #expect(remaining[0].id == 2)

        // Delete non-existent ID does not throw
        try await repo.deleteBodyweight(id: 999)
    }

    @Test("SwiftDataAthleteRepository: calculateRelativeStrength delegation and protocol extension")
    func testCalculateRelativeStrength() async throws {
        let container = try makeContainer()
        let repo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        let scoreDots = try await repo.calculateRelativeStrength(
            totalKg: 600.0,
            bodyweightKg: 85.0,
            gender: .male,
            formula: .dots
        )
        #expect(scoreDots.formula == .dots)
        #expect(scoreDots.score > 350.0)

        // Test default parameters in extension
        let defaultScore = try await (repo as AthleteRepository).calculateRelativeStrength(
            totalKg: 500.0,
            bodyweightKg: 80.0
        )
        #expect(defaultScore.formula == .dots)
        #expect(defaultScore.gender == .male)
    }
}
