import Foundation

public enum ScoringFormula: String, Codable, CaseIterable, Identifiable, Sendable {
    case dots = "DOTS"
    case wilks = "WILKS"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .dots: return "DOTS"
        case .wilks: return "Wilks"
        }
    }
}
