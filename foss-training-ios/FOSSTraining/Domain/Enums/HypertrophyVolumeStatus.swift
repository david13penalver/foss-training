import Foundation

public enum HypertrophyVolumeStatus: String, CaseIterable, Identifiable, Codable, Sendable {
    case belowMev = "BELOW_MEV"
    case maintenance = "MAINTENANCE"
    case adaptive = "ADAPTIVE"
    case approachingMrv = "APPROACHING_MRV"
    case exceededMrv = "EXCEEDED_MRV"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .belowMev: return "Below MEV"
        case .maintenance: return "Maintenance"
        case .adaptive: return "Optimal Adaptive"
        case .approachingMrv: return "Approaching MRV"
        case .exceededMrv: return "Exceeded MRV"
        }
    }

    public var description: String {
        switch self {
        case .belowMev:
            return "< 6 weekly sets. Sub-stimulative volume; below Minimum Effective Volume (MEV)."
        case .maintenance:
            return "6–9 weekly sets. Minimum Effective Volume (MEV); preserves current muscle mass with modest adaptation."
        case .adaptive:
            return "10–20 weekly sets. Maximum Adaptive Volume (MAV); optimal evidence-based sweet spot for muscle hypertrophy."
        case .approachingMrv:
            return "21–25 weekly sets. High volume approaching Maximum Recoverable Volume (MRV); monitor recovery carefully."
        case .exceededMrv:
            return "> 25 weekly sets. Excessive volume exceeding MRV; systemic recovery compromised with diminished returns."
        }
    }

    public static func from(sets: Double) -> HypertrophyVolumeStatus {
        if sets < 6.0 {
            return .belowMev
        } else if sets < 10.0 {
            return .maintenance
        } else if sets <= 20.0 {
            return .adaptive
        } else if sets <= 25.0 {
            return .approachingMrv
        } else {
            return .exceededMrv
        }
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let raw = try container.decode(String.self).uppercased().trimmingCharacters(in: .whitespacesAndNewlines)
        switch raw {
        case "BELOW_MEV", "UNDERTRAINED", "BELOWMEV":
            self = .belowMev
        case "MAINTENANCE":
            self = .maintenance
        case "ADAPTIVE", "OPTIMAL", "MAV":
            self = .adaptive
        case "APPROACHING_MRV", "APPROACHINGMRV":
            self = .approachingMrv
        case "EXCEEDED_MRV", "OVERTRAINED", "EXCEEDEDMRV":
            self = .exceededMrv
        default:
            self = .belowMev
        }
    }
}
