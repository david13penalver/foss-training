import SwiftUI

@Observable
@MainActor
public final class SettingsViewModel {
    private let appEnvironment: AppEnvironment
    public let settingsRepository: SettingsRepository

    public var isMigrating: Bool = false
    public var migrationSuccessMessage: String?
    public var errorMessage: String?
    public var connectionTestStatus: String?
    public var pingLatencyMs: Double?

    public var connectionMode: AppConnectionMode {
        didSet {
            settingsRepository.setConnectionMode(connectionMode)
            appEnvironment.tierMode = (connectionMode == .remoteCloud ? .premium : .local)
        }
    }

    public var remoteServerUrlString: String {
        didSet {
            if let url = URL(string: remoteServerUrlString), !remoteServerUrlString.isEmpty {
                settingsRepository.setRemoteServerUrl(url)
                appEnvironment.backendURL = remoteServerUrlString
            }
        }
    }

    public var apiKey: String {
        didSet {
            settingsRepository.setApiKey(apiKey.isEmpty ? nil : apiKey)
        }
    }

    public init(appEnvironment: AppEnvironment, settingsRepository: SettingsRepository? = nil) {
        self.appEnvironment = appEnvironment
        let repo = settingsRepository ?? appEnvironment.settingsRepository
        self.settingsRepository = repo
        self.connectionMode = repo.getConnectionMode()
        self.remoteServerUrlString = repo.getRemoteServerUrl()?.absoluteString ?? appEnvironment.backendURL
        self.apiKey = repo.getApiKey() ?? ""
    }

    public func setConnectionMode(_ mode: AppConnectionMode) {
        self.connectionMode = mode
    }

    public func saveRemoteServerUrl(_ urlString: String) {
        self.remoteServerUrlString = urlString
    }

    public func saveApiKey(_ key: String) {
        self.apiKey = key
    }

    @discardableResult
    public func pingServer(url: URL) async -> Bool {
        let start = Date()
        let success = await settingsRepository.pingRemoteServer(url: url)
        let elapsed = max(1.0, Date().timeIntervalSince(start) * 1000.0)
        if success {
            self.pingLatencyMs = elapsed
            self.connectionTestStatus = String(format: "Connected! (%.0f ms)", elapsed)
            self.errorMessage = nil
        } else {
            self.pingLatencyMs = nil
            self.connectionTestStatus = nil
            self.errorMessage = "Unable to reach server"
        }
        return success
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
