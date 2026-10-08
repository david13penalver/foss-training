import Foundation

public enum Gender: String, Codable, CaseIterable, Identifiable, Sendable {
    case male = "MALE"
    case female = "FEMALE"
    case other = "OTHER"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .male: return "Male"
        case .female: return "Female"
        case .other: return "Other"
        }
    }
}

public typealias AthleteGender = Gender
