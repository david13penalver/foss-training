import Foundation

public protocol DataPortabilityRepository: Sendable {
    /// Generates full JSON backup data aggregate from local storage
    func generateBackup() async throws -> FullBackupData

    /// Restores database entities from full JSON backup snapshot
    func restoreBackup(_ backup: FullBackupData, mode: ImportMode) async throws -> ImportSummary

    /// Generates UTF-8 encoded CSV string of all workout execution history
    func exportWorkoutsCsv() async throws -> String

    /// Imports workout rows from a CSV string
    func importWorkoutsCsv(_ csvContent: String) async throws -> ImportSummary

    /// Transmits local backup payload to remote Spring Boot API and verifies sync
    func migrateToRemoteServer(serverUrl: URL, token: String?) async throws -> ImportSummary

    /// Deletes all local records across all entities
    func purgeLocalDatabase() async throws

    // Backwards-compatibility aliases:
    func exportFullBackup() async throws -> FullBackupData
    func importFullBackup(payload: FullBackupData) async throws -> Int
    func exportWorkoutsCSV() async throws -> String
}

extension DataPortabilityRepository {
    public func exportFullBackup() async throws -> FullBackupData {
        try await generateBackup()
    }

    public func importFullBackup(payload: FullBackupData) async throws -> Int {
        let summary = try await restoreBackup(payload, mode: .overwrite)
        return summary.totalImported
    }

    public func exportWorkoutsCSV() async throws -> String {
        try await exportWorkoutsCsv()
    }
}
