import Foundation
import SwiftData

@MainActor
public final class SwiftDataAthleteRepository: AthleteRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func getBodyweightHistory() async throws -> [BodyweightEntry] {
        let descriptor = FetchDescriptor<SDBodyweightEntry>(sortBy: [SortDescriptor(\.measuredDate, order: .forward)])
        return try modelContext.fetch(descriptor).map { $0.toDomain() }
    }

    public func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry {
        let all = try await getBodyweightHistory()
        let nextId = (all.map(\.id).max() ?? 0) + 1
        let finalEntry = BodyweightEntry(id: nextId, weightKg: entry.weightKg, measuredDate: entry.measuredDate, notes: entry.notes)
        let sd = SDBodyweightEntry.fromDomain(finalEntry)
        modelContext.insert(sd)
        try modelContext.save()
        return finalEntry
    }

    public func deleteBodyweight(id: Int) async throws {
        let descriptor = FetchDescriptor<SDBodyweightEntry>(predicate: #Predicate<SDBodyweightEntry> { $0.id == id })
        if let existing = try modelContext.fetch(descriptor).first {
            modelContext.delete(existing)
            try modelContext.save()
        }
    }
}
