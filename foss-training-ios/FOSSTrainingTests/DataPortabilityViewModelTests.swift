import Foundation
import Testing
@testable import FOSSTraining

private final class MockPortabilityRepoForVM: DataPortabilityRepository, @unchecked Sendable {
    var backupToReturn = FullBackupData(exercises: [Exercise(id: 1, name: "Squat", primaryCategory: .resistance)])
    var summaryToReturn = ImportSummary(exercisesImported: 1)
    var csvToReturn = "training_id,training_date\n"
    var shouldThrowGenerate = false
    var shouldThrowRestore = false
    var shouldThrowCsvExport = false
    var shouldThrowCsvImport = false
    var shouldThrowPurge = false
    var purged = false

    func generateBackup() async throws -> FullBackupData {
        if shouldThrowGenerate { throw URLError(.badServerResponse) }
        return backupToReturn
    }

    func restoreBackup(_ backup: FullBackupData, mode: ImportMode) async throws -> ImportSummary {
        if shouldThrowRestore { throw URLError(.badServerResponse) }
        return summaryToReturn
    }

    func exportWorkoutsCsv() async throws -> String {
        if shouldThrowCsvExport { throw URLError(.badServerResponse) }
        return csvToReturn
    }

    func importWorkoutsCsv(_ csvContent: String) async throws -> ImportSummary {
        if shouldThrowCsvImport { throw URLError(.badServerResponse) }
        return summaryToReturn
    }

    func migrateToRemoteServer(serverUrl: URL, token: String?) async throws -> ImportSummary {
        summaryToReturn
    }

    func purgeLocalDatabase() async throws {
        if shouldThrowPurge { throw URLError(.badServerResponse) }
        purged = true
    }
}

