import SwiftUI

@Observable
@MainActor
public final class SettingsViewModel {
    private let appEnvironment: AppEnvironment

    public var isMigrating: Bool = false
    public var migrationSuccessMessage: String?
    public var errorMessage: String?
    public var connectionTestStatus: String?

    public init(appEnvironment: AppEnvironment) {
        self.appEnvironment = appEnvironment
    }

    public func testConnection() async {
        connectionTestStatus = "Connecting..."
        errorMessage = nil
        do {
            let exercises = try await appEnvironment.exerciseRepository.getExercises(category: nil, search: nil)
            connectionTestStatus = "Connected successfully! (\(exercises.count) exercises found)"
        } catch {
            connectionTestStatus = nil
            errorMessage = "Connection failed: \(error.localizedDescription)"
        }
    }

    public func syncLocalDataToCloud() async {
        isMigrating = true
        errorMessage = nil
        migrationSuccessMessage = nil

        do {
            let count = try await appEnvironment.migrateLocalDataToCloud()
            migrationSuccessMessage = "Successfully migrated \(count) records to cloud backend!"
        } catch {
            errorMessage = "Migration failed: \(error.localizedDescription)"
        }
        isMigrating = false
    }
}
