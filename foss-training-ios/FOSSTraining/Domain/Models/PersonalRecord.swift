import Foundation

public enum PRRecordType: String, CaseIterable, Identifiable, Codable, Sendable {
    case maxWeight = "MAX_WEIGHT"
    case maxVolume = "MAX_VOLUME"
    case maxEstimated1RM = "MAX_ESTIMATED_1RM"
    case maxReps = "MAX_REPS"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .maxWeight: return "Heaviest Weight"
        case .maxVolume: return "Highest Volume"
        case .maxEstimated1RM: return "Best Estimated 1RM"
        case .maxReps: return "Max Repetitions"
        }
    }
}

public struct PersonalRecord: Identifiable, Codable, Hashable, Sendable {
    public var id: String { "\(exerciseId)-\(recordType.rawValue)" }
    public let exerciseId: Int
    public let exerciseName: String
    public let recordType: PRRecordType
    public let value: Double
    public let unit: String
    public let achievedDate: Date
    public let trainingId: Int

    public init(
        exerciseId: Int,
        exerciseName: String,
        recordType: PRRecordType,
        value: Double,
        unit: String = "kg",
        achievedDate: Date,
        trainingId: Int
    ) {
        self.exerciseId = exerciseId
        self.exerciseName = exerciseName
        self.recordType = recordType
        self.value = value
        self.unit = unit
        self.achievedDate = achievedDate
        self.trainingId = trainingId
    }
}
