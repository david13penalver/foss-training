import Testing
import Foundation
@testable import FOSSTraining

final class ProgramMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.host == "program-mock.local"
    }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = ProgramMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "ProgramMockURLProtocol", code: -1))
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

@Suite("Remote Training Program Repository Tests", .serialized)
@MainActor
struct RemoteTrainingProgramRepositoryTests {

    private func encodeISO8601<T: Encodable>(_ value: T) -> Data {
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        return try! encoder.encode(value)
    }

    private func createMockClient() -> NetworkClient {
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [ProgramMockURLProtocol.self]
        let session = URLSession(configuration: configuration)
        return NetworkClient(baseURLString: "http://program-mock.local", session: session)
    }

    @Test("getPrograms and getProgram endpoints")
    func testGetPrograms() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingProgramRepository(client: client)

        let programDto = TrainingProgramResponseDto(
            id: 1,
            name: "Strength Block",
            description: "Block 1",
            durationWeeks: 4,
            periodizationType: "LINEAR",
            level: "INTERMEDIATE",
            workouts: [
                ProgramWorkoutResponseDto(
                    dayOfWeek: 1,
                    focus: "Upper",
                    session: SessionResponseDto(id: 10, name: "Upper A")
                )
            ],
            isActive: true
        )

        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            #expect(request.url?.path == "/api/programs")
            let data = self.encodeISO8601([programDto])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let programs = try await repo.getPrograms()
        #expect(programs.count == 1)
        #expect(programs[0].name == "Strength Block")
        #expect(programs[0].workouts.count == 1)

