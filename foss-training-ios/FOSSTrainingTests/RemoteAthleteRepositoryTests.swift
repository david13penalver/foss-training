import Foundation
import Testing
@testable import FOSSTraining

private final class AthleteMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: (@Sendable (URLRequest) throws -> (HTTPURLResponse, Data))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host() == "athlete-mock.local"
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        request
    }

    override func startLoading() {
        guard let handler = AthleteMockURLProtocol.requestHandler else {
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

@Suite("Remote Athlete Repository Tests", .serialized)
struct RemoteAthleteRepositoryTests {

    private func makeClient() -> NetworkClient {
        let config = URLSessionConfiguration.ephemeral
        config.protocolClasses = [AthleteMockURLProtocol.self]
        let session = URLSession(configuration: config)
        return NetworkClient(baseURLString: "https://athlete-mock.local", session: session)
    }

    @Test("RemoteAthleteRepository: profile lifecycle and fallback on network error")
    func testProfileLifecycle() async throws {
        let client = makeClient()
        let repo = RemoteAthleteRepository(client: client)

        // 1. Success getProfile
        AthleteMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path() == "/api/athlete/profile")
            let json = """
            {
                "id": 1,
                "displayName": "Alex",
                "gender": "FEMALE",
                "dateOfBirth": "1998-05-12",
                "heightCm": 168.0,
                "experienceLevel": "ADVANCED",
                "targetGoal": "Powerlifting",
                "preferredUnit": "KG"
            }
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let profile = try await repo.getProfile()
        #expect(profile?.displayName == "Alex")
        #expect(profile?.gender == .female)
        #expect(profile?.experienceLevel == .advanced)

        // 2. Success saveProfile
        AthleteMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "PUT")
            let json = """
            {
                "id": 1,
                "displayName": "Alex Updated",
                "gender": "FEMALE",
                "dateOfBirth": "1998-05-12",
                "heightCm": 170.0,
                "experienceLevel": "INTERMEDIATE",
                "targetGoal": null,
                "preferredUnit": "LBS"
            }
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        var toUpdate = profile!
        toUpdate.displayName = "Alex Updated"
        let saved = try await repo.saveProfile(toUpdate)
        #expect(saved.displayName == "Alex Updated")
        #expect(saved.preferredUnit == .lbs)

        // 3. Fallback when network error occurs
        AthleteMockURLProtocol.requestHandler = { _ in
            throw URLError(.notConnectedToInternet)
        }

        let fallbackGet = try await repo.getProfile()
        #expect(fallbackGet?.displayName == "Alex Updated")

        let fallbackSave = try await repo.saveProfile(AthleteProfile(id: 2, displayName: "Offline Lifter"))
        #expect(fallbackSave.displayName == "Offline Lifter")
    }

    @Test("RemoteAthleteRepository: bodyweight history, logging, and deletion")
    func testBodyweightOperations() async throws {
        let client = makeClient()
        let repo = RemoteAthleteRepository(client: client)

        // 1. History
        AthleteMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path() == "/api/athlete/bodyweight/history")
            let json = """
            [
                {
                    "id": 10,
                    "entryDate": "2026-09-20",
                    "weightKg": 80.5,
                    "notes": "Morning weigh-in",
                    "createdAt": "2026-09-20T08:00:00"
                }
            ]
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let history = try await repo.getBodyweightHistory()
        #expect(history.count == 1)
        #expect(history[0].id == 10)
        #expect(history[0].weightKg == 80.5)

        // 2. Log bodyweight
        AthleteMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path() == "/api/athlete/bodyweight")
            let json = """
            {
                "id": 11,
                "entryDate": "2026-09-21",
                "weightKg": 80.2,
                "notes": "Post run",
                "createdAt": "2026-09-21T08:00:00"
            }
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let logged = try await repo.logBodyweight(entry: BodyweightEntry(id: 0, weightKg: 80.2, measuredDate: Date(), notes: "Post run"))
        #expect(logged.id == 11)
        #expect(logged.weightKg == 80.2)

        // 3. Delete bodyweight
        AthleteMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "DELETE")
            #expect(request.url?.path() == "/api/athlete/bodyweight/11")
            let response = HTTPURLResponse(url: request.url!, statusCode: 204, httpVersion: nil, headerFields: nil)!
            return (response, Data())
        }

        try await repo.deleteBodyweight(id: 11)
    }

    @Test("RemoteAthleteRepository: calculateRelativeStrength backend success and offline fallback")
    func testCalculateRelativeStrength() async throws {
        let client = makeClient()
        let repo = RemoteAthleteRepository(client: client)

        // 1. Success from backend
        AthleteMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path() == "/api/athlete/relative-strength")
            let json = """
            {
                "totalWeightKg": 500.0,
                "bodyweightKg": 80.0,
                "gender": "MALE",
                "ratio": 6.25,
                "dots": 356.18,
                "wilks": 342.91,
                "classification": "Advanced"
            }
            """.data(using: .utf8)!
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, json)
        }

        let score = try await repo.calculateRelativeStrength(
            totalKg: 500.0,
            bodyweightKg: 80.0,
            gender: .male,
            formula: .dots
        )
        #expect(score.formula == .dots)
        #expect(score.score == 356.18)
        #expect(score.dotsScore == 356.18)
        #expect(score.wilksScore == 342.91)
        #expect(score.tier == .advanced)

        // 2. Offline fallback
        AthleteMockURLProtocol.requestHandler = { _ in
            throw URLError(.notConnectedToInternet)
        }

        let fallbackScore = try await repo.calculateRelativeStrength(
            totalKg: 500.0,
            bodyweightKg: 80.0,
            gender: .male,
            formula: .wilks
        )
        #expect(fallbackScore.formula == .wilks)
        #expect(fallbackScore.wilksScore > 340.0)
    }

    @Test("Remote DTO initializers and mapping fallbacks")
    func testRemoteDtoMappings() {
        let req = RemoteBodyweightRequest(entryDate: "2026-09-22", weightKg: 85.0, bodyFatPercentage: 15.0, notes: "Note")
        #expect(req.bodyFatPercentage == 15.0)

        let resp = RemoteBodyweightResponse(id: 1, entryDate: "invalid-date", weightKg: 85.0, bodyFatPercentage: 15.0, notes: "Note", createdAt: nil)
        let domain = resp.toDomain()
        #expect(domain.weightKg == 85.0)

        let strengthResp = RemoteRelativeStrengthResponse(
            totalWeightKg: 400.0,
            bodyweightKg: 80.0,
            gender: nil,
            ratio: nil,
            dots: nil,
            wilks: nil,
            classification: nil
        )
        let strengthDomain = strengthResp.toDomain(formula: .wilks)
        #expect(strengthDomain.gender == .male)
        #expect(strengthDomain.score > 0.0)

        let femaleResp = RemoteRelativeStrengthResponse(
            totalWeightKg: 300.0,
            bodyweightKg: 60.0,
            gender: "FEMALE",
            ratio: nil,
            dots: nil,
            wilks: nil,
            classification: nil
        )
        let femaleDomain = femaleResp.toDomain(formula: .dots)
        #expect(femaleDomain.gender == .female)

        let profileDto = RemoteAthleteProfileDTO(
            id: 5,
            displayName: "Test",
            gender: "INVALID",
            dateOfBirth: nil,
            heightCm: nil,
            experienceLevel: "INVALID",
            targetGoal: nil,
            preferredUnit: "INVALID"
        )
        let domainProf = profileDto.toDomain()
        #expect(domainProf.gender == .male)
        #expect(domainProf.experienceLevel == .intermediate)
        #expect(domainProf.preferredUnit == .kg)

        let domainValid = AthleteProfile(id: 1, displayName: "A", gender: .female, dateOfBirth: Date(), heightCm: 165.0, experienceLevel: .advanced, targetGoal: "Goal", preferredUnit: .lbs)
        let fromDomain = RemoteAthleteProfileDTO.fromDomain(domainValid)
        #expect(fromDomain.gender == "FEMALE")
        #expect(fromDomain.preferredUnit == "LBS")
    }
}
