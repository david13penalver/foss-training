import Foundation

public enum WeightUnit: String, Codable, CaseIterable, Identifiable, Sendable {
    case kg = "KG"
    case lbs = "LBS"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .kg: return "kg"
        case .lbs: return "lbs"
        }
    }
}
