import Foundation

public struct AthleteProfile: Identifiable, Codable, Hashable, Sendable {
    public var id: Int
    public var displayName: String
    public var gender: Gender
    public var dateOfBirth: Date?
    public var heightCm: Double?
    public var experienceLevel: ProgramLevel
    public var targetGoal: String?
    public var preferredUnit: WeightUnit

    public init(
        id: Int = 1,
        displayName: String = "Athlete",
        gender: Gender = .male,
        dateOfBirth: Date? = nil,
        heightCm: Double? = nil,
        experienceLevel: ProgramLevel = .intermediate,
        targetGoal: String? = nil,
        preferredUnit: WeightUnit = .kg
    ) {
        self.id = id
        self.displayName = displayName
        self.gender = gender
        self.dateOfBirth = dateOfBirth
        self.heightCm = heightCm
        self.experienceLevel = experienceLevel
        self.targetGoal = targetGoal
        self.preferredUnit = preferredUnit
    }

    public static let `default` = AthleteProfile(
        id: 1,
        displayName: "Athlete",
        gender: .male,
        dateOfBirth: nil,
        heightCm: 175.0,
        experienceLevel: .intermediate,
        targetGoal: "Strength & Hypertrophy",
        preferredUnit: .kg
    )
}
