import Foundation

public enum ProgressionTrend: String, CaseIterable, Identifiable, Codable, Sendable {
    case improving = "IMPROVING"
    case stagnant = "STAGNANT"
    case declining = "DECLINING"
    case insufficientData = "INSUFFICIENT_DATA"

    public static let increasing = ProgressionTrend.improving
    public static let stable = ProgressionTrend.stagnant
    public static let decreasing = ProgressionTrend.declining

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .improving: return "Improving"
        case .stagnant: return "Stagnant"
        case .declining: return "Declining"
        case .insufficientData: return "Insufficient Data"
        }
    }

    public var description: String {
        switch self {
        case .improving:
            return "Strength performance has increased over time (> +2% gain)."
        case .stagnant:
            return "Strength performance has plateaued within a +/- 2% margin."
        case .declining:
            return "Strength performance has decreased compared to baseline (< -2% change)."
        case .insufficientData:
            return "At least two completed sessions are required to determine a progression trend."
        }
    }

    public static func evaluate(relativeGainPercentage: Double, sessionCount: Int) -> ProgressionTrend {
        if sessionCount < 2 {
            return .insufficientData
        }
        if relativeGainPercentage >= 2.0 {
            return .improving
        } else if relativeGainPercentage <= -2.0 {
            return .declining
        } else {
            return .stagnant
        }
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let raw = try container.decode(String.self).uppercased().trimmingCharacters(in: .whitespacesAndNewlines)
        switch raw {
        case "IMPROVING", "INCREASING":
            self = .improving
        case "STAGNANT", "STABLE":
            self = .stagnant
        case "DECLINING", "DECREASING":
            self = .declining
        case "INSUFFICIENT_DATA", "INSUFFICIENTDATA":
            self = .insufficientData
        default:
            self = .insufficientData
        }
    }
}
