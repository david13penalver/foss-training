import Foundation

public struct ImportSummary: Codable, Hashable, Sendable, Equatable {
    public let exercisesImported: Int
    public let sessionsImported: Int
    public let programsImported: Int
    public let trainingsImported: Int
    public let bodyweightImported: Int

    public var totalImported: Int {
        exercisesImported + sessionsImported + programsImported + trainingsImported + bodyweightImported
    }

    public init(
        exercisesImported: Int = 0,
        sessionsImported: Int = 0,
        programsImported: Int = 0,
        trainingsImported: Int = 0,
        bodyweightImported: Int = 0
    ) {
        self.exercisesImported = exercisesImported
        self.sessionsImported = sessionsImported
        self.programsImported = programsImported
        self.trainingsImported = trainingsImported
        self.bodyweightImported = bodyweightImported
    }
}
