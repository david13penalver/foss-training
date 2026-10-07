import Foundation

public enum APIError: LocalizedError, Sendable {
    case invalidURL
    case networkError(String)
    case serverError(statusCode: Int, message: String)
    case decodingError(String)

    public var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid server URL configuration."
        case .networkError(let message):
            return "Network connection error: \(message)"
        case .serverError(let code, let msg):
            return "Server responded with code \(code): \(msg)"
        case .decodingError(let msg):
            return "Failed to parse server response: \(msg)"
        }
    }
}

public actor NetworkClient {
    private var baseURLString: String
    private let session: URLSession
    private let jsonDecoder: JSONDecoder
    private let jsonEncoder: JSONEncoder

    public init(baseURLString: String = "http://localhost:8080", session: URLSession = .shared) {
        self.baseURLString = baseURLString
        self.session = session

        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        self.jsonDecoder = decoder

        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        self.jsonEncoder = encoder
    }

    public func setBaseURL(_ url: String) {
        self.baseURLString = url
    }

    public func get<T: Decodable>(endpoint: String) async throws -> T {
        guard let url = URL(string: "\(baseURLString)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        return try await performRequest(request)
    }

    public func post<B: Encodable, T: Decodable>(endpoint: String, body: B) async throws -> T {
        guard let url = URL(string: "\(baseURLString)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = try jsonEncoder.encode(body)

        return try await performRequest(request)
    }

    public func put<B: Encodable, T: Decodable>(endpoint: String, body: B) async throws -> T {
        guard let url = URL(string: "\(baseURLString)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = try jsonEncoder.encode(body)

        return try await performRequest(request)
    }

    public func postNoResponse<B: Encodable>(endpoint: String, body: B) async throws {
        guard let url = URL(string: "\(baseURLString)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try jsonEncoder.encode(body)

        let (_, response) = try await session.data(for: request)
        guard let http = response as? HTTPURLResponse else {
            throw APIError.networkError("Invalid HTTP response")
        }
        guard (200...299).contains(http.statusCode) else {
            throw APIError.serverError(statusCode: http.statusCode, message: "Request failed")
        }
    }

    public func postEmpty<T: Decodable>(endpoint: String) async throws -> T {
        guard let url = URL(string: "\(baseURLString)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        return try await performRequest(request)
    }

    public func delete(endpoint: String) async throws {
        guard let url = URL(string: "\(baseURLString)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "DELETE"

        let (_, response) = try await session.data(for: request)
        guard let http = response as? HTTPURLResponse else {
            throw APIError.networkError("Invalid HTTP response")
        }
        guard (200...299).contains(http.statusCode) else {
            throw APIError.serverError(statusCode: http.statusCode, message: "DELETE failed")
        }
    }

    private func performRequest<T: Decodable>(_ request: URLRequest) async throws -> T {
        do {
            let (data, response) = try await session.data(for: request)
            guard let http = response as? HTTPURLResponse else {
                throw APIError.networkError("Invalid HTTP response")
            }

            guard (200...299).contains(http.statusCode) else {
                let message = String(data: data, encoding: .utf8) ?? "Server returned \(http.statusCode)"
                throw APIError.serverError(statusCode: http.statusCode, message: message)
            }

            do {
                return try jsonDecoder.decode(T.self, from: data)
            } catch {
                throw APIError.decodingError(error.localizedDescription)
            }
        } catch let err as APIError {
            throw err
        } catch {
            throw APIError.networkError(error.localizedDescription)
        }
    }
}
