import Foundation

public final class UserDefaultsSettingsRepository: SettingsRepository, @unchecked Sendable {
    private let userDefaults: UserDefaults
    public let pinger: @Sendable (URL) async -> Bool

    private enum Keys {
        static let connectionMode = "app_connection_mode"
        static let remoteServerUrl = "app_remote_server_url"
        static let apiKey = "app_api_key"
    }

    public init(
        userDefaults: UserDefaults = .standard,
        pinger: (@Sendable (URL) async -> Bool)? = nil
    ) {
        self.userDefaults = userDefaults
        if let pinger = pinger {
            self.pinger = pinger
        } else {
            self.pinger = { url in
                var request = URLRequest(url: url)
                request.httpMethod = "HEAD"
                request.timeoutInterval = 3.0
                do {
                    let (_, response) = try await URLSession.shared.data(for: request)
                    return Self.isSuccessResponse(response)
                } catch {
                    return false
                }
            }
        }
    }

    static func isSuccessResponse(_ response: URLResponse) -> Bool {
        guard let httpResponse = response as? HTTPURLResponse else {
            return false
        }
        return (200...399).contains(httpResponse.statusCode)
    }

    public func getConnectionMode() -> AppConnectionMode {
        guard let raw = userDefaults.string(forKey: Keys.connectionMode),
              let mode = AppConnectionMode(rawValue: raw) else {
            return .localOffline
        }
        return mode
    }

    public func setConnectionMode(_ mode: AppConnectionMode) {
        userDefaults.set(mode.rawValue, forKey: Keys.connectionMode)
    }

    public func getRemoteServerUrl() -> URL? {
        guard let str = userDefaults.string(forKey: Keys.remoteServerUrl),
              let url = URL(string: str) else {
            return nil
        }
        return url
    }

    public func setRemoteServerUrl(_ url: URL?) {
        if let url = url {
            userDefaults.set(url.absoluteString, forKey: Keys.remoteServerUrl)
        } else {
            userDefaults.removeObject(forKey: Keys.remoteServerUrl)
        }
    }

    public func getApiKey() -> String? {
        userDefaults.string(forKey: Keys.apiKey)
    }

    public func setApiKey(_ key: String?) {
        if let key = key {
            userDefaults.set(key, forKey: Keys.apiKey)
        } else {
            userDefaults.removeObject(forKey: Keys.apiKey)
        }
    }

    public func pingRemoteServer(url: URL) async -> Bool {
        await pinger(url)
    }
}
