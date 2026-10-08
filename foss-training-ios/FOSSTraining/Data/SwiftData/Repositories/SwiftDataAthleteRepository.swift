import Foundation
import SwiftData

@MainActor
public final class SwiftDataAthleteRepository: AthleteRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func getProfile() async throws -> AthleteProfile? {
        let descriptor = FetchDescriptor<SDAthleteProfile>()
        let results = try modelContext.fetch(descriptor)
        return results.first?.toDomain()
    }

    public func saveProfile(_ profile: AthleteProfile) async throws -> AthleteProfile {
        let descriptor = FetchDescriptor<SDAthleteProfile>(predicate: #Predicate<SDAthleteProfile> { $0.id == profile.id })
        if let existing = try modelContext.fetch(descriptor).first {
            existing.displayName = profile.displayName
            existing.genderRaw = profile.gender.rawValue
            existing.dateOfBirth = profile.dateOfBirth
            existing.heightCm = profile.heightCm
            existing.experienceLevelRaw = profile.experienceLevel.rawValue
            existing.targetGoal = profile.targetGoal
            existing.preferredUnitRaw = profile.preferredUnit.rawValue
        } else {
            let newProfile = SDAthleteProfile.fromDomain(profile)
            modelContext.insert(newProfile)
        }
        try modelContext.save()
        return profile
    }

    public func getBodyweightHistory() async throws -> [BodyweightEntry] {
        let descriptor = FetchDescriptor<SDBodyweightEntry>(sortBy: [SortDescriptor(\.measuredDate, order: .forward)])
        return try modelContext.fetch(descriptor).map { $0.toDomain() }
    }

    public func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry {
        let all = try await getBodyweightHistory()
        let nextId = (all.map(\.id).max() ?? 0) + 1
        let finalEntry = BodyweightEntry(id: nextId, weightKg: entry.weightKg, measuredDate: entry.measuredDate, notes: entry.notes)
        let sd = SDBodyweightEntry.fromDomain(finalEntry)
        modelContext.insert(sd)
        try modelContext.save()
        return finalEntry
    }

    public func deleteBodyweight(id: Int) async throws {
        let descriptor = FetchDescriptor<SDBodyweightEntry>(predicate: #Predicate<SDBodyweightEntry> { $0.id == id })
        if let existing = try modelContext.fetch(descriptor).first {
            modelContext.delete(existing)
            try modelContext.save()
        }
    }

    public func calculateRelativeStrength(
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender,
        formula: ScoringFormula
    ) async throws -> RelativeStrengthScore {
        RelativeStrengthCalculator.calculate(
            totalKg: totalKg,
            bodyweightKg: bodyweightKg,
            gender: gender,
            formula: formula
        )
    }
}
