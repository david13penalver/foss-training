import Foundation
import Testing
@testable import FOSSTraining

private final class MockPortabilityRepoForMigration: DataPortabilityRepository, @unchecked Sendable {
    var generatedBackup: FullBackupData = FullBackupData(
        exercises: [Exercise(id: 1, name: "Squat", primaryCategory: .resistance)]
    )
    var shouldFailGenerate: Bool = false
    var shouldFailPurge: Bool = false
    var wasPurged: Bool = false

    func generateBackup() async throws -> FullBackupData {
        if shouldFailGenerate { throw URLError(.cannotOpenFile) }
        return generatedBackup
    }

    func restoreBackup(_ backup: FullBackupData, mode: ImportMode) async throws -> ImportSummary {
        ImportSummary(exercisesImported: backup.exercises.count)
    }

    func exportWorkoutsCsv() async throws -> String { "" }
    func importWorkoutsCsv(_ csvContent: String) async throws -> ImportSummary { ImportSummary() }
    func migrateToRemoteServer(serverUrl: URL, token: String?) async throws -> ImportSummary { ImportSummary() }

    func purgeLocalDatabase() async throws {
        if shouldFailPurge { throw URLError(.cannotRemoveFile) }
        wasPurged = true
    }
}

private final class MigrationMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (HTTPURLResponse, Data))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host() == "migration-mock.local"
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = MigrationMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: URLError(.badServerResponse))
            return
        }
        do {
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            client?.urlProtocol(self, didLoad: data)
            client?.urlProtocolDidFinishLoading(self)
        } catch {
            client?.urlProtocol(self, didFailWithError: error)
        }
    }

    override func stopLoading() {}
}

private func createMockClient(url: URL) -> NetworkClient {
    let config = URLSessionConfiguration.ephemeral
    config.protocolClasses = [MigrationMockURLProtocol.self]
    let session = URLSession(configuration: config)
    return NetworkClient(baseURLString: url.absoluteString, session: session)
}

@Suite("Cloud Migration Coordinator Tests", .serialized)
@MainActor
struct CloudMigrationCoordinatorTests {

    @Test("CloudMigrationCoordinator: testConnection ping success and failure")
    func testConnectionPing() async {
        let repo = MockPortabilityRepoForMigration()
        let coordinator = CloudMigrationCoordinator(localRepository: repo) { url in
            createMockClient(url: url)
        }
        let serverUrl = URL(string: "https://migration-mock.local")!

        // Success
        MigrationMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path() == "/api/exercises")
            let json = "[]".data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let isOnline = await coordinator.testConnection(serverUrl: serverUrl)
        #expect(isOnline == true)
        #expect(coordinator.state == .idle)

        // Failure
        MigrationMockURLProtocol.requestHandler = { _ in
            throw URLError(.cannotConnectToHost)
        }

        let isOffline = await coordinator.testConnection(serverUrl: serverUrl)
        #expect(isOffline == false)
        if case .failed = coordinator.state {
            #expect(true)
        } else {
            Issue.record("Expected failed state")
        }
    }

    @Test("CloudMigrationCoordinator: migrate successfully and purge local records")
    func testSuccessfulMigrationWithPurge() async throws {
        let repo = MockPortabilityRepoForMigration()
        let coordinator = CloudMigrationCoordinator(localRepository: repo) { url in
            createMockClient(url: url)
        }
        let serverUrl = URL(string: "https://migration-mock.local")!

        MigrationMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path() == "/api/data/import/backup")
            let json = """
            {
                "exercisesImported": 1,
                "sessionsImported": 2,
                "programsImported": 3,
                "trainingsImported": 4,
                "bodyweightsImported": 5
            }
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let summary = try await coordinator.migrate(serverUrl: serverUrl, purgeLocalAfterSuccess: true)
        #expect(summary.exercisesImported == 1)
        #expect(summary.sessionsImported == 2)
        #expect(summary.programsImported == 3)
        #expect(summary.trainingsImported == 4)
        #expect(summary.bodyweightImported == 5)
        #expect(summary.totalImported == 15)
        #expect(repo.wasPurged == true)

        if case .completed(let completedSummary) = coordinator.state {
            #expect(completedSummary.totalImported == 15)
        } else {
            Issue.record("Expected completed state")
        }
    }

    @Test("CloudMigrationCoordinator: default clientFactory closure invocation")
    func testDefaultClientFactory() {
        let repo = MockPortabilityRepoForMigration()
        let coordinator = CloudMigrationCoordinator(localRepository: repo)
        let client = coordinator.clientFactory(URL(string: "https://example.com")!)
        _ = client
    }

    @Test("CloudMigrationCoordinator: migration with nil DTO fields falls back to zero")
    func testMigrationWithNilDtoFields() async throws {
        let repo = MockPortabilityRepoForMigration()
        let coordinator = CloudMigrationCoordinator(localRepository: repo) { url in
            createMockClient(url: url)
        }
        let serverUrl = URL(string: "https://migration-mock.local")!

        MigrationMockURLProtocol.requestHandler = { _ in
            let json = "{}".data(using: .utf8)!
            let response = HTTPURLResponse(url: serverUrl, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let summary = try await coordinator.migrate(serverUrl: serverUrl, purgeLocalAfterSuccess: false)
        #expect(summary.exercisesImported == 0)
        #expect(summary.sessionsImported == 0)
        #expect(summary.programsImported == 0)
        #expect(summary.trainingsImported == 0)
        #expect(summary.bodyweightImported == 0)
        #expect(summary.totalImported == 0)
    }

    @Test("CloudMigrationCoordinator: export failure aborts and does not purge")
    func testExportFailure() async {
        let repo = MockPortabilityRepoForMigration()
        repo.shouldFailGenerate = true
        let coordinator = CloudMigrationCoordinator(localRepository: repo) { url in
            createMockClient(url: url)
        }
        let serverUrl = URL(string: "https://migration-mock.local")!

        await #expect(throws: Error.self) {
            _ = try await coordinator.migrate(serverUrl: serverUrl, purgeLocalAfterSuccess: true)
        }
        #expect(repo.wasPurged == false)
        if case .failed = coordinator.state {
            #expect(true)
        } else {
            Issue.record("Expected failed state")
        }
    }

    @Test("CloudMigrationCoordinator: upload failure aborts and preserves local data")
    func testUploadFailure() async {
        let repo = MockPortabilityRepoForMigration()
        let coordinator = CloudMigrationCoordinator(localRepository: repo) { url in
            createMockClient(url: url)
        }
        let serverUrl = URL(string: "https://migration-mock.local")!

        MigrationMockURLProtocol.requestHandler = { _ in
            throw URLError(.timedOut)
        }

        await #expect(throws: Error.self) {
            _ = try await coordinator.migrate(serverUrl: serverUrl, purgeLocalAfterSuccess: true)
        }
        #expect(repo.wasPurged == false)
        if case .failed = coordinator.state {
            #expect(true)
        } else {
            Issue.record("Expected failed state")
        }
    }
}
