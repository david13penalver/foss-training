import Foundation

public enum AcwrRiskZone: String, CaseIterable, Identifiable, Codable, Sendable {
    case low = "LOW"
    case optimal = "OPTIMAL"
    case caution = "CAUTION"
    case high = "HIGH"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .low: return "Under-training / Low Risk"
        case .optimal: return "Optimal 'Sweet Spot'"
        case .caution: return "Elevated Injury Risk"
        case .high: return "High Injury Risk Danger Zone"
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
