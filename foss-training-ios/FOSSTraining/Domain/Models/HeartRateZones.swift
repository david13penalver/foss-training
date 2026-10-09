import Foundation

public enum HeartRateZoneMethod: String, CaseIterable, Identifiable, Codable, Sendable {
    case karvonen = "KARVONEN"
    case percentMaxHr = "PERCENT_MAX_HR"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .karvonen: return "Karvonen (Heart Rate Reserve)"
        case .percentMaxHr: return "Percentage of Max HR"
        }
    }
}

public struct CalculatedHeartRateZone: Identifiable, Codable, Hashable, Sendable {
    public var id: Int { zoneNumber }
    public let zoneNumber: Int
    public let displayName: String
    public let minPercentage: Double
    public let maxPercentage: Double
    public let minBpm: Int
    public let maxBpm: Int
    public let description: String
    public let trainingBenefit: String

    public init(
        zoneNumber: Int,
        displayName: String,
        minPercentage: Double,
        maxPercentage: Double,
        minBpm: Int,
        maxBpm: Int,
        description: String,
        trainingBenefit: String
    ) {
        self.zoneNumber = zoneNumber
        self.displayName = displayName
        self.minPercentage = minPercentage
        self.maxPercentage = maxPercentage
        self.minBpm = minBpm
        self.maxBpm = maxBpm
        self.description = description
        self.trainingBenefit = trainingBenefit
    }
}

public struct HeartRateZones: Codable, Hashable, Sendable {
    public let maxHr: Int
    public let restingHr: Int?
    public let age: Int?
    public let method: HeartRateZoneMethod
    public let heartRateReserve: Int?
    public let zones: [CalculatedHeartRateZone]

    public init(
        maxHr: Int,
        restingHr: Int? = nil,
        age: Int? = nil,
        method: HeartRateZoneMethod,
        heartRateReserve: Int? = nil,
        zones: [CalculatedHeartRateZone]
    ) {
        self.maxHr = maxHr
        self.restingHr = restingHr
        self.age = age
        self.method = method
        self.heartRateReserve = heartRateReserve
        self.zones = zones
    }
}
