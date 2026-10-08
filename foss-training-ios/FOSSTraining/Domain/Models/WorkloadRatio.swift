import Foundation

public struct DailyWorkload: Identifiable, Codable, Hashable, Sendable {
    public var id: Date { date }
    public let date: Date
    public var workloadAu: Double
    public var totalVolumeKg: Double
    public var completedSessions: Int

    public init(date: Date, workloadAu: Double = 0.0, totalVolumeKg: Double = 0.0, completedSessions: Int = 0) {
        self.date = date
        self.workloadAu = workloadAu
        self.totalVolumeKg = totalVolumeKg
        self.completedSessions = completedSessions
    }
}

public struct WorkloadRatio: Codable, Hashable, Sendable {
    public let targetDate: Date
    public let acuteWorkload: Double
    public let acuteDailyAverage: Double
    public let chronicWorkload: Double
    public let chronicWeeklyAverage: Double
    public let chronicDailyAverage: Double
    public let acwr: Double
    public let riskZone: AcwrRiskZone
    public let deloadRecommended: Bool
    public let recommendation: String
    public let dailyWorkloads: [DailyWorkload]

    public var acwrRatio: Double { acwr }

    public init(
        targetDate: Date,
        acuteWorkload: Double,
        acuteDailyAverage: Double,
        chronicWorkload: Double,
        chronicWeeklyAverage: Double,
        chronicDailyAverage: Double,
        acwr: Double,
        riskZone: AcwrRiskZone,
        deloadRecommended: Bool,
        recommendation: String,
        dailyWorkloads: [DailyWorkload]
    ) {
        self.targetDate = targetDate
        self.acuteWorkload = acuteWorkload
        self.acuteDailyAverage = acuteDailyAverage
        self.chronicWorkload = chronicWorkload
        self.chronicWeeklyAverage = chronicWeeklyAverage
        self.chronicDailyAverage = chronicDailyAverage
        self.acwr = acwr
        self.riskZone = riskZone
        self.deloadRecommended = deloadRecommended
        self.recommendation = recommendation
        self.dailyWorkloads = dailyWorkloads
    }
}
