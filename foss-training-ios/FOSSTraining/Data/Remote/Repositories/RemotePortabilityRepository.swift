import Foundation

public struct ImportSummaryDTO: Codable, Sendable {
    public let exercisesImported: Int?
    public let sessionsImported: Int?
    public let programsImported: Int?
    public let trainingsImported: Int?
    public let bodyweightsImported: Int?

    public init(
        exercisesImported: Int? = nil,
        sessionsImported: Int? = nil,
        programsImported: Int? = nil,
        trainingsImported: Int? = nil,
        bodyweightsImported: Int? = nil
    ) {
        self.exercisesImported = exercisesImported
        self.sessionsImported = sessionsImported
        self.programsImported = programsImported
        self.trainingsImported = trainingsImported
        self.bodyweightsImported = bodyweightsImported
    }
}

public final class RemotePortabilityRepository: DataPortabilityRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func generateBackup() async throws -> FullBackupData {
        try await client.get(endpoint: "/api/data/export/backup")
    }

    public func restoreBackup(_ backup: FullBackupData, mode: ImportMode = .overwrite) async throws -> ImportSummary {
        let summaryDTO: ImportSummaryDTO = try await client.post(endpoint: "/api/data/import/backup", body: backup)
        return ImportSummary(
            exercisesImported: summaryDTO.exercisesImported ?? 0,
            sessionsImported: summaryDTO.sessionsImported ?? 0,
            programsImported: summaryDTO.programsImported ?? 0,
            trainingsImported: summaryDTO.trainingsImported ?? 0,
            bodyweightImported: summaryDTO.bodyweightsImported ?? 0
        )
    }

    public func exportWorkoutsCsv() async throws -> String {
        let url = URL(string: "http://localhost:8080/api/data/export/workouts.csv")!
        let (data, _) = try await URLSession.shared.data(from: url)
        return String(data: data, encoding: .utf8) ?? ""
    }

    public func exportWorkoutsCSV() async throws -> String {
        try await exportWorkoutsCsv()
    }

    public func importWorkoutsCsv(_ csvContent: String) async throws -> ImportSummary {
        return ImportSummary()
    }

    public func migrateToRemoteServer(serverUrl: URL, token: String? = nil) async throws -> ImportSummary {
        let backup = try await generateBackup()
        let summaryDTO: ImportSummaryDTO = try await client.post(endpoint: "/api/data/import/backup", body: backup)
        return ImportSummary(
            exercisesImported: summaryDTO.exercisesImported ?? 0,
            sessionsImported: summaryDTO.sessionsImported ?? 0,
            programsImported: summaryDTO.programsImported ?? 0,
            trainingsImported: summaryDTO.trainingsImported ?? 0,
            bodyweightImported: summaryDTO.bodyweightsImported ?? 0
        )
    }

    public func purgeLocalDatabase() async throws {}
}
