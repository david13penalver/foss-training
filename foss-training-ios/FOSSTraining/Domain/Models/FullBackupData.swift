import Foundation

public struct FullBackupData: Codable, Sendable, Equatable {
    public var exportVersion: String
    public var exportedAt: Date
    public var exercises: [Exercise]
    public var sessions: [Session]
    public var programs: [TrainingProgram]
    public var trainings: [Training]
    public var bodyweightEntries: [BodyweightEntry]

    public init(
        exportVersion: String = "1.0",
        exportedAt: Date = Date(),
        exercises: [Exercise] = [],
        sessions: [Session] = [],
        programs: [TrainingProgram] = [],
        trainings: [Training] = [],
        bodyweightEntries: [BodyweightEntry] = []
    ) {
        self.exportVersion = exportVersion
        self.exportedAt = exportedAt
        self.exercises = exercises
        self.sessions = sessions
        self.programs = programs
        self.trainings = trainings
        self.bodyweightEntries = bodyweightEntries
    }
}

public typealias BackupDataPayload = FullBackupData
