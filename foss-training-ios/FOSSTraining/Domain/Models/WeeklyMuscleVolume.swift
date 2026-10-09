import Foundation

public struct MuscleGroupVolume: Identifiable, Codable, Hashable, Sendable {
    public var id: String { muscleGroup }
    public let muscleGroup: String
    public let muscleGroupName: String
    public let directSets: Int
    public let indirectSets: Int
    public let effectiveSets: Double
    public let totalVolumeKg: Double
    public let status: HypertrophyVolumeStatus

    public init(
        muscleGroup: String,
        muscleGroupName: String,
        directSets: Int,
        indirectSets: Int,
        effectiveSets: Double,
        totalVolumeKg: Double,
        status: HypertrophyVolumeStatus
    ) {
        self.muscleGroup = muscleGroup
        self.muscleGroupName = muscleGroupName
        self.directSets = directSets
        self.indirectSets = indirectSets
        self.effectiveSets = effectiveSets
        self.totalVolumeKg = totalVolumeKg
        self.status = status
    }
}

public struct WeeklyMuscleVolume: Codable, Hashable, Sendable {
    public let startDate: Date
    public let endDate: Date
    public let totalWorkingSets: Int
    public let totalVolumeKg: Double
    public let muscleVolumes: [MuscleGroupVolume]
    public let pushPullRatio: Double
    public let upperLowerRatio: Double
    public let neglectedMuscleGroups: [String]
    public let optimalMuscleGroups: [String]
    public let overtrainedMuscleGroups: [String]
    public let recommendations: [String]

    public var totalSets: Int { totalWorkingSets }

    public init(
        startDate: Date,
        endDate: Date,
        totalWorkingSets: Int,
        totalVolumeKg: Double,
        muscleVolumes: [MuscleGroupVolume],
        pushPullRatio: Double,
        upperLowerRatio: Double,
        neglectedMuscleGroups: [String],
        optimalMuscleGroups: [String],
        overtrainedMuscleGroups: [String],
        recommendations: [String]
    ) {
        self.startDate = startDate
        self.endDate = endDate
        self.totalWorkingSets = totalWorkingSets
        self.totalVolumeKg = totalVolumeKg
        self.muscleVolumes = muscleVolumes
        self.pushPullRatio = pushPullRatio
        self.upperLowerRatio = upperLowerRatio
        self.neglectedMuscleGroups = neglectedMuscleGroups
        self.optimalMuscleGroups = optimalMuscleGroups
        self.overtrainedMuscleGroups = overtrainedMuscleGroups
        self.recommendations = recommendations
    }
}
