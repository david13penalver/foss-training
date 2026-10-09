import Foundation

public protocol AthleteRepository: Sendable {
    /// Retrieves current athlete profile
    func getProfile() async throws -> AthleteProfile?

    /// Creates or updates athlete profile
    func saveProfile(_ profile: AthleteProfile) async throws -> AthleteProfile

    /// Retrieves chronological bodyweight logs
    func getBodyweightHistory() async throws -> [BodyweightEntry]

    /// Logs a morning bodyweight entry
    func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry

    /// Deletes a bodyweight entry by ID
    func deleteBodyweight(id: Int) async throws

    /// Computes powerlifting relative strength metrics
    func calculateRelativeStrength(
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender,
        formula: ScoringFormula
    ) async throws -> RelativeStrengthScore
}

public extension AthleteRepository {
    func getBodyweightEntries(limit: Int?) async throws -> [BodyweightEntry] {
        let all = try await getBodyweightHistory()
        if let limit {
            return Array(all.suffix(limit))
        }
        return all
    }

    func logBodyweight(weightKg: Double, date: Date = Date(), notes: String? = nil) async throws -> BodyweightEntry {
        let entry = BodyweightEntry(id: 0, weightKg: weightKg, measuredDate: date, notes: notes)
        return try await logBodyweight(entry: entry)
    }

    func calculateRelativeStrength(
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender = .male,
        formula: ScoringFormula = .dots
    ) async throws -> RelativeStrengthScore {
        RelativeStrengthCalculator.calculate(totalKg: totalKg, bodyweightKg: bodyweightKg, gender: gender, formula: formula)
    }
}
