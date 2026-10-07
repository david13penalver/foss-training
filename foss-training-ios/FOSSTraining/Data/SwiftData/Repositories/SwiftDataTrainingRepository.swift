import Foundation
import SwiftData

@MainActor
public final class SwiftDataTrainingRepository: TrainingRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func getTrainings() async throws -> [Training] {
        let descriptor = FetchDescriptor<SDTraining>(sortBy: [SortDescriptor(\.trainingDate, order: .reverse)])
        return try modelContext.fetch(descriptor).map { $0.toDomain() }
    }

    public func getTraining(id: Int) async throws -> Training? {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == id })
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func createTrainingFromSession(sessionId: Int) async throws -> Training {
        let sessionDescriptor = FetchDescriptor<SDSession>(predicate: #Predicate<SDSession> { $0.id == sessionId })
        guard let session = try modelContext.fetch(sessionDescriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Session template not found"])
        }

        let allTrainings = try await getTrainings()
        let nextId = (allTrainings.map(\.id).max() ?? 0) + 1

        let newTraining = SDTraining(
            id: nextId,
            name: session.name,
            trainingDescription: session.sessionDescription,
            trainingDate: Date(),
            status: .planned,
            notes: session.notes,
            loggedExercises: session.exercises.map { ex in
                SDSessionExercise(
                    orderIndex: ex.orderIndex,
                    exerciseId: ex.exerciseId,
                    exerciseName: ex.exerciseName,
                    part: SessionPartEnum(rawValue: ex.partRaw) ?? .main,
                    restSeconds: ex.restSeconds,
                    sets: ex.sets.map { s in
                        SDResistanceSet(
                            setNumber: s.setNumber,
                            setType: SetType(rawValue: s.setTypeRaw) ?? .normal,
                            weightKg: s.weightKg,
                            repetitions: s.repetitions,
                            rpe: s.rpe,
                            restSeconds: s.restSeconds,
                            isCompleted: false
                        )
                    }
                )
            }
        )

        modelContext.insert(newTraining)
        try modelContext.save()
        return newTraining.toDomain()
    }

    public func startTraining(id: Int) async throws -> Training {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == id })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }
        sd.statusRaw = TrainingStatus.inProgress.rawValue
        sd.startTime = Date()
        try modelContext.save()
        return sd.toDomain()
    }

    public func pauseTraining(id: Int) async throws -> Training {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == id })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }
        sd.statusRaw = TrainingStatus.paused.rawValue
        try modelContext.save()
        return sd.toDomain()
    }

    public func resumeTraining(id: Int) async throws -> Training {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == id })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }
        sd.statusRaw = TrainingStatus.inProgress.rawValue
        try modelContext.save()
        return sd.toDomain()
    }

    public func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == id })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }
        sd.statusRaw = TrainingStatus.completed.rawValue
        sd.endTime = Date()
        sd.overallRpe = overallRpe
        if let notes = notes {
            sd.notes = notes
        }
        try modelContext.save()
        return sd.toDomain()
    }

    public func cancelTraining(id: Int) async throws -> Training {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == id })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }
        sd.statusRaw = TrainingStatus.cancelled.rawValue
        sd.endTime = Date()
        try modelContext.save()
        return sd.toDomain()
    }

    public func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == trainingId })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }

        if let ex = sd.loggedExercises.first(where: { $0.exerciseId == exerciseId }) {
            let newSDSet = SDResistanceSet.fromDomain(set)
            ex.sets.append(newSDSet)
            try modelContext.save()
            return set
        }

        throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Exercise not found in training"])
    }

    public func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == trainingId })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }

        if let ex = sd.loggedExercises.first(where: { $0.exerciseId == exerciseId }) {
            if let targetSet = ex.sets.first(where: { $0.setNumber == set.setNumber }) {
                targetSet.weightKg = set.weightKg
                targetSet.repetitions = set.repetitions
                targetSet.rpe = set.rpe
                targetSet.setTypeRaw = set.setType.rawValue
                targetSet.restSeconds = set.restSeconds
                targetSet.isCompleted = set.isCompleted
                try modelContext.save()
                return targetSet.toDomain()
            }
        }

        throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Set not found"])
    }

    public func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws {
        let descriptor = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == trainingId })
        guard let sd = try modelContext.fetch(descriptor).first else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training not found"])
        }

        if let ex = sd.loggedExercises.first(where: { $0.exerciseId == exerciseId }) {
            if let index = ex.sets.firstIndex(where: { $0.setNumber == setNumber }) {
                let setToRemove = ex.sets[index]
                ex.sets.remove(at: index)
                modelContext.delete(setToRemove)
                try modelContext.save()
            }
        }
    }
}