private final class VMMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (HTTPURLResponse, Data))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host() == "vm-migration-mock.local"
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = VMMockURLProtocol.requestHandler else {
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

private func createVMMockClient(url: URL) -> NetworkClient {
    let config = URLSessionConfiguration.ephemeral
    config.protocolClasses = [VMMockURLProtocol.self]
    let session = URLSession(configuration: config)
    return NetworkClient(baseURLString: url.absoluteString, session: session)
}

@Suite("Data Portability ViewModel Tests")
@MainActor
struct DataPortabilityViewModelTests {

    @Test("DataPortabilityViewModel: JSON backup export success and failure")
    func testExportJson() async {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        // Success
        await vm.exportJsonBackup()
        #expect(vm.isExportingJson == true)
        #expect(vm.exportedJsonData != nil)
        #expect(vm.errorMessage == nil)

        // Failure
        repo.shouldThrowGenerate = true
        await vm.exportJsonBackup()
        #expect(vm.errorMessage != nil)
    }

    @Test("DataPortabilityViewModel: CSV workouts export success and failure")
    func testExportCsv() async {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        // Success
        await vm.exportWorkoutsCsv()
        #expect(vm.isExportingCsv == true)
        #expect(vm.exportedCsvString != nil)
        #expect(vm.errorMessage == nil)

        // Failure
        repo.shouldThrowCsvExport = true
        await vm.exportWorkoutsCsv()
        #expect(vm.errorMessage != nil)
    }

    @Test("DataPortabilityViewModel: import JSON decoding, conflict modal, and confirmation")
    func testImportJsonFlow() async throws {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        let validBackup = FullBackupData(exercises: [Exercise(id: 2, name: "Bench", primaryCategory: .resistance)])
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        let validData = try encoder.encode(validBackup)

        // Valid data shows modal
        vm.handleImportedJsonData(validData)
        #expect(vm.showConflictModal == true)
        #expect(vm.pendingBackupData != nil)
        #expect(vm.errorMessage == nil)

        // Confirm import
        await vm.confirmImport(mode: .merge)
        #expect(vm.showConflictModal == false)
        #expect(vm.pendingBackupData == nil)
        #expect(vm.successMessage != nil)
        #expect(vm.lastImportSummary?.exercisesImported == 1)

        // Confirm with nil pending does nothing
        await vm.confirmImport(mode: .overwrite)

        // Error during restore
        vm.handleImportedJsonData(validData)
        repo.shouldThrowRestore = true
        await vm.confirmImport(mode: .overwrite)
        #expect(vm.errorMessage != nil)

        // Invalid JSON data sets error
        let invalidData = "invalid-json".data(using: .utf8)!
        vm.handleImportedJsonData(invalidData)
        #expect(vm.errorMessage != nil)
    }

    @Test("DataPortabilityViewModel: CSV import flow and failure")
    func testImportCsvFlow() async {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        // Success
        await vm.handleImportedCsvString("training_id\n1\n")
        #expect(vm.successMessage != nil)
        #expect(vm.lastImportSummary != nil)

        // Failure
        repo.shouldThrowCsvImport = true
        await vm.handleImportedCsvString("training_id\n1\n")
        #expect(vm.errorMessage != nil)
    }

    @Test("DataPortabilityViewModel: server connection test branches")
    func testServerConnectionTest() async {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        // Invalid URL
        vm.serverUrlString = "not a url"
        await vm.testServerConnection()
        #expect(vm.isConnectionSuccessful == false)
        #expect(vm.errorMessage == "Invalid server URL.")

        // Valid URL (connection failure to localhost in test)
        vm.serverUrlString = "http://localhost:9999"
        await vm.testServerConnection()
        #expect(vm.isConnectionSuccessful == false)
        #expect(vm.errorMessage != nil)

        // Valid URL - connection success
        let coord = CloudMigrationCoordinator(localRepository: repo) { url in
            createVMMockClient(url: url)
        }
        let vmSuccess = DataPortabilityViewModel(portabilityRepository: repo, migrationCoordinator: coord)
        vmSuccess.serverUrlString = "https://vm-migration-mock.local"
        VMMockURLProtocol.requestHandler = { request in
            let json = "[]".data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }
        await vmSuccess.testServerConnection()
        #expect(vmSuccess.isConnectionSuccessful == true)
        #expect(vmSuccess.errorMessage == nil)
    }

    @Test("DataPortabilityViewModel: migration execution branches")
    func testMigrationExecution() async {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        // Invalid URL
        vm.serverUrlString = "not a url"
        await vm.executeMigration()
        #expect(vm.errorMessage == "Invalid server URL.")

        // Migration error
        vm.serverUrlString = "http://localhost:9999"
        await vm.executeMigration()
        #expect(vm.errorMessage != nil)
        #expect(vm.isMigrating == false)

        // Successful migration
        let coord = CloudMigrationCoordinator(localRepository: repo) { url in
            createVMMockClient(url: url)
        }
        let vmSuccess = DataPortabilityViewModel(portabilityRepository: repo, migrationCoordinator: coord)
        vmSuccess.serverUrlString = "https://vm-migration-mock.local"
        VMMockURLProtocol.requestHandler = { request in
            let json = "{\"exercisesImported\": 3}".data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }
        await vmSuccess.executeMigration()
        #expect(vmSuccess.isMigrating == false)
        #expect(vmSuccess.showMigrationSheet == false)
        #expect(vmSuccess.lastImportSummary?.exercisesImported == 3)
        #expect(vmSuccess.successMessage != nil)
    }

    @Test("DataPortabilityViewModel: purge database validation and execution")
    func testPurgeDatabase() async {
        let repo = MockPortabilityRepoForVM()
        let vm = DataPortabilityViewModel(portabilityRepository: repo)

        // Invalid confirmation text
        vm.purgeConfirmationText = "NO"
        let failedValidation = await vm.purgeDatabase()
        #expect(failedValidation == false)
        #expect(vm.errorMessage == "Please type 'DELETE' to confirm database purge.")
        #expect(repo.purged == false)

        // Valid confirmation text
        vm.purgeConfirmationText = "DELETE"
        let success = await vm.purgeDatabase()
        #expect(success == true)
        #expect(repo.purged == true)
        #expect(vm.successMessage == "Local database completely cleared.")
        #expect(vm.purgeConfirmationText.isEmpty)

        // Purge repository error
        repo.shouldThrowPurge = true
        vm.purgeConfirmationText = "delete"
        let failedRepo = await vm.purgeDatabase()
        #expect(failedRepo == false)
        #expect(vm.errorMessage != nil)
    }
}
