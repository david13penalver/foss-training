import Foundation
import SwiftData

@Model
public final class SDAthleteProfile {
    @Attribute(.unique) public var id: Int
    public var displayName: String
    public var genderRaw: String
    public var dateOfBirth: Date?
    public var heightCm: Double?
    public var experienceLevelRaw: String
    public var targetGoal: String?
    public var preferredUnitRaw: String

    public init(
        id: Int = 1,
        displayName: String = "Athlete",
        genderRaw: String = Gender.male.rawValue,
        dateOfBirth: Date? = nil,
        heightCm: Double? = 175.0,
        experienceLevelRaw: String = ProgramLevel.intermediate.rawValue,
        targetGoal: String? = nil,
        preferredUnitRaw: String = WeightUnit.kg.rawValue
    ) {
        self.id = id
        self.displayName = displayName
        self.genderRaw = genderRaw
        self.dateOfBirth = dateOfBirth
        self.heightCm = heightCm
        self.experienceLevelRaw = experienceLevelRaw
        self.targetGoal = targetGoal
        self.preferredUnitRaw = preferredUnitRaw
    }

    public func toDomain() -> AthleteProfile {
        AthleteProfile(
            id: id,
            displayName: displayName,
            gender: Gender(rawValue: genderRaw) ?? .male,
            dateOfBirth: dateOfBirth,
            heightCm: heightCm,
            experienceLevel: ProgramLevel(rawValue: experienceLevelRaw) ?? .intermediate,
            targetGoal: targetGoal,
            preferredUnit: WeightUnit(rawValue: preferredUnitRaw) ?? .kg
        )
    }

    public static func fromDomain(_ profile: AthleteProfile) -> SDAthleteProfile {
        SDAthleteProfile(
            id: profile.id,
            displayName: profile.displayName,
            genderRaw: profile.gender.rawValue,
            dateOfBirth: profile.dateOfBirth,
            heightCm: profile.heightCm,
            experienceLevelRaw: profile.experienceLevel.rawValue,
            targetGoal: profile.targetGoal,
            preferredUnitRaw: profile.preferredUnit.rawValue
        )
    }
}
