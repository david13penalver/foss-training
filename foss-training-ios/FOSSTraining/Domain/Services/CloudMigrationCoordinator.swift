import Foundation

public enum MigrationState: Equatable, Sendable {
    case idle
    case pingingServer
    case exportingLocalBackup
    case uploadingBackup
    case completed(ImportSummary)
    case failed(String)
}

@MainActor
public final class CloudMigrationCoordinator {
    private let localRepository: DataPortabilityRepository
    let clientFactory: @Sendable (URL) -> NetworkClient
    public var state: MigrationState = .idle

    public init(
        localRepository: DataPortabilityRepository,
        clientFactory: (@Sendable (URL) -> NetworkClient)? = nil
    ) {
        self.localRepository = localRepository
        self.clientFactory = clientFactory ?? { url in NetworkClient(baseURLString: url.absoluteString) }
    }

    public func testConnection(serverUrl: URL) async -> Bool {
        self.state = .pingingServer
        do {
            let client = clientFactory(serverUrl)
            let _: [Exercise] = try await client.get(endpoint: "/api/exercises")
            self.state = .idle
            return true
        } catch {
            self.state = .failed("Connection failed: \(error.localizedDescription)")
            return false
        }
    }

    public func migrate(
        serverUrl: URL,
        token: String? = nil,
        purgeLocalAfterSuccess: Bool = false
    ) async throws -> ImportSummary {
        self.state = .exportingLocalBackup
        let backup: FullBackupData
        do {
            backup = try await localRepository.generateBackup()
        } catch {
            self.state = .failed("Backup export failed: \(error.localizedDescription)")
            throw error
        }

        self.state = .uploadingBackup
        let client = clientFactory(serverUrl)
        let summaryDTO: ImportSummaryDTO
        do {
            summaryDTO = try await client.post(endpoint: "/api/data/import/backup", body: backup)
        } catch {
            self.state = .failed("Upload failed: \(error.localizedDescription)")
            throw error
        }

        let summary = ImportSummary(
            exercisesImported: summaryDTO.exercisesImported ?? 0,
            sessionsImported: summaryDTO.sessionsImported ?? 0,
            programsImported: summaryDTO.programsImported ?? 0,
            trainingsImported: summaryDTO.trainingsImported ?? 0,
            bodyweightImported: summaryDTO.bodyweightsImported ?? 0
        )

        if purgeLocalAfterSuccess {
            try await localRepository.purgeLocalDatabase()
        }

        self.state = .completed(summary)
        return summary
    }
}
