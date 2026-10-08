import Foundation

public struct SessionExerciseDto: Codable, Sendable {
    public let id: Int?
    public let exerciseId: Int
    public let exerciseName: String
    public let orderIndex: Int
    public let part: String
    public let restSeconds: Int
    public let sets: [ResistanceSet]?

    public init(item: SessionExerciseItem) {
        self.id = nil
        self.exerciseId = item.exerciseId
        self.exerciseName = item.exerciseName
        self.orderIndex = item.orderIndex
        self.part = item.part.rawValue
        self.restSeconds = item.restSeconds
        self.sets = item.sets
    }
}

public struct SessionRequestDto: Codable, Sendable {
    public let id: Int?
    public let name: String
    public let description: String?
    public let notes: String?
    public let estimatedDurationMinutes: Int?
    public let sessionExercises: [SessionExerciseDto]?

    public init(session: Session) {
        self.id = session.id > 0 ? session.id : nil
        self.name = session.name
        self.description = session.description
        self.notes = session.notes
        self.estimatedDurationMinutes = session.estimatedDurationMinutes
        self.sessionExercises = session.exercises.map { SessionExerciseDto(item: $0) }
    }
}

public struct SessionResponseDto: Codable, Sendable {
    public let id: Int
    public let name: String
    public let description: String?
    public let notes: String?
    public let estimatedDurationMinutes: Int?
    public let sessionExercises: [SessionExerciseDto]?

    public init(
        id: Int,
        name: String,
        description: String? = nil,
        notes: String? = nil,
        estimatedDurationMinutes: Int? = 60,
        sessionExercises: [SessionExerciseDto]? = nil
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.notes = notes
        self.estimatedDurationMinutes = estimatedDurationMinutes
        self.sessionExercises = sessionExercises
    }

    public func toDomain() -> Session {
        Session(
            id: id,
            name: name,
            description: description,
            notes: notes,
            estimatedDurationMinutes: estimatedDurationMinutes ?? 60,
            exercises: (sessionExercises ?? []).map { ex in
                SessionExerciseItem(
                    orderIndex: ex.orderIndex,
                    exerciseId: ex.exerciseId,
                    exerciseName: ex.exerciseName,
                    part: SessionPartEnum(rawValue: ex.part) ?? .main,
                    restSeconds: ex.restSeconds,
                    sets: ex.sets ?? []
                )
            }
        )
    }
}

public struct CloneSessionRequestDto: Codable, Sendable {
    public let name: String?

    public init(name: String? = nil) {
        self.name = name
    }
}

public final class RemoteSessionRepository: SessionRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func getSessions() async throws -> [Session] {
        let dtos: [SessionResponseDto] = try await client.get(endpoint: "/api/sessions")
        return dtos.map { $0.toDomain() }
    }

    public func getSession(id: Int) async throws -> Session? {
        do {
            let dto: SessionResponseDto = try await client.get(endpoint: "/api/sessions/\(id)")
            return dto.toDomain()
        } catch APIError.serverError(statusCode: 404, _) {
            return nil
        }
    }

    public func saveSession(_ session: Session) async throws -> Session {
        let req = SessionRequestDto(session: session)
        if session.id > 0 {
            let dto: SessionResponseDto = try await client.put(endpoint: "/api/sessions/\(session.id)", body: req)
            return dto.toDomain()
        } else {
            let dto: SessionResponseDto = try await client.post(endpoint: "/api/sessions", body: req)
            return dto.toDomain()
        }
    }

    public func deleteSession(id: Int) async throws {
        try await client.delete(endpoint: "/api/sessions/\(id)")
    }

    public func cloneSession(id: Int) async throws -> Session {
        let dto: SessionResponseDto = try await client.post(endpoint: "/api/sessions/\(id)/clone", body: CloneSessionRequestDto())
        return dto.toDomain()
    }

    public func sessionExists(id: Int) async throws -> Bool {
        try await client.get(endpoint: "/api/sessions/\(id)/exists")
    }
}
