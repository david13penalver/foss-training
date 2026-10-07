import Foundation

public struct ImportSummaryDTO: Codable, Sendable {
    public let exercisesImported: Int?
    public let sessionsImported: Int?
    public let trainingsImported: Int?
    public let bodyweightsImported: Int?
}

public final class RemotePortabilityRepository: DataPortabilityRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func exportFullBackup() async throws -> BackupDataPayload {
        try await client.get(endpoint: "/api/data/export/backup")
    }

    public func importFullBackup(payload: BackupDataPayload) async throws -> Int {
        let summary: ImportSummaryDTO = try await client.post(endpoint: "/api/data/import/backup", body: payload)
        return (summary.exercisesImported ?? 0) + (summary.sessionsImported ?? 0) + (summary.trainingsImported ?? 0)
    }

    public func exportWorkoutsCSV() async throws -> String {
        let url = URL(string: "http://localhost:8080/api/data/export/workouts.csv")!
        let (data, _) = try await URLSession.shared.data(from: url)
        return String(data: data, encoding: .utf8) ?? ""
    }
}
