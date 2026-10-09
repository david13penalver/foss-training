import Foundation

public struct ProgressionDataPoint: Identifiable, Codable, Hashable, Sendable {
    public var id: Date { date }
    public let trainingId: Int
    public let date: Date
    public let totalSets: Int
    public let workingSets: Int
    public let totalReps: Int
    public let totalVolumeKg: Double
    public let topWeightKg: Double
    public let topWeightReps: Int
    public let topWeightRpe: Double?
    public let estimatedOneRepMax: Double
    public let averageIntensityKg: Double

    public init(
        trainingId: Int,
        date: Date,
        totalSets: Int,
        workingSets: Int,
        totalReps: Int,
        totalVolumeKg: Double,
        topWeightKg: Double,
        topWeightReps: Int,
        topWeightRpe: Double? = nil,
        estimatedOneRepMax: Double,
        averageIntensityKg: Double
    ) {
        self.trainingId = trainingId
        self.date = date
        self.totalSets = totalSets
        self.workingSets = workingSets
        self.totalReps = totalReps
        self.totalVolumeKg = totalVolumeKg
        self.topWeightKg = topWeightKg
        self.topWeightReps = topWeightReps
        self.topWeightRpe = topWeightRpe
        self.estimatedOneRepMax = estimatedOneRepMax
        self.averageIntensityKg = averageIntensityKg
    }

    private enum CodingKeys: String, CodingKey {
        case trainingId
        case date
        case totalSets
        case workingSets
        case totalReps
        case totalVolumeKg
        case topWeightKg
        case topWeightReps
        case topWeightRpe
        case estimatedOneRepMax
        case estimated1RmKg
        case averageIntensityKg
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.trainingId = try container.decode(Int.self, forKey: .trainingId)
        self.date = try container.decode(Date.self, forKey: .date)
        self.totalSets = try container.decode(Int.self, forKey: .totalSets)
        self.workingSets = try container.decode(Int.self, forKey: .workingSets)
        self.totalReps = try container.decode(Int.self, forKey: .totalReps)
        self.totalVolumeKg = try container.decode(Double.self, forKey: .totalVolumeKg)
        self.topWeightKg = try container.decode(Double.self, forKey: .topWeightKg)
        self.topWeightReps = try container.decode(Int.self, forKey: .topWeightReps)
        self.topWeightRpe = try container.decodeIfPresent(Double.self, forKey: .topWeightRpe)

        if let est = try container.decodeIfPresent(Double.self, forKey: .estimatedOneRepMax) {
            self.estimatedOneRepMax = est
        } else if let estKg = try container.decodeIfPresent(Double.self, forKey: .estimated1RmKg) {
            self.estimatedOneRepMax = estKg
        } else {
            self.estimatedOneRepMax = 0.0
        }

        self.averageIntensityKg = try container.decode(Double.self, forKey: .averageIntensityKg)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(trainingId, forKey: .trainingId)
        try container.encode(date, forKey: .date)
        try container.encode(totalSets, forKey: .totalSets)
        try container.encode(workingSets, forKey: .workingSets)
        try container.encode(totalReps, forKey: .totalReps)
        try container.encode(totalVolumeKg, forKey: .totalVolumeKg)
        try container.encode(topWeightKg, forKey: .topWeightKg)
        try container.encode(topWeightReps, forKey: .topWeightReps)
        try container.encodeIfPresent(topWeightRpe, forKey: .topWeightRpe)
        try container.encode(estimatedOneRepMax, forKey: .estimatedOneRepMax)
        try container.encode(averageIntensityKg, forKey: .averageIntensityKg)
    }
}

public struct ExerciseProgression: Codable, Hashable, Sendable {
    public let exerciseId: Int
    public let exerciseName: String
    public let formula: OneRepMaxFormula
    public let startDate: Date
    public let endDate: Date
    public let totalSessions: Int
    public let initial1RmKg: Double
    public let latest1RmKg: Double
    public let absolute1RmGainKg: Double
    public let relative1RmGainPercentage: Double
    public let allTimeBest1RmKg: Double
    public let allTimeBestTopWeightKg: Double
    public let allTimeMaxVolumeKg: Double
    public let trend: ProgressionTrend
    public let dataPoints: [ProgressionDataPoint]

    public var percentageChange: Double { relative1RmGainPercentage }

    public init(
        exerciseId: Int,
        exerciseName: String,
        formula: OneRepMaxFormula = .epley,
        startDate: Date,
        endDate: Date,
        totalSessions: Int,
        initial1RmKg: Double,
        latest1RmKg: Double,
        absolute1RmGainKg: Double,
        relative1RmGainPercentage: Double,
        allTimeBest1RmKg: Double,
        allTimeBestTopWeightKg: Double,
        allTimeMaxVolumeKg: Double,
        trend: ProgressionTrend,
        dataPoints: [ProgressionDataPoint]
    ) {
        self.exerciseId = exerciseId
        self.exerciseName = exerciseName
        self.formula = formula
        self.startDate = startDate
        self.endDate = endDate
        self.totalSessions = totalSessions
        self.initial1RmKg = initial1RmKg
        self.latest1RmKg = latest1RmKg
        self.absolute1RmGainKg = absolute1RmGainKg
        self.relative1RmGainPercentage = relative1RmGainPercentage
        self.allTimeBest1RmKg = allTimeBest1RmKg
        self.allTimeBestTopWeightKg = allTimeBestTopWeightKg
        self.allTimeMaxVolumeKg = allTimeMaxVolumeKg
        self.trend = trend
        self.dataPoints = dataPoints
    }
}
