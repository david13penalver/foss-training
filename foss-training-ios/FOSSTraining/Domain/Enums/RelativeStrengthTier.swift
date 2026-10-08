import Foundation

public enum RelativeStrengthTier: String, Codable, CaseIterable, Identifiable, Sendable {
    case novice = "NOVICE"
    case intermediate = "INTERMEDIATE"
    case advanced = "ADVANCED"
    case elite = "ELITE"
    case internationalElite = "INTERNATIONAL_ELITE"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .novice: return "Novice"
        case .intermediate: return "Intermediate"
        case .advanced: return "Advanced"
        case .elite: return "Elite"
        case .internationalElite: return "International Elite"
        }
    }

    public var description: String {
        switch self {
        case .novice:
            return "Developing foundational mechanics and base strength (< 250 DOTS)."
        case .intermediate:
            return "Consistent lifter with multi-year structured training (250–324.9 DOTS)."
        case .advanced:
            return "Regional/state competitive level lifter (325–399.9 DOTS)."
        case .elite:
            return "National championship contender (400–474.9 DOTS)."
        case .internationalElite:
            return "World-class international caliber (≥ 475 DOTS)."
        }
    }

    public static func evaluate(dotsScore: Double) -> RelativeStrengthTier {
        if dotsScore < 250 {
            return .novice
        } else if dotsScore < 325 {
            return .intermediate
        } else if dotsScore < 400 {
            return .advanced
        } else if dotsScore < 475 {
            return .elite
        } else {
            return .internationalElite
        }
    }
}
