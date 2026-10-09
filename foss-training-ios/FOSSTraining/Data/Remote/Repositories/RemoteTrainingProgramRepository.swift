import Foundation

public struct ProgramWorkoutRequestDto: Codable, Sendable {
    public let dayOfWeek: Int
    public let focus: String?
    public let session: SessionRequestDto?

    public init(workout: ProgramWorkout) {
        self.dayOfWeek = workout.dayOfWeek
        self.focus = workout.focus
        self.session = SessionRequestDto(session: workout.session)
    }
}

public struct ProgramWorkoutResponseDto: Codable, Sendable {
    public let dayOfWeek: Int?
    public let focus: String?
    public let session: SessionResponseDto?

    public init(dayOfWeek: Int? = 1, focus: String? = nil, session: SessionResponseDto? = nil) {
        self.dayOfWeek = dayOfWeek
        self.focus = focus
        self.session = session
    }

    public func toDomain() -> ProgramWorkout {
        ProgramWorkout(
            id: UUID(),
            dayOfWeek: dayOfWeek ?? 1,
            focus: focus,
            session: session?.toDomain() ?? Session(id: 0, name: "Workout")
        )
    }
}

public struct TrainingProgramRequestDto: Codable, Sendable {
    public let id: Int?
    public let name: String
    public let description: String?
    public let durationWeeks: Int
    public let periodizationType: String
    public let level: String
    public let workouts: [ProgramWorkoutRequestDto]
    public let isActive: Bool

    public init(program: TrainingProgram) {
        self.id = program.id > 0 ? program.id : nil
        self.name = program.name
        self.description = program.description
        self.durationWeeks = program.durationWeeks
        self.periodizationType = program.periodizationType.rawValue
        self.level = program.level.rawValue
        self.workouts = program.workouts.map { ProgramWorkoutRequestDto(workout: $0) }
        self.isActive = program.isActive
    }
}

public struct TrainingProgramResponseDto: Codable, Sendable {
    public let id: Int
    public let name: String
    public let description: String?
    public let durationWeeks: Int?
    public let periodizationType: String?
    public let level: String?
    public let workouts: [ProgramWorkoutResponseDto]?
    public let isActive: Bool?

    public init(
        id: Int,
        name: String,
        description: String? = nil,
        durationWeeks: Int? = 4,
        periodizationType: String? = nil,
        level: String? = nil,
        workouts: [ProgramWorkoutResponseDto]? = nil,
        isActive: Bool? = true
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.durationWeeks = durationWeeks
        self.periodizationType = periodizationType
        self.level = level
        self.workouts = workouts
        self.isActive = isActive
    }

    public func toDomain() -> TrainingProgram {
        TrainingProgram(
            id: id,
            name: name,
            description: description,
            durationWeeks: durationWeeks ?? 4,
            periodizationType: PeriodizationType(rawValue: periodizationType ?? "") ?? .linear,
            level: ProgramLevel(rawValue: level ?? "") ?? .intermediate,
            workouts: (workouts ?? []).map { $0.toDomain() },
            isActive: isActive ?? true
        )
    }
}

public struct CloneProgramRequestDto: Codable, Sendable {
    public let name: String?

    public init(name: String? = nil) {
        self.name = name
    }
}

public struct WeeklyAdherenceDto: Codable, Sendable {
    public let weekNumber: Int
    public let weekStartDate: Date?
    public let weekEndDate: Date?
    public let scheduledWorkouts: Int
    public let completedWorkouts: Int
    public let missedWorkouts: Int
    public let adherenceRate: Double
    public let completed: Bool

    public init(
        weekNumber: Int,
        weekStartDate: Date? = nil,
        weekEndDate: Date? = nil,
        scheduledWorkouts: Int,
        completedWorkouts: Int,
        missedWorkouts: Int,
        adherenceRate: Double,
        completed: Bool
    ) {
        self.weekNumber = weekNumber
        self.weekStartDate = weekStartDate
        self.weekEndDate = weekEndDate
        self.scheduledWorkouts = scheduledWorkouts
        self.completedWorkouts = completedWorkouts
        self.missedWorkouts = missedWorkouts
        self.adherenceRate = adherenceRate
        self.completed = completed
    }

    public func toDomain() -> WeeklyAdherence {
        WeeklyAdherence(
            weekNumber: weekNumber,
            weekStartDate: weekStartDate,
            weekEndDate: weekEndDate,
            scheduledWorkouts: scheduledWorkouts,
            completedWorkouts: completedWorkouts,
            missedWorkouts: missedWorkouts,
            adherenceRate: adherenceRate,
            completed: completed
        )
    }
}

public struct WorkoutAdherenceItemDto: Codable, Sendable {
    public let trainingId: Int
    public let workoutName: String
    public let scheduledDate: Date?
    public let completedDate: Date?
    public let status: String
    public let sessionRpe: Double?
    public let volumeKg: Double?
    public let onTime: Bool?

    public init(
        trainingId: Int,
        workoutName: String,
        scheduledDate: Date? = nil,
        completedDate: Date? = nil,
        status: String = "PLANNED",
        sessionRpe: Double? = nil,
        volumeKg: Double? = 0.0,
        onTime: Bool? = false
    ) {
        self.trainingId = trainingId
        self.workoutName = workoutName
        self.scheduledDate = scheduledDate
        self.completedDate = completedDate
        self.status = status
        self.sessionRpe = sessionRpe
        self.volumeKg = volumeKg
        self.onTime = onTime
    }