        // getProgram(id:) 200
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            #expect(request.url?.path == "/api/programs/1")
            let data = self.encodeISO8601(programDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let single = try await repo.getProgram(id: 1)
        #expect(single?.name == "Strength Block")

        // getProgram(id:) 404
        ProgramMockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 404, httpVersion: nil, headerFields: nil)!
            return (response, Data())
        }

        let notFound = try await repo.getProgram(id: 99)
        #expect(notFound == nil)
    }

    @Test("saveProgram sends POST for id == 0 and PUT for id > 0")
    func testSaveProgram() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingProgramRepository(client: client)

        let workout = ProgramWorkout(dayOfWeek: 1, focus: "Upper", session: Session(id: 1, name: "Upper Session"))
        let newProgram = TrainingProgram(id: 0, name: "New Linear", workouts: [workout])
        let createdDto = TrainingProgramResponseDto(id: 15, name: "New Linear", workouts: [])

        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path == "/api/programs")
            let data = self.encodeISO8601(createdDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let saved = try await repo.saveProgram(newProgram)
        #expect(saved.id == 15)

        // Update program id: 15 -> PUT
        var existingProgram = saved
        existingProgram.name = "Updated Linear"
        let updatedDto = TrainingProgramResponseDto(id: 15, name: "Updated Linear", workouts: [])

        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "PUT")
            #expect(request.url?.path == "/api/programs/15")
            let data = self.encodeISO8601(updatedDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let updated = try await repo.saveProgram(existingProgram)
        #expect(updated.name == "Updated Linear")
    }

    @Test("deleteProgram, programExists, cloneProgram")
    func testDeleteExistsClone() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingProgramRepository(client: client)

        // delete
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "DELETE")
            #expect(request.url?.path == "/api/programs/12")
            let response = HTTPURLResponse(url: request.url!, statusCode: 204, httpVersion: nil, headerFields: nil)!
            return (response, Data())
        }
        try await repo.deleteProgram(id: 12)

        // exists
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            #expect(request.url?.path == "/api/programs/12/exists")
            let data = try! JSONEncoder().encode(true)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let exists = try await repo.programExists(id: 12)
        #expect(exists == true)

        // clone with custom name
        let clonedDto = TrainingProgramResponseDto(id: 20, name: "Clone Name", workouts: [])
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path == "/api/programs/12/clone")
            let data = self.encodeISO8601(clonedDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let cloned = try await repo.cloneProgram(id: 12, newName: "Clone Name")
        #expect(cloned.id == 20)
        #expect(cloned.name == "Clone Name")

        // clone with nil name
        let clonedNilDto = TrainingProgramResponseDto(id: 21, name: "Linear (Copy)", workouts: [])
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path == "/api/programs/12/clone")
            let data = self.encodeISO8601(clonedNilDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let clonedNil = try await repo.cloneProgram(id: 12, newName: nil)
        #expect(clonedNil.id == 21)
    }

    @Test("generateSchedule with and without startDate")
    func testGenerateSchedule() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingProgramRepository(client: client)

        let training = Training(id: 101, name: "Program - W1D1: Upper", programId: 5)

        let specificDate = Date(timeIntervalSince1970: 1774915200)
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withFullDate]
        let expectedDateStr = formatter.string(from: specificDate)

        // 1. With startDate
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path == "/api/programs/5/generate-schedule")
            #expect(request.url?.query?.contains("startDate=\(expectedDateStr)") == true)
            let data = self.encodeISO8601([training])
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let scheduled = try await repo.generateSchedule(programId: 5, startDate: specificDate)
        #expect(scheduled.count == 1)
        #expect(scheduled[0].id == 101)

        // 2. Without startDate
        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path == "/api/programs/5/generate-schedule")
            #expect(request.url?.query == nil)
            let data = self.encodeISO8601([training])
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let scheduledNil = try await repo.generateSchedule(programId: 5, startDate: nil)
        #expect(scheduledNil.count == 1)
    }

    @Test("getProgramAdherence")
    func testGetProgramAdherence() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingProgramRepository(client: client)

        let adherenceDto = ProgramAdherenceResponseDto(
            programId: 7,
            programName: "Undulating Hypertrophy",
            durationWeeks: 4,
            totalScheduledWorkouts: 16,
            completedWorkouts: 8,
            inProgressWorkouts: 1,
            plannedWorkouts: 7,
            missedWorkouts: 0,
            cancelledWorkouts: 0,
            overallCompletionRate: 50.0,
            currentAdherenceRate: 100.0,
            currentStreak: 4,
            longestStreak: 4,
            status: "ON_TRACK",
            statusDescription: "On Track",
            weeklyBreakdowns: [
                WeeklyAdherenceDto(
                    weekNumber: 1,
                    scheduledWorkouts: 4,
                    completedWorkouts: 4,
                    missedWorkouts: 0,
                    adherenceRate: 100.0,
                    completed: true
                )
            ],
            workoutDetails: [
                WorkoutAdherenceItemDto(
                    trainingId: 201,
                    workoutName: "W1D1",
                    status: "COMPLETED",
                    sessionRpe: 8.5,
                    volumeKg: 4500.0,
                    onTime: true
                )
            ]
        )

        ProgramMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            #expect(request.url?.path == "/api/programs/7/adherence")
            let data = self.encodeISO8601(adherenceDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }

        let adherence = try await repo.getProgramAdherence(id: 7)
        #expect(adherence.programId == 7)
        #expect(adherence.status == .onTrack)
        #expect(adherence.weeklyBreakdowns.count == 1)
        #expect(adherence.workoutDetails.count == 1)
        #expect(adherence.workoutDetails[0].volumeKg == 4500.0)
    }

    @Test("DTO fallback and branch coverage")
    func testDtoFallbackBranches() {
        // ProgramWorkoutResponseDto with nil values
        let workoutDto = ProgramWorkoutResponseDto(dayOfWeek: nil, focus: nil, session: nil)
        let domainWorkout = workoutDto.toDomain()
        #expect(domainWorkout.dayOfWeek == 1)
        #expect(domainWorkout.session.name == "Workout")

        // TrainingProgramResponseDto with nil duration, enums, workouts, isActive
        let programDto = TrainingProgramResponseDto(
            id: 99,
            name: "Bare",
            description: nil,
            durationWeeks: nil,
            periodizationType: nil,
            level: nil,
            workouts: nil,
            isActive: nil
        )
        let domainProg = programDto.toDomain()
        #expect(domainProg.durationWeeks == 4)
        #expect(domainProg.periodizationType == .linear)
        #expect(domainProg.level == .intermediate)
        #expect(domainProg.workouts.isEmpty)
        #expect(domainProg.isActive == true)

        // WorkoutAdherenceItemDto with default values
        let itemDto = WorkoutAdherenceItemDto(
            trainingId: 5,
            workoutName: "Session",
            status: "UNKNOWN_STATUS",
            sessionRpe: nil,
            volumeKg: nil,
            onTime: nil
        )
        let domainItem = itemDto.toDomain()
        #expect(domainItem.status == .planned)
        #expect(domainItem.volumeKg == 0.0)
        #expect(domainItem.onTime == false)

        // ProgramAdherenceResponseDto with nil status description and collections
        let adhDto = ProgramAdherenceResponseDto(
            programId: 1,
            programName: "Prog",
            durationWeeks: 4,
            totalScheduledWorkouts: 12,
            completedWorkouts: 0,
            inProgressWorkouts: 0,
            plannedWorkouts: 12,
            missedWorkouts: 0,
            cancelledWorkouts: 0,
            overallCompletionRate: 0,
            currentAdherenceRate: 0,
            currentStreak: 0,
            longestStreak: 0,
            status: "INVALID_STATUS",
            statusDescription: nil,
            weeklyBreakdowns: nil,
            workoutDetails: nil
        )
        let domainAdh = adhDto.toDomain()
        #expect(domainAdh.status == .notStarted)
        #expect(domainAdh.statusDescription == "")
        #expect(domainAdh.weeklyBreakdowns.isEmpty)
        #expect(domainAdh.workoutDetails.isEmpty)
    }
}
