import Foundation

public protocol AnalyticsRepository: Sendable {
    /// Estimates 1RM across all supported formulas for given weight and repetitions
    func calculateOneRepMax(weightKg: Double, reps: Int) async throws -> [OneRepMaxEstimate]

    /// Retrieves all-time personal records, optionally filtered by exercise ID
    func getPersonalRecords(exerciseId: Int?) async throws -> [PersonalRecord]

    /// Calculates Acute to Chronic Workload Ratio (ACWR) as of an optional reference date
    func calculateAcwr(asOfDate: Date?) async throws -> WorkloadRatio

    /// Calculates weekly direct and indirect set volume grouped by muscle group
    func getWeeklyMuscleVolume(weekStartDate: Date?) async throws -> WeeklyMuscleVolume

    /// Calculates chronological progression history and trend for a specific exercise over given months
    func getExerciseProgression(exerciseId: Int, months: Int) async throws -> ExerciseProgression

    /// Calculates 5-zone cardiovascular heart rate distribution
    func calculateHeartRateZones(restingHr: Int, maxHr: Int, method: HeartRateZoneMethod) async throws -> HeartRateZones
}
