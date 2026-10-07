import Foundation

public final class RemoteExerciseRepository: ExerciseRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func getExercises(category: ExerciseCategory?, search: String?) async throws -> [Exercise] {
        var endpoint = "/api/exercises"
        var queryItems: [String] = []
        if let category = category {
            queryItems.append("category=\(category.rawValue)")
        }
        if let search = search, !search.isEmpty {
            queryItems.append("search=\(search.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? search)")
        }
        if !queryItems.isEmpty {
            endpoint += "?" + queryItems.joined(separator: "&")
        }

        return try await client.get(endpoint: endpoint)
    }

    public func getExercise(id: Int) async throws -> Exercise? {
        do {
            return try await client.get(endpoint: "/api/exercises/\(id)")
        } catch APIError.serverError(statusCode: 404, _) {
            return nil
        }
    }

    public func saveExercise(_ exercise: Exercise) async throws -> Exercise {
        if exercise.id > 0 {
            return try await client.post(endpoint: "/api/exercises/\(exercise.id)", body: exercise)
        } else {
            return try await client.post(endpoint: "/api/exercises", body: exercise)
        }
    }

    public func deleteExercise(id: Int) async throws {
        try await client.delete(endpoint: "/api/exercises/\(id)")
    }
}
