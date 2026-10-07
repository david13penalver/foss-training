import Foundation
import SwiftData

@Model
public final class SDTraining {
    @Attribute(.unique) public var id: Int
    public var name: String
    public var trainingDescription: String?
    public var trainingDate: Date
    public var startTime: Date?
    public var endTime: Date?
    public var statusRaw: String
    public var notes: String?
    public var overallRpe: Double?
    public var programId: Int?
    @Relationship(deleteRule: .cascade) public var loggedExercises: [SDSessionExercise]

    public init(
        id: Int,
        name: String,
        trainingDescription: String? = nil,
        trainingDate: Date = Date(),
        startTime: Date? = nil,
        endTime: Date? = nil,
        status: TrainingStatus = .planned,
        notes: String? = nil,
        overallRpe: Double? = nil,
        programId: Int? = nil,
        loggedExercises: [SDSessionExercise] = []
    ) {
        self.id = id
        self.name = name
        self.trainingDescription = trainingDescription
        self.trainingDate = trainingDate
        self.startTime = startTime
        self.endTime = endTime
        self.statusRaw = status.rawValue
        self.notes = notes
        self.overallRpe = overallRpe
        self.programId = programId
        self.loggedExercises = loggedExercises
    }

    public func toDomain() -> Training {
        Training(
            id: id,
            name: name,
            description: trainingDescription,
            trainingDate: trainingDate,
            startTime: startTime,
            endTime: endTime,
            status: TrainingStatus(rawValue: statusRaw) ?? .planned,
            notes: notes,
            overallRpe: overallRpe,
            programId: programId,
            loggedExercises: loggedExercises.map { $0.toDomain() }
        )
    }

    public static func fromDomain(_ training: Training) -> SDTraining {
        SDTraining(
            id: training.id,
            name: training.name,
            trainingDescription: training.description,
            trainingDate: training.trainingDate,
            startTime: training.startTime,
            endTime: training.endTime,
            status: training.status,
            notes: training.notes,
            overallRpe: training.overallRpe,
            programId: training.programId,
            loggedExercises: training.loggedExercises.map { SDSessionExercise.fromDomain($0) }
        )
    }
}

@Model
public final class SDBodyweightEntry {
    @Attribute(.unique) public var id: Int
    public var weightKg: Double
    public var measuredDate: Date
    public var notes: String?

    public init(id: Int, weightKg: Double, measuredDate: Date = Date(), notes: String? = nil) {
        self.id = id
        self.weightKg = weightKg
        self.measuredDate = measuredDate
        self.notes = notes
    }

    public func toDomain() -> BodyweightEntry {
        BodyweightEntry(id: id, weightKg: weightKg, measuredDate: measuredDate, notes: notes)
    }

    public static func fromDomain(_ entry: BodyweightEntry) -> SDBodyweightEntry {
        SDBodyweightEntry(id: entry.id, weightKg: entry.weightKg, measuredDate: entry.measuredDate, notes: entry.notes)
    }
}
