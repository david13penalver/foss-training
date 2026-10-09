import Foundation
import SwiftUI

@Observable
@MainActor
public final class DataPortabilityViewModel {
    private let portabilityRepository: DataPortabilityRepository
    private let migrationCoordinator: CloudMigrationCoordinator

    // State
    public var isLoading: Bool = false
    public var errorMessage: String?
    public var successMessage: String?
    public var lastImportSummary: ImportSummary?

    // File Exporter
    public var exportedJsonData: Data?
    public var exportedCsvString: String?
    public var isExportingJson: Bool = false
    public var isExportingCsv: Bool = false

    // File Importer & Conflict Modal
    public var isImportingJson: Bool = false
    public var pendingBackupData: FullBackupData?
    public var showConflictModal: Bool = false
    public var selectedImportMode: ImportMode = .merge

    // Cloud Migration
    public var serverUrlString: String = "http://localhost:8080"
    public var isTestingConnection: Bool = false
    public var isConnectionSuccessful: Bool?
    public var isMigrating: Bool = false
    public var purgeAfterMigration: Bool = false
    public var showMigrationSheet: Bool = false

    // Danger Zone
    public var showPurgeConfirmation: Bool = false
    public var purgeConfirmationText: String = ""

    public init(
        portabilityRepository: DataPortabilityRepository,
        migrationCoordinator: CloudMigrationCoordinator? = nil
    ) {
        self.portabilityRepository = portabilityRepository
        self.migrationCoordinator = migrationCoordinator ?? CloudMigrationCoordinator(localRepository: portabilityRepository)
    }

    public func exportJsonBackup() async {
        self.isLoading = true
        self.errorMessage = nil
        do {
            let backup = try await portabilityRepository.generateBackup()
            let encoder = JSONEncoder()
            encoder.dateEncodingStrategy = .iso8601
            encoder.outputFormatting = .prettyPrinted
            let data = try encoder.encode(backup)
            self.exportedJsonData = data
            self.isExportingJson = true
            self.isLoading = false
        } catch {
            self.errorMessage = "Failed to export JSON backup: \(error.localizedDescription)"
            self.isLoading = false
        }
    }

    public func exportWorkoutsCsv() async {
        self.isLoading = true
        self.errorMessage = nil
        do {
            let csv = try await portabilityRepository.exportWorkoutsCsv()
            self.exportedCsvString = csv
            self.isExportingCsv = true
            self.isLoading = false
        } catch {
            self.errorMessage = "Failed to export CSV workouts: \(error.localizedDescription)"
            self.isLoading = false
        }
    }

    public func handleImportedJsonData(_ data: Data) {
        self.errorMessage = nil
        do {
            let decoder = JSONDecoder()
            decoder.dateDecodingStrategy = .iso8601
            let backup = try decoder.decode(FullBackupData.self, from: data)
            self.pendingBackupData = backup
            self.showConflictModal = true
        } catch {
            self.errorMessage = "Invalid backup file: \(error.localizedDescription)"
        }
    }

    public func confirmImport(mode: ImportMode) async {
        guard let pending = pendingBackupData else { return }
        self.isLoading = true
        self.errorMessage = nil
        do {
            let summary = try await portabilityRepository.restoreBackup(pending, mode: mode)
            self.lastImportSummary = summary
            self.successMessage = "Successfully restored \(summary.totalImported) records."
            self.pendingBackupData = nil
            self.showConflictModal = false
            self.isLoading = false
        } catch {
            self.errorMessage = "Import failed: \(error.localizedDescription)"
            self.isLoading = false
        }
    }

    public func handleImportedCsvString(_ csv: String) async {
        self.isLoading = true
        self.errorMessage = nil
        do {
            let summary = try await portabilityRepository.importWorkoutsCsv(csv)
            self.lastImportSummary = summary
            self.successMessage = "Successfully imported \(summary.trainingsImported) workouts from CSV."
            self.isLoading = false
        } catch {
            self.errorMessage = "CSV import failed: \(error.localizedDescription)"
            self.isLoading = false
        }
    }

    public func testServerConnection() async {
        guard let url = URL(string: serverUrlString), url.scheme != nil else {
            self.errorMessage = "Invalid server URL."
            self.isConnectionSuccessful = false
            return
        }

        self.isTestingConnection = true
        self.errorMessage = nil
        let success = await migrationCoordinator.testConnection(serverUrl: url)
        self.isConnectionSuccessful = success
        self.isTestingConnection = false
        if !success {
            self.errorMessage = "Could not connect to server at \(serverUrlString)"
        }
    }

    public func executeMigration() async {
        guard let url = URL(string: serverUrlString), url.scheme != nil else {
            self.errorMessage = "Invalid server URL."
            return
        }

        self.isMigrating = true
        self.errorMessage = nil
        do {
            let summary = try await migrationCoordinator.migrate(
                serverUrl: url,
                purgeLocalAfterSuccess: purgeAfterMigration
            )
            self.lastImportSummary = summary
            self.successMessage = "Successfully migrated \(summary.totalImported) records to cloud backend!"
            self.isMigrating = false
            self.showMigrationSheet = false
        } catch {
            self.errorMessage = "Migration failed: \(error.localizedDescription)"
            self.isMigrating = false
        }
    }

    public func purgeDatabase() async -> Bool {
        guard purgeConfirmationText.trimmingCharacters(in: .whitespaces).uppercased() == "DELETE" else {
            self.errorMessage = "Please type 'DELETE' to confirm database purge."
            return false
        }

        self.isLoading = true
        self.errorMessage = nil
        do {
            try await portabilityRepository.purgeLocalDatabase()
            self.successMessage = "Local database completely cleared."
            self.showPurgeConfirmation = false
            self.purgeConfirmationText = ""
            self.isLoading = false
            return true
        } catch {
            self.errorMessage = "Failed to purge database: \(error.localizedDescription)"
            self.isLoading = false
            return false
        }
    }
}
