import Testing
import Foundation
@testable import FOSSTraining

@Suite("Settings ViewModel Tests")
@MainActor
struct SettingsViewModelTests {

    @Test("SettingsViewModel connection mode and tier synchronization")
    func testConnectionModeSync() {
        let appEnv = AppEnvironment(inMemory: true)
        let defaults = UserDefaults(suiteName: "SettingsVMTests_Mode")!
        defaults.removePersistentDomain(forName: "SettingsVMTests_Mode")
        let settingsRepo = UserDefaultsSettingsRepository(userDefaults: defaults)

        let vm = SettingsViewModel(appEnvironment: appEnv, settingsRepository: settingsRepo)
        #expect(vm.connectionMode == .localOffline)

        vm.setConnectionMode(.remoteCloud)
        #expect(vm.connectionMode == .remoteCloud)
        #expect(settingsRepo.getConnectionMode() == .remoteCloud)
        #expect(appEnv.tierMode == .premium)

        vm.setConnectionMode(.localOffline)
        #expect(vm.connectionMode == .localOffline)
        #expect(settingsRepo.getConnectionMode() == .localOffline)
        #expect(appEnv.tierMode == .local)
    }

    @Test("SettingsViewModel URL and API key mutators")
    func testUrlAndApiKeyMutators() {
        let appEnv = AppEnvironment(inMemory: true)
        let defaults = UserDefaults(suiteName: "SettingsVMTests_UrlKey")!
        defaults.removePersistentDomain(forName: "SettingsVMTests_UrlKey")
        let settingsRepo = UserDefaultsSettingsRepository(userDefaults: defaults)

        let vm = SettingsViewModel(appEnvironment: appEnv, settingsRepository: settingsRepo)

        vm.saveRemoteServerUrl("http://192.168.1.100:8080")
        #expect(vm.remoteServerUrlString == "http://192.168.1.100:8080")
        #expect(settingsRepo.getRemoteServerUrl()?.absoluteString == "http://192.168.1.100:8080")
        #expect(appEnv.backendURL == "http://192.168.1.100:8080")

        vm.saveRemoteServerUrl("")
        #expect(vm.remoteServerUrlString == "")

        vm.saveApiKey("gym-super-token")
        #expect(vm.apiKey == "gym-super-token")
        #expect(settingsRepo.getApiKey() == "gym-super-token")

        vm.saveApiKey("")
        #expect(vm.apiKey == "")
        #expect(settingsRepo.getApiKey() == nil)
    }

    @Test("SettingsViewModel pingServer success and failure branches")
    func testPingServer() async {
        let appEnv = AppEnvironment(inMemory: true)
        let testUrl = URL(string: "http://example.com:8080")!

        // 1. Success
        let successRepo = UserDefaultsSettingsRepository(pinger: { _ in true })
        let vmSuccess = SettingsViewModel(appEnvironment: appEnv, settingsRepository: successRepo)
        let pingOk = await vmSuccess.pingServer(url: testUrl)
        #expect(pingOk == true)
        #expect(vmSuccess.pingLatencyMs != nil)
        #expect(vmSuccess.connectionTestStatus?.contains("Connected!") == true)
        #expect(vmSuccess.errorMessage == nil)

        // 2. Failure
        let failRepo = UserDefaultsSettingsRepository(pinger: { _ in false })
        let vmFail = SettingsViewModel(appEnvironment: appEnv, settingsRepository: failRepo)
        let pingFail = await vmFail.pingServer(url: testUrl)
        #expect(pingFail == false)
        #expect(vmFail.pingLatencyMs == nil)
        #expect(vmFail.connectionTestStatus == nil)
        #expect(vmFail.errorMessage?.contains("Unable to reach server") == true)
    }
}
