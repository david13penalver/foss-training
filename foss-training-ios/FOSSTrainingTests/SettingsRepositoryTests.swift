import Testing
import Foundation
@testable import FOSSTraining

final class SettingsPingMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host == "settings-ping-mock.local"
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        request
    }

    override func startLoading() {
        guard let handler = SettingsPingMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "SettingsPingMockURLProtocol", code: -1))
            return
        }

        do {
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            if let data = data {
                client?.urlProtocol(self, didLoad: data)
            }
            client?.urlProtocolDidFinishLoading(self)
        } catch {
            client?.urlProtocol(self, didFailWithError: error)
        }
    }

    override func stopLoading() {}
}

@Suite("Settings Repository & Persistence Tests")
struct SettingsRepositoryTests {

    @Test("Connection mode getter and setter with UserDefaults")
    func testConnectionMode() {
        let defaults = UserDefaults(suiteName: "SettingsRepositoryTests_Mode")!
        defaults.removePersistentDomain(forName: "SettingsRepositoryTests_Mode")
        let repo = UserDefaultsSettingsRepository(userDefaults: defaults)

        // Default when missing
        #expect(repo.getConnectionMode() == .localOffline)

        // Set remote cloud
        repo.setConnectionMode(.remoteCloud)
        #expect(repo.getConnectionMode() == .remoteCloud)

        // Set local offline
        repo.setConnectionMode(.localOffline)
        #expect(repo.getConnectionMode() == .localOffline)

        // Fallback when invalid raw value
        defaults.set("INVALID_MODE", forKey: "app_connection_mode")
        #expect(repo.getConnectionMode() == .localOffline)
    }

    @Test("Remote server URL getter and setter")
    func testRemoteServerUrl() {
        let defaults = UserDefaults(suiteName: "SettingsRepositoryTests_URL")!
        defaults.removePersistentDomain(forName: "SettingsRepositoryTests_URL")
        let repo = UserDefaultsSettingsRepository(userDefaults: defaults)

        #expect(repo.getRemoteServerUrl() == nil)

        let testUrl = URL(string: "http://example.com:8080")!
        repo.setRemoteServerUrl(testUrl)
        #expect(repo.getRemoteServerUrl() == testUrl)

        repo.setRemoteServerUrl(nil)
        #expect(repo.getRemoteServerUrl() == nil)

        defaults.set("not a valid url with spaces and illegal %%% chars", forKey: "app_remote_server_url")
        defaults.removeObject(forKey: "app_remote_server_url")
    }

    @Test("API key getter and setter")
    func testApiKey() {
        let defaults = UserDefaults(suiteName: "SettingsRepositoryTests_Key")!
        defaults.removePersistentDomain(forName: "SettingsRepositoryTests_Key")
        let repo = UserDefaultsSettingsRepository(userDefaults: defaults)

        #expect(repo.getApiKey() == nil)

        repo.setApiKey("test-secret-key-123")
        #expect(repo.getApiKey() == "test-secret-key-123")

        repo.setApiKey(nil)
        #expect(repo.getApiKey() == nil)
    }

    @Test("pingRemoteServer delegates to pinger")
    func testPingRemoteServerDelegation() async {
        let repoSuccess = UserDefaultsSettingsRepository(pinger: { _ in true })
        let testUrl = URL(string: "http://example.com")!
        let success = await repoSuccess.pingRemoteServer(url: testUrl)
        #expect(success == true)

        let repoFailure = UserDefaultsSettingsRepository(pinger: { _ in false })
        let failure = await repoFailure.pingRemoteServer(url: testUrl)
        #expect(failure == false)
    }

    @Test("Default pinger closure execution with SettingsPingMockURLProtocol")
    func testDefaultPingerClosure() async {
        URLProtocol.registerClass(SettingsPingMockURLProtocol.self)
        defer { URLProtocol.unregisterClass(SettingsPingMockURLProtocol.self) }

        let defaultRepo = UserDefaultsSettingsRepository()
        let mockUrl = URL(string: "http://settings-ping-mock.local/health")!

        // 1. Success 200
        SettingsPingMockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, Data())
        }
        let ping200 = await defaultRepo.pinger(mockUrl)
        #expect(ping200 == true)

        // 2. Failure 500
        SettingsPingMockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 500, httpVersion: nil, headerFields: nil)!
            return (response, Data())
        }
        let ping500 = await defaultRepo.pinger(mockUrl)
        #expect(ping500 == false)

        // 3. Error thrown
        SettingsPingMockURLProtocol.requestHandler = { _ in
            throw URLError(.cannotConnectToHost)
        }
        let pingError = await defaultRepo.pinger(mockUrl)
        #expect(pingError == false)
    }

    @Test("isSuccessResponse helper handles HTTP status ranges and non-HTTP responses")
    func testIsSuccessResponseHelper() {
        let url = URL(string: "http://example.com")!

        let res200 = HTTPURLResponse(url: url, statusCode: 200, httpVersion: nil, headerFields: nil)!
        #expect(UserDefaultsSettingsRepository.isSuccessResponse(res200) == true)

        let res500 = HTTPURLResponse(url: url, statusCode: 500, httpVersion: nil, headerFields: nil)!
        #expect(UserDefaultsSettingsRepository.isSuccessResponse(res500) == false)

        let nonHttp = URLResponse(url: url, mimeType: nil, expectedContentLength: 0, textEncodingName: nil)
        #expect(UserDefaultsSettingsRepository.isSuccessResponse(nonHttp) == false)
    }
}
