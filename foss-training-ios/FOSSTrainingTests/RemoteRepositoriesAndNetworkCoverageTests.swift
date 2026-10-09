import Testing
import Foundation
@testable import FOSSTraining

final class MockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host == "mock.local" || request.url?.path.contains("workouts.csv") == true
    }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = MockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "MockURLProtocol", code: -1))
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

@Suite("Remote Repositories & NetworkClient Coverage Tests", .serialized)
@MainActor
struct RemoteRepositoriesAndNetworkCoverageTests {

    private func encodeISO8601<T: Encodable>(_ value: T) -> Data {
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        return try! encoder.encode(value)
    }

    private func createMockClient() -> NetworkClient {
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [MockURLProtocol.self]
        let session = URLSession(configuration: configuration)
        return NetworkClient(baseURLString: "http://mock.local", session: session)
    }

    @Test("NetworkClient: GET, POST, PUT, DELETE, postEmpty, postNoResponse and error branches")
    func testNetworkClientMethods() async throws {
        let client = createMockClient()

        // 1. Successful GET
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            let data = try! JSONEncoder().encode(["result": "ok"])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let getResult: [String: String] = try await client.get(endpoint: "/test")
        #expect(getResult["result"] == "ok")

        // 2. Successful POST with body
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            let data = try! JSONEncoder().encode(["created": 1])
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let postResult: [String: Int] = try await client.post(endpoint: "/create", body: ["name": "test"])
        #expect(postResult["created"] == 1)

        // 3. Successful PUT
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "PUT")
            let data = try! JSONEncoder().encode(["updated": true])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let putResult: [String: Bool] = try await client.put(endpoint: "/update", body: ["name": "new"])
        #expect(putResult["updated"] == true)

        // 4. Successful POST Empty
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            let data = try! JSONEncoder().encode(["status": "started"])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let emptyResult: [String: String] = try await client.postEmpty(endpoint: "/empty")
        #expect(emptyResult["status"] == "started")

        // 5. Successful postNoResponse
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 204, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        try await client.postNoResponse(endpoint: "/no-response", body: ["k": "v"])