    public func toDomain() -> WorkoutAdherenceItem {
        WorkoutAdherenceItem(
            trainingId: trainingId,
            workoutName: workoutName,
            scheduledDate: scheduledDate,
            completedDate: completedDate,
            status: TrainingStatus(rawValue: status) ?? .planned,
            sessionRpe: sessionRpe,
            volumeKg: volumeKg ?? 0.0,
            onTime: onTime ?? false
        )
    }
}

public struct ProgramAdherenceResponseDto: Codable, Sendable {
    public let programId: Int
    public let programName: String
    public let durationWeeks: Int
    public let totalScheduledWorkouts: Int
    public let completedWorkouts: Int
    public let inProgressWorkouts: Int
    public let plannedWorkouts: Int
    public let missedWorkouts: Int
    public let cancelledWorkouts: Int
    public let overallCompletionRate: Double
    public let currentAdherenceRate: Double
    public let currentStreak: Int
    public let longestStreak: Int
    public let status: String
    public let statusDescription: String?
    public let weeklyBreakdowns: [WeeklyAdherenceDto]?
    public let workoutDetails: [WorkoutAdherenceItemDto]?

    public init(
        programId: Int,
        programName: String,
        durationWeeks: Int,
        totalScheduledWorkouts: Int,
        completedWorkouts: Int,
        inProgressWorkouts: Int,
        plannedWorkouts: Int,
        missedWorkouts: Int,
        cancelledWorkouts: Int,
        overallCompletionRate: Double,
        currentAdherenceRate: Double,
        currentStreak: Int,
        longestStreak: Int,
        status: String,
        statusDescription: String? = nil,
        weeklyBreakdowns: [WeeklyAdherenceDto]? = nil,
        workoutDetails: [WorkoutAdherenceItemDto]? = nil
    ) {
        self.programId = programId
        self.programName = programName
        self.durationWeeks = durationWeeks
        self.totalScheduledWorkouts = totalScheduledWorkouts
        self.completedWorkouts = completedWorkouts
        self.inProgressWorkouts = inProgressWorkouts
        self.plannedWorkouts = plannedWorkouts
        self.missedWorkouts = missedWorkouts
        self.cancelledWorkouts = cancelledWorkouts
        self.overallCompletionRate = overallCompletionRate
        self.currentAdherenceRate = currentAdherenceRate
        self.currentStreak = currentStreak
        self.longestStreak = longestStreak
        self.status = status
        self.statusDescription = statusDescription
        self.weeklyBreakdowns = weeklyBreakdowns
        self.workoutDetails = workoutDetails
    }

    public func toDomain() -> ProgramAdherence {
        ProgramAdherence(
            programId: programId,
            programName: programName,
            durationWeeks: durationWeeks,
            totalScheduledWorkouts: totalScheduledWorkouts,
            completedWorkouts: completedWorkouts,
            inProgressWorkouts: inProgressWorkouts,
            plannedWorkouts: plannedWorkouts,
            missedWorkouts: missedWorkouts,
            cancelledWorkouts: cancelledWorkouts,
            overallCompletionRate: overallCompletionRate,
            currentAdherenceRate: currentAdherenceRate,
            currentStreak: currentStreak,
            longestStreak: longestStreak,
            status: ProgramAdherenceStatus(rawValue: status) ?? .notStarted,
            statusDescription: statusDescription ?? "",
            weeklyBreakdowns: (weeklyBreakdowns ?? []).map { $0.toDomain() },
            workoutDetails: (workoutDetails ?? []).map { $0.toDomain() }
        )
    }
}

public final class RemoteTrainingProgramRepository: TrainingProgramRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func getPrograms() async throws -> [TrainingProgram] {
        let dtos: [TrainingProgramResponseDto] = try await client.get(endpoint: "/api/programs")
        return dtos.map { $0.toDomain() }
    }

    public func getProgram(id: Int) async throws -> TrainingProgram? {
        do {
            let dto: TrainingProgramResponseDto = try await client.get(endpoint: "/api/programs/\(id)")
            return dto.toDomain()
        } catch APIError.serverError(statusCode: 404, _) {
            return nil
        }
    }

    public func saveProgram(_ program: TrainingProgram) async throws -> TrainingProgram {
        let req = TrainingProgramRequestDto(program: program)
        if program.id > 0 {
            let dto: TrainingProgramResponseDto = try await client.put(endpoint: "/api/programs/\(program.id)", body: req)
            return dto.toDomain()
        } else {
            let dto: TrainingProgramResponseDto = try await client.post(endpoint: "/api/programs", body: req)
            return dto.toDomain()
        }
    }

    public func deleteProgram(id: Int) async throws {
        try await client.delete(endpoint: "/api/programs/\(id)")
    }

    public func cloneProgram(id: Int, newName: String?) async throws -> TrainingProgram {
        let req = CloneProgramRequestDto(name: newName)
        let dto: TrainingProgramResponseDto = try await client.post(endpoint: "/api/programs/\(id)/clone", body: req)
        return dto.toDomain()
    }

    public func programExists(id: Int) async throws -> Bool {
        try await client.get(endpoint: "/api/programs/\(id)/exists")
    }

    public func generateSchedule(programId: Int, startDate: Date?) async throws -> [Training] {
        var endpoint = "/api/programs/\(programId)/generate-schedule"
        if let startDate {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withFullDate]
            let dateStr = formatter.string(from: startDate)
            endpoint += "?startDate=\(dateStr)"
        }
        return try await client.postEmpty(endpoint: endpoint)
    }

    public func getProgramAdherence(id: Int) async throws -> ProgramAdherence {
        let dto: ProgramAdherenceResponseDto = try await client.get(endpoint: "/api/programs/\(id)/adherence")
        return dto.toDomain()
    }
}
