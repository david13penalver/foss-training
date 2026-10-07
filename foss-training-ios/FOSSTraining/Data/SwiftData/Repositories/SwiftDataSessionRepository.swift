import Foundation
import SwiftData

@MainActor
public final class SwiftDataSessionRepository: SessionRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func getSessions() async throws -> [Session] {
        let descriptor = FetchDescriptor<SDSession>(sortBy: [SortDescriptor(\.name)])
        return try modelContext.fetch(descriptor).map { $0.toDomain() }
    }

    public func getSession(id: Int) async throws -> Session? {
        let descriptor = FetchDescriptor<SDSession>(predicate: #Predicate<SDSession> { $0.id == id })
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func saveSession(_ session: Session) async throws -> Session {
        let descriptor = FetchDescriptor<SDSession>(predicate: #Predicate<SDSession> { $0.id == session.id })
        if let existing = try modelContext.fetch(descriptor).first {
            existing.name = session.name
            existing.sessionDescription = session.description
            existing.notes = session.notes
            existing.estimatedDurationMinutes = session.estimatedDurationMinutes
            existing.exercises = session.exercises.map { SDSessionExercise.fromDomain($0) }
            try modelContext.save()
            return existing.toDomain()
        } else {
            let newSD = SDSession.fromDomain(session)
            modelContext.insert(newSD)
            try modelContext.save()
            return newSD.toDomain()
        }
    }

    public func deleteSession(id: Int) async throws {
        let descriptor = FetchDescriptor<SDSession>(predicate: #Predicate<SDSession> { $0.id == id })
        if let existing = try modelContext.fetch(descriptor).first {
            modelContext.delete(existing)
            try modelContext.save()
        }
    }

    public func sessionExists(id: Int) async throws -> Bool {
        let descriptor = FetchDescriptor<SDSession>(predicate: #Predicate<SDSession> { $0.id == id })
        return try modelContext.fetchCount(descriptor) > 0
    }

    public func cloneSession(id: Int) async throws -> Session {
        guard let original = try await getSession(id: id) else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Session not found"])
        }
        let allSessions = try await getSessions()
        let nextId = (allSessions.map(\.id).max()!) + 1
        let cloned = original
        let clonedSession = Session(
            id: nextId,
            name: "\(cloned.name) (Copy)",
            description: cloned.description,
            notes: cloned.notes,
            estimatedDurationMinutes: cloned.estimatedDurationMinutes,
            exercises: cloned.exercises
        )
        return try await saveSession(clonedSession)
    }
}
