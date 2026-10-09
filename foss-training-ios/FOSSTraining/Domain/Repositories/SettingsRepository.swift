import Foundation

public protocol SettingsRepository: Sendable {
    func getConnectionMode() -> AppConnectionMode
    func setConnectionMode(_ mode: AppConnectionMode)
    func getRemoteServerUrl() -> URL?
    func setRemoteServerUrl(_ url: URL?)
    func getApiKey() -> String?
    func setApiKey(_ key: String?)
    func pingRemoteServer(url: URL) async -> Bool
}
