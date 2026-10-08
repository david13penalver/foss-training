import Foundation

public enum AcwrRiskZone: String, CaseIterable, Identifiable, Codable, Sendable {
    case low = "LOW"
    case optimal = "OPTIMAL"
    case caution = "CAUTION"
    case high = "HIGH"

    public static let underTraining = AcwrRiskZone.low
    public static let sweetSpot = AcwrRiskZone.optimal
    public static let elevatedRisk = AcwrRiskZone.caution
    public static let dangerZone = AcwrRiskZone.high

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .low: return "Under-training / Low Risk"
        case .optimal: return "Optimal 'Sweet Spot'"
        case .caution: return "Elevated Injury Risk"
        case .high: return "High Injury Risk Danger Zone"
        }
    }

    public var description: String {
        switch self {
        case .low:
            return "Acute workload is significantly below your chronic training baseline. Risk of deconditioning and elevated injury hazard upon sudden return to high volume."
        case .optimal:
            return "Acute workload is in the optimal 'sweet spot' (0.80 - 1.30). Fitness gains are maximized while injury risk remains minimal."
        case .caution:
            return "Acute workload is moderately elevated above chronic baseline (1.30 - 1.50). Fatigue is accumulating; monitor recovery and sleep closely."
        case .high:
            return "Acute workload spike exceeds safe thresholds (ratio > 1.50). Injury risk is exponentially elevated. A deload week or active recovery is strongly recommended."
        }
    }

    public static func from(ratio: Double) -> AcwrRiskZone {
        if ratio < 0.8 {
            return .low
        } else if ratio <= 1.3 {
            return .optimal
        } else if ratio <= 1.5 {
            return .caution
        } else {
            return .high
        }
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let raw = try container.decode(String.self).uppercased().trimmingCharacters(in: .whitespacesAndNewlines)
        switch raw {
        case "LOW", "UNDERTRAINING", "UNDER_TRAINING":
            self = .low
        case "OPTIMAL", "SWEET_SPOT":
            self = .optimal
        case "CAUTION", "OVERREACHING", "ELEVATED_RISK":
            self = .caution
        case "HIGH", "HIGH_RISK", "DANGER_ZONE":
            self = .high
        default:
            self = .optimal
        }
    }
}

public enum WorkloadRatioCalculator {
    public static func computeACWR(trainings: [Training], targetDate: Date = Date()) -> Double {
        let calendar = Calendar.current
        let acuteCutoff = calendar.date(byAdding: .day, value: -7, to: targetDate)!
        let chronicCutoff = calendar.date(byAdding: .day, value: -28, to: targetDate)!

        var acuteLoad = 0.0
        var chronicLoad = 0.0

        for tr in trainings where tr.status == .completed {
            let sessionMinutes = tr.startTime != nil && tr.endTime != nil
                ? max(1.0, tr.endTime!.timeIntervalSince(tr.startTime!) / 60.0)
                : 45.0
            let rpe = tr.overallRpe ?? 6.0
            let sessionLoadAU = sessionMinutes * rpe

            if tr.trainingDate >= acuteCutoff && tr.trainingDate <= targetDate {
                acuteLoad += sessionLoadAU
            }
            if tr.trainingDate >= chronicCutoff && tr.trainingDate <= targetDate {
                chronicLoad += sessionLoadAU
            }
        }

        let weeklyChronicAverage = chronicLoad / 4.0
        guard weeklyChronicAverage > 0 else {
            return 1.0 // Default baseline
        }

        let ratio = acuteLoad / weeklyChronicAverage
        return (ratio * 100.0).rounded() / 100.0
    }
}
