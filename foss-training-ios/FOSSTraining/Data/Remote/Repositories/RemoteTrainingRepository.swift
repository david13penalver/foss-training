import Foundation

public struct CompleteTrainingRequest: Codable, Sendable {
    public let overallRpe: Double?
    public let notes: String?

    public init(overallRpe: Double?, notes: String?) {
        self.overallRpe = overallRpe
        self.notes = notes
    }
}

public final class RemoteTrainingRepository: TrainingRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func getTrainings() async throws -> [Training] {
        try await client.get(endpoint: "/api/trainings")
    }

    public func getTraining(id: Int) async throws -> Training? {
        do {
            return try await client.get(endpoint: "/api/trainings/\(id)")
        } catch APIError.serverError(statusCode: 404, _) {
            return nil
        }
    }

    public func createTrainingFromSession(sessionId: Int) async throws -> Training {
        try await client.postEmpty(endpoint: "/api/trainings/from-session/\(sessionId)")
    }

    public func startTraining(id: Int) async throws -> Training {
        try await client.postEmpty(endpoint: "/api/trainings/\(id)/start")
    }

    public func pauseTraining(id: Int) async throws -> Training {
        try await client.postEmpty(endpoint: "/api/trainings/\(id)/pause")
    }

    public func resumeTraining(id: Int) async throws -> Training {
        try await client.postEmpty(endpoint: "/api/trainings/\(id)/resume")
    }

    public func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training {
        let req = CompleteTrainingRequest(overallRpe: overallRpe, notes: notes)
        return try await client.post(endpoint: "/api/trainings/\(id)/complete", body: req)
    }

    public func cancelTraining(id: Int) async throws -> Training {
        try await client.postEmpty(endpoint: "/api/trainings/\(id)/cancel")
    }

    public func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        try await client.post(endpoint: "/api/trainings/\(trainingId)/exercises/\(exerciseId)/sets", body: set)
    }

    public func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        try await client.post(endpoint: "/api/trainings/\(trainingId)/exercises/\(exerciseId)/sets/\(set.setNumber)", body: set)
    }

    public func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws {
        try await client.delete(endpoint: "/api/trainings/\(trainingId)/exercises/\(exerciseId)/sets/\(setNumber)")
    }
}