        // 6. Successful DELETE
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "DELETE")
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        try await client.delete(endpoint: "/delete/1")

        // 7. Server Error (500)
        MockURLProtocol.requestHandler = { request in
            let data = "Internal Server Error".data(using: .utf8)
            let response = HTTPURLResponse(url: request.url!, statusCode: 500, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        await #expect(throws: APIError.self) {
            let _: [String: String] = try await client.get(endpoint: "/error")
        }

        // 8. postNoResponse Server Error (500)
        await #expect(throws: APIError.self) {
            try await client.postNoResponse(endpoint: "/error", body: ["k": "v"])
        }

        // 9. delete Server Error (500)
        await #expect(throws: APIError.self) {
            try await client.delete(endpoint: "/error")
        }

        // 10. Decoding Error
        MockURLProtocol.requestHandler = { request in
            let data = "Not a json".data(using: .utf8)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        await #expect(throws: APIError.self) {
            let _: [String: String] = try await client.get(endpoint: "/bad-json")
        }

        // 11. Non-utf8 Server Error fallback message
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 400, httpVersion: nil, headerFields: nil)!
            return (response, Data([0xFF, 0xFE, 0xFD]))
        }
        await #expect(throws: APIError.self) {
            let _: [String: String] = try await client.get(endpoint: "/non-utf8")
        }

        // 12. Network connection error
        MockURLProtocol.requestHandler = { _ in
            throw URLError(.timedOut)
        }
        await #expect(throws: APIError.self) {
            let _: [String: String] = try await client.get(endpoint: "/timeout")
        }

        // 13. Non-HTTP response errors
        MockURLProtocol.requestHandler = { request in
            let response = URLResponse(url: request.url!, mimeType: nil, expectedContentLength: 0, textEncodingName: nil)
            return (response, nil)
        }
        await #expect(throws: APIError.self) {
            let _: [String: String] = try await client.get(endpoint: "/not-http")
        }
        await #expect(throws: APIError.self) {
            try await client.postNoResponse(endpoint: "/not-http", body: ["k": "v"])
        }
        await #expect(throws: APIError.self) {
            try await client.delete(endpoint: "/not-http")
        }

        // 14. Invalid URL paths
        let badClient = NetworkClient(baseURLString: "http://invalid url with spaces ")
        await #expect(throws: APIError.self) { let _: [String: String] = try await badClient.get(endpoint: "") }
        await #expect(throws: APIError.self) { let _: [String: String] = try await badClient.post(endpoint: "", body: ["a": "b"]) }
        await #expect(throws: APIError.self) { let _: [String: String] = try await badClient.put(endpoint: "", body: ["a": "b"]) }
        await #expect(throws: APIError.self) { try await badClient.postNoResponse(endpoint: "", body: ["a": "b"]) }
        await #expect(throws: APIError.self) { let _: [String: String] = try await badClient.postEmpty(endpoint: "") }
        await #expect(throws: APIError.self) { try await badClient.delete(endpoint: "") }

        // 13. setBaseURL
        await client.setBaseURL("http://updated.local:9090")
    }

    @Test("APIError error descriptions")
    func testAPIErrors() {
        #expect(APIError.invalidURL.errorDescription?.contains("Invalid") == true)
        #expect(APIError.networkError("timeout").errorDescription?.contains("timeout") == true)
        #expect(APIError.serverError(statusCode: 404, message: "Not Found").errorDescription?.contains("404") == true)
        #expect(APIError.decodingError("mismatch").errorDescription?.contains("mismatch") == true)
    }

    @Test("RemoteExerciseRepository: getExercises with filters, getExercise 200 and 404, save (create and update), delete")
    func testRemoteExerciseRepository() async throws {
        let client = createMockClient()
        let repo = RemoteExerciseRepository(client: client)

        let exercise = Exercise(id: 1, name: "Squat", primaryCategory: .resistance)

        // getExercises with category and search
        MockURLProtocol.requestHandler = { request in
            #expect(request.url?.query?.contains("category=RESISTANCE") == true)
            #expect(request.url?.query?.contains("search=Squat") == true)
            let data = try! JSONEncoder().encode([exercise])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let list = try await repo.getExercises(category: .resistance, search: "Squat")
        #expect(list.count == 1)

        // getExercise 200
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(exercise)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let found = try await repo.getExercise(id: 1)
        #expect(found?.name == "Squat")

        // getExercise 404 (returns nil)
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 404, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        let notFound = try await repo.getExercise(id: 999)
        #expect(notFound == nil)

        // saveExercise update (id > 0 uses PUT)
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "PUT")
            let data = try! JSONEncoder().encode(exercise)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let updated = try await repo.saveExercise(exercise)
        #expect(updated.id == 1)

        // saveExercise create (id == 0 uses POST)
        let newEx = Exercise(id: 0, name: "New", primaryCategory: .resistance)
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            let data = try! JSONEncoder().encode(newEx)
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        _ = try await repo.saveExercise(newEx)

        // deleteExercise
        MockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "DELETE")
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        try await repo.deleteExercise(id: 1)

        // Search with non-percent-encodable fallback
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode([exercise])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let badChar = String(utf16CodeUnits: [0xD800], count: 1)
        let fallbackResults = try await repo.getExercises(category: nil, search: badChar)
        #expect(fallbackResults.count == 1)
    }

    @Test("RemoteTrainingRepository: all lifecycle endpoints, 404 handling, sets logging")
    func testRemoteTrainingRepository() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingRepository(client: client)

        let training = Training(id: 10, name: "Push Workout")

        // getTrainings
        MockURLProtocol.requestHandler = { request in
            let data = self.encodeISO8601([training])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let list = try await repo.getTrainings()
        #expect(list.count == 1)

        // getTraining found
        MockURLProtocol.requestHandler = { request in
            let data = self.encodeISO8601(training)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let found = try await repo.getTraining(id: 10)
        #expect(found?.name == "Push Workout")

        // getTraining 404
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 404, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        let notFound = try await repo.getTraining(id: 999)
        #expect(notFound == nil)

        // createTrainingFromSession, start, pause, resume, complete, cancel
        MockURLProtocol.requestHandler = { request in
            let data = self.encodeISO8601(training)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        _ = try await repo.createTrainingFromSession(sessionId: 1)
        _ = try await repo.startTraining(id: 10)
        _ = try await repo.pauseTraining(id: 10)
        _ = try await repo.resumeTraining(id: 10)
        _ = try await repo.completeTraining(id: 10, overallRpe: 8.0, notes: "Good")
        _ = try await repo.cancelTraining(id: 10)

        // logSet and updateSet
        let set = ResistanceSet(setNumber: 1, weightKg: 80, repetitions: 5)
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(set)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        _ = try await repo.logSet(trainingId: 10, exerciseId: 1, set: set)
        _ = try await repo.updateSet(trainingId: 10, exerciseId: 1, set: set)

        // deleteSet
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        try await repo.deleteSet(trainingId: 10, exerciseId: 1, setNumber: 1)
    }

    @Test("RemotePortabilityRepository: exportFullBackup, importFullBackup, and ImportSummaryDTO")
    func testRemotePortabilityRepository() async throws {
        let client = createMockClient()
        let repo = RemotePortabilityRepository(client: client)

        let payload = BackupDataPayload(
            exportVersion: "1.0",
            exportedAt: Date(),
            exercises: [],
            sessions: [],
            trainings: [],
            bodyweightEntries: []
        )

        // exportFullBackup
        MockURLProtocol.requestHandler = { request in
            let data = self.encodeISO8601(payload)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let exported = try await repo.exportFullBackup()
        #expect(exported.exportVersion == "1.0")

        // importFullBackup
        let summaryDTO = ImportSummaryDTO(
            exercisesImported: 5,
            sessionsImported: 2,
            trainingsImported: 3,
            bodyweightsImported: 1
        )
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(summaryDTO)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let count = try await repo.importFullBackup(payload: payload)
        #expect(count == 11)

        // importFullBackup with nil counts (tests ?? 0)
        let emptySummary = ImportSummaryDTO(exercisesImported: nil, sessionsImported: nil, trainingsImported: nil, bodyweightsImported: nil)
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(emptySummary)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let zeroCount = try await repo.importFullBackup(payload: payload)
        #expect(zeroCount == 0)

        // restoreBackup
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(summaryDTO)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let restoredSummary = try await repo.restoreBackup(payload, mode: .overwrite)
        #expect(restoredSummary.exercisesImported == 5)

        // restoreBackup with nil DTO fields
        MockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(emptySummary)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let emptyRestored = try await repo.restoreBackup(payload, mode: .merge)
        #expect(emptyRestored.exercisesImported == 0)

        // importWorkoutsCsv
        let csvImportSummary = try await repo.importWorkoutsCsv("header\n1\n")
        #expect(csvImportSummary.trainingsImported == 0)

        // purgeLocalDatabase
        try await repo.purgeLocalDatabase()

        // migrateToRemoteServer
        MockURLProtocol.requestHandler = { request in
            if request.url?.path() == "/api/data/export/backup" {
                let data = self.encodeISO8601(payload)
                let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
                return (response, data)
            } else {
                let data = try! JSONEncoder().encode(summaryDTO)
                let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
                return (response, data)
            }
        }
        let migratedSummary = try await repo.migrateToRemoteServer(serverUrl: URL(string: "http://mock.local")!)
        #expect(migratedSummary.exercisesImported == 5)

        // migrateToRemoteServer with nil DTO fields
        MockURLProtocol.requestHandler = { request in
            if request.url?.path() == "/api/data/export/backup" {
                let data = self.encodeISO8601(payload)
                let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
                return (response, data)
            } else {
                let data = try! JSONEncoder().encode(emptySummary)
                let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
                return (response, data)
            }
        }
        let emptyMigrated = try await repo.migrateToRemoteServer(serverUrl: URL(string: "http://mock.local")!)
        #expect(emptyMigrated.exercisesImported == 0)

        // exportWorkoutsCSV
        URLProtocol.registerClass(MockURLProtocol.self)
        defer { URLProtocol.unregisterClass(MockURLProtocol.self) }
        MockURLProtocol.requestHandler = { request in
            let data = "col1,col2\nval1,val2".data(using: .utf8)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let csv = try await repo.exportWorkoutsCSV()
        #expect(csv.contains("col1,col2"))

        // exportWorkoutsCSV with non-utf8 data fallback ?? ""
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, Data([0xFF, 0xFE, 0xFD]))
        }
        let emptyCsv = try await repo.exportWorkoutsCSV()
        #expect(emptyCsv == "")
    }

    @Test("AppEnvironment: tier mode switching and backendURL updates")
    func testAppEnvironmentTierModeSwitching() {
        UserDefaults.standard.set(AppTierMode.local.rawValue, forKey: "app_tier_mode")
        let env = AppEnvironment(inMemory: true)
        #expect(env.analyticsRepository is SwiftDataAnalyticsRepository)

        env.tierMode = .premium
        #expect(env.tierMode == .premium)
        #expect(env.analyticsRepository is RemoteAnalyticsRepository)

        env.backendURL = "http://127.0.0.1:8080"
        #expect(env.backendURL == "http://127.0.0.1:8080")

        env.tierMode = .local
        #expect(env.tierMode == .local)
        #expect(env.analyticsRepository is SwiftDataAnalyticsRepository)

        // AppEnvironment init variations
        // 1. Missing keys in UserDefaults
        UserDefaults.standard.removeObject(forKey: "app_tier_mode")
        UserDefaults.standard.removeObject(forKey: "app_backend_url")
        let envMissing = AppEnvironment(inMemory: true)
        #expect(envMissing.tierMode == .local)
        #expect(envMissing.backendURL == "http://localhost:8080")

        // 2. Invalid tier in UserDefaults
        UserDefaults.standard.set("invalid_tier", forKey: "app_tier_mode")
        let envInvalid = AppEnvironment(inMemory: true)
        #expect(envInvalid.tierMode == .local)

        // 3. Premium tier in UserDefaults
        UserDefaults.standard.set(AppTierMode.premium.rawValue, forKey: "app_tier_mode")
        let envPremium = AppEnvironment(inMemory: true)
        #expect(envPremium.tierMode == .premium)
        #expect(envPremium.analyticsRepository is RemoteAnalyticsRepository)

        // Reset to local
        UserDefaults.standard.set(AppTierMode.local.rawValue, forKey: "app_tier_mode")
    }
}
