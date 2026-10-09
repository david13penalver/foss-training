import Foundation

public enum HeartRateZoneError: LocalizedError, Equatable, Sendable {
    case missingParameters
    case invalidMaxHr(Int)
    case invalidAge(Int)
    case invalidRestingHr(Int, maxHr: Int)

    public var errorDescription: String? {
        switch self {
        case .missingParameters:
            return "Either maxHr or age must be provided to calculate heart rate zones"
        case .invalidMaxHr(let hr):
            return "maxHr must be between 60 and 240 bpm: \(hr)"
        case .invalidAge(let age):
            return "age must be between 10 and 110: \(age)"
        case .invalidRestingHr(let rest, let max):
            return "restingHr must be at least 30 and less than maxHr (\(max)): \(rest)"
        }
    }
}

public enum HeartRateZoneCalculator {
    private struct ZoneConfig {
        let number: Int
        let name: String
        let minPct: Double
        let maxPct: Double
        let desc: String
        let benefit: String
    }

    private static let zoneConfigs: [ZoneConfig] = [
        ZoneConfig(
            number: 1,
            name: "Active Recovery",
            minPct: 0.50,
            maxPct: 0.60,
            desc: "Promotes recovery, improves fat metabolism",
            benefit: "Active recovery, warmup, cooldown, and foundational aerobic base building. Low physical stress."
        ),
        ZoneConfig(
            number: 2,
            name: "Aerobic Base",
            minPct: 0.60,
            maxPct: 0.70,
            desc: "Builds endurance base, optimizes mitochondrial function",
            benefit: "Maximizes lipid oxidation and stimulates mitochondrial biogenesis and capillary network growth."
        ),
        ZoneConfig(
            number: 3,
            name: "Tempo / Aerobic Endurance",
            minPct: 0.70,
            maxPct: 0.80,
            desc: "Improves aerobic capacity, raises lactate threshold",
            benefit: "Elevates aerobic power, cardiac stroke volume, and muscular glycogen storage efficiency."
        ),
        ZoneConfig(
            number: 4,
            name: "Lactate Threshold",
            minPct: 0.80,
            maxPct: 0.90,
            desc: "Maximizes lactate tolerance and high-intensity stamina",
            benefit: "Increases lactate clearance capacity, anaerobic threshold, and sustains high-intensity speed-endurance."
        ),
        ZoneConfig(
            number: 5,
            name: "Neuromuscular / Anaerobic",
            minPct: 0.90,
            maxPct: 1.00,
            desc: "Peak power output and VO2max intervals",
            benefit: "Develops maximum neuromuscular power, VO2 max capacity, and fast-twitch motor unit recruitment."
        )
    ]
    public static func calculate(restingHr: Int?, maxHr: Int, method: HeartRateZoneMethod) throws -> HeartRateZones {
        switch method {
        case .karvonen:
            return try compute(maxHr: maxHr, restingHr: restingHr, age: nil)
        case .percentMaxHr:
            return try compute(maxHr: maxHr, restingHr: nil, age: nil)
        }
    }

    public static func compute(maxHr: Int?, restingHr: Int?, age: Int?) throws(HeartRateZoneError) -> HeartRateZones {
        guard maxHr != nil || age != nil else {
            throw .missingParameters
        }

        let effectiveMaxHr: Int
        if let max = maxHr {
            guard max >= 60 && max <= 240 else {
                throw .invalidMaxHr(max)
            }
            effectiveMaxHr = max
        } else {
            let userAge = age!
            guard userAge >= 10 && userAge <= 110 else {
                throw .invalidAge(userAge)
            }
            effectiveMaxHr = Int((208.0 - (0.7 * Double(userAge))).rounded())
        }

        let method: HeartRateZoneMethod
        let heartRateReserve: Int?

        if let rest = restingHr {
            guard rest >= 30 && rest < effectiveMaxHr else {
                throw .invalidRestingHr(rest, maxHr: effectiveMaxHr)
            }
            method = .karvonen
            heartRateReserve = effectiveMaxHr - rest
        } else {
            method = .percentMaxHr
            heartRateReserve = nil
        }

        var zones: [CalculatedHeartRateZone] = []

        for config in zoneConfigs {
            let minBpm: Int
            let maxBpm: Int

            if method == .karvonen, let hrr = heartRateReserve, let rest = restingHr {
                minBpm = Int((Double(rest) + (config.minPct * Double(hrr))).rounded())
                maxBpm = Int((Double(rest) + (config.maxPct * Double(hrr))).rounded())
            } else {
                minBpm = Int((config.minPct * Double(effectiveMaxHr)).rounded())
                maxBpm = Int((config.maxPct * Double(effectiveMaxHr)).rounded())
            }

            zones.append(CalculatedHeartRateZone(
                zoneNumber: config.number,
                displayName: config.name,
                minPercentage: ((config.minPct * 100.0) * 10.0).rounded() / 10.0,
                maxPercentage: ((config.maxPct * 100.0) * 10.0).rounded() / 10.0,
                minBpm: minBpm,
                maxBpm: maxBpm,
                description: config.desc,
                trainingBenefit: config.benefit
            ))
        }

        return HeartRateZones(
            maxHr: effectiveMaxHr,
            restingHr: restingHr,
            age: age,
            method: method,
            heartRateReserve: heartRateReserve,
            zones: zones
        )
    }
}
