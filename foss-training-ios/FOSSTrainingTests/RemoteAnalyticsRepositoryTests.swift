import Testing
import Foundation
@testable import FOSSTraining

final class AnalyticsMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host == "analytics-mock.local"
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = AnalyticsMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "AnalyticsMockURLProtocol", code: -1))
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

@Suite("Remote Analytics Repository Tests", .serialized)
@MainActor
struct RemoteAnalyticsRepositoryTests {

    private func encodeISO8601<T: Encodable>(_ value: T) -> Data {
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        return try! encoder.encode(value)
    }

    private func createMockClient() -> NetworkClient {
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [AnalyticsMockURLProtocol.self]
        let session = URLSession(configuration: configuration)
        return NetworkClient(baseURLString: "http://analytics-mock.local", session: session)
    }

    @Test("calculateOneRepMax calls 1rm endpoint for all formulas")
    func testCalculateOneRepMax() async throws {
        let client = createMockClient()
        let repo = RemoteAnalyticsRepository(client: client)

        AnalyticsMockURLProtocol.requestHandler = { request in
            let url = request.url?.absoluteString ?? ""
            #expect(url.contains("/api/analytics/1rm?weight=100.0&reps=5&formula="))

            let dto = RemoteOneRepMaxResponseDto(
                weight: 100.0,
                unit: "KG",
                repetitions: 5,
                formula: .epley,
                estimated1Rm: 116.67,
                percentages: ["95": 110.84, "90": 105.0, "invalid": 0.0]
            )
            let data = self.encodeISO8601(dto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let estimates = try await repo.calculateOneRepMax(weightKg: 100.0, reps: 5)
        #expect(estimates.count == 6)
        #expect(estimates[0].estimatedOneRepMax == 116.67)
        #expect(estimates[0].percentages.contains { $0.percentage == 95 })

        // Test DTO with nil percentages
        let nilPctDto = RemoteOneRepMaxResponseDto(weight: 100, repetitions: 5, formula: .epley, estimated1Rm: 116.67, percentages: nil)
        #expect(nilPctDto.toDomain().percentages.isEmpty)
    }

    @Test("getPersonalRecords: all, specific exercise, 404 fallback, and fallback DTO branches")
    func testGetPersonalRecords() async throws {
        let client = createMockClient()
        let repo = RemoteAnalyticsRepository(client: client)

        // 1. All personal records
        AnalyticsMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path == "/api/analytics/personal-records")
            let dto = RemotePersonalRecordResponseDto(
                exerciseId: 1,
                exerciseName: "Bench Press",
                maxWeight: RemoteMaxWeightRecordDto(value: 120.0, unit: "kg", repetitions: 3, trainingId: 10, trainingDate: Date()),
                bestEstimated1Rm: RemoteBestEstimated1RmRecordDto(estimated1Rm: 130.0, unit: "kg", sourceWeight: 120, sourceReps: 3, formula: .epley, trainingId: 10, trainingDate: Date()),
                maxSessionVolume: RemoteMaxSessionVolumeRecordDto(volume: 2400.0, unit: "kg", trainingId: 10, trainingDate: Date()),
                maxReps: RemoteMaxRepsRecordDto(repetitions: 12, weight: 80, unit: "reps", trainingId: 10, trainingDate: Date())
            )
            let data = self.encodeISO8601([dto])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let allPrs = try await repo.getPersonalRecords(exerciseId: nil)
        #expect(allPrs.count == 4)

        // 2. Filtered exercise ID
        AnalyticsMockURLProtocol.requestHandler = { request in
            #expect(request.url?.path == "/api/analytics/personal-records/exercise/1")
            let dto = RemotePersonalRecordResponseDto(
                exerciseId: 1,
                exerciseName: "Bench Press",
                maxWeight: RemoteMaxWeightRecordDto(value: 120.0, unit: "kg", repetitions: 3, trainingId: 10, trainingDate: Date())
            )
            let data = self.encodeISO8601(dto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let singlePr = try await repo.getPersonalRecords(exerciseId: 1)
        #expect(singlePr.count == 1)

        // 3. 404 returns empty array
        AnalyticsMockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 404, httpVersion: nil, headerFields: nil)!
            return (response, "Not found".data(using: .utf8)!)
        }
        let missing = try await repo.getPersonalRecords(exerciseId: 999)
        #expect(missing.isEmpty)

        // 4. Test DTO nil optional fallbacks in toDomain()
        let fallbackDto = RemotePersonalRecordResponseDto(
            exerciseId: 2,
            exerciseName: "Squat",
            maxWeight: RemoteMaxWeightRecordDto(value: 140.0, unit: nil, repetitions: nil, trainingId: nil, trainingDate: nil),
            bestEstimated1Rm: RemoteBestEstimated1RmRecordDto(estimated1Rm: 155.0, unit: nil, sourceWeight: nil, sourceReps: nil, formula: nil, trainingId: nil, trainingDate: nil),
            maxSessionVolume: RemoteMaxSessionVolumeRecordDto(volume: 3500.0, unit: nil, trainingId: nil, trainingDate: nil),
            maxReps: RemoteMaxRepsRecordDto(repetitions: 15, weight: nil, unit: nil, trainingId: nil, trainingDate: nil)
        )
        let fallbackPrs = fallbackDto.toDomain()
        #expect(fallbackPrs.count == 4)
        #expect(fallbackPrs[0].unit == "kg")
        #expect(fallbackPrs[3].unit == "reps")
    }

    @Test("calculateAcwr with and without asOfDate")
    func testCalculateAcwr() async throws {
        let client = createMockClient()
        let repo = RemoteAnalyticsRepository(client: client)

        AnalyticsMockURLProtocol.requestHandler = { request in
            let now = Date()
            let workload = WorkloadRatio(
                targetDate: now,
                acuteWorkload: 1200.0,
                acuteDailyAverage: 171.43,
                chronicWorkload: 4000.0,
                chronicWeeklyAverage: 1000.0,
                chronicDailyAverage: 142.86,
                acwr: 1.20,
                riskZone: .optimal,
                deloadRecommended: false,
                recommendation: "Optimal training load",
                dailyWorkloads: [
                    DailyWorkload(date: now, workloadAu: 200.0, totalVolumeKg: 1500.0, completedSessions: 1)
                ]
            )
            let data = self.encodeISO8601(workload)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let withDate = try await repo.calculateAcwr(asOfDate: Date())
        #expect(withDate.acwr == 1.20)

        let withoutDate = try await repo.calculateAcwr(asOfDate: nil)
        #expect(withoutDate.acwr == 1.20)
    }

    @Test("getWeeklyMuscleVolume with and without weekStartDate")
    func testGetWeeklyMuscleVolume() async throws {
        let client = createMockClient()
        let repo = RemoteAnalyticsRepository(client: client)

        AnalyticsMockURLProtocol.requestHandler = { request in
            let now = Date()
            let vol = WeeklyMuscleVolume(
                startDate: now,
                endDate: now.addingTimeInterval(6 * 86400),
                totalWorkingSets: 24,
                totalVolumeKg: 12000.0,
                muscleVolumes: [
                    MuscleGroupVolume(muscleGroup: "Chest", muscleGroupName: "Chest", directSets: 12, indirectSets: 4, effectiveSets: 14.0, totalVolumeKg: 6000.0, status: .adaptive)
                ],
                pushPullRatio: 1.0,
                upperLowerRatio: 1.2,
                neglectedMuscleGroups: [],
                optimalMuscleGroups: ["Chest"],
                overtrainedMuscleGroups: [],
                recommendations: ["Maintain volume"]
            )
            let data = self.encodeISO8601(vol)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let withDate = try await repo.getWeeklyMuscleVolume(weekStartDate: Date())
        #expect(withDate.totalWorkingSets == 24)

        let withoutDate = try await repo.getWeeklyMuscleVolume(weekStartDate: nil)
        #expect(withoutDate.totalWorkingSets == 24)
    }

    @Test("getExerciseProgression with months > 0 and months == 0")
    func testGetExerciseProgression() async throws {
        let client = createMockClient()
        let repo = RemoteAnalyticsRepository(client: client)

        AnalyticsMockURLProtocol.requestHandler = { request in
            let now = Date()
            let prog = ExerciseProgression(
                exerciseId: 1,
                exerciseName: "Bench Press",
                formula: .epley,
                startDate: now.addingTimeInterval(-90 * 86400),
                endDate: now,
                totalSessions: 12,
                initial1RmKg: 100.0,
                latest1RmKg: 120.0,
                absolute1RmGainKg: 20.0,
                relative1RmGainPercentage: 20.0,
                allTimeBest1RmKg: 120.0,
                allTimeBestTopWeightKg: 110.0,
                allTimeMaxVolumeKg: 3000.0,
                trend: .improving,
                dataPoints: [
                    ProgressionDataPoint(trainingId: 1, date: now, totalSets: 4, workingSets: 4, totalReps: 20, totalVolumeKg: 2000, topWeightKg: 100, topWeightReps: 5, topWeightRpe: 8, estimatedOneRepMax: 116.67, averageIntensityKg: 100)
                ]
            )
            let data = self.encodeISO8601(prog)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let prog3m = try await repo.getExerciseProgression(exerciseId: 1, months: 3)
        #expect(prog3m.exerciseId == 1)
        #expect(prog3m.trend == .improving)

        let progAll = try await repo.getExerciseProgression(exerciseId: 1, months: 0)
        #expect(progAll.exerciseId == 1)
    }

    @Test("calculateHeartRateZones: Karvonen and percentMaxHr")
    func testCalculateHeartRateZones() async throws {
        let client = createMockClient()
        let repo = RemoteAnalyticsRepository(client: client)

        AnalyticsMockURLProtocol.requestHandler = { request in
            let url = request.url?.absoluteString ?? ""
            let isKarvonen = url.contains("restingHr=")
            let zones = [
                CalculatedHeartRateZone(zoneNumber: 1, displayName: "Recovery", minPercentage: 0.5, maxPercentage: 0.6, minBpm: 120, maxBpm: 135, description: "Recovery", trainingBenefit: "Aerobic base")
            ]
            let hrZones = HeartRateZones(
                maxHr: 190,
                restingHr: isKarvonen ? 60 : nil,
                age: nil,
                method: isKarvonen ? .karvonen : .percentMaxHr,
                heartRateReserve: isKarvonen ? 130 : nil,
                zones: zones
            )
            let data = self.encodeISO8601(hrZones)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: ["Content-Type": "application/json"])!
            return (response, data)
        }

        let karvonen = try await repo.calculateHeartRateZones(restingHr: 60, maxHr: 190, method: .karvonen)
        #expect(karvonen.method == .karvonen)

        let pctMax = try await repo.calculateHeartRateZones(restingHr: 60, maxHr: 190, method: .percentMaxHr)
        #expect(pctMax.method == .percentMaxHr)
    }
}
