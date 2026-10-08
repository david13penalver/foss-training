import Foundation
import SwiftData

@MainActor
public final class SwiftDataTrainingProgramRepository: TrainingProgramRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func getPrograms() async throws -> [TrainingProgram] {
        let descriptor = FetchDescriptor<SDTrainingProgram>(sortBy: [SortDescriptor(\.name)])
        return try modelContext.fetch(descriptor).map { $0.toDomain() }
    }

    public func getProgram(id: Int) async throws -> TrainingProgram? {
        let descriptor = FetchDescriptor<SDTrainingProgram>(predicate: #Predicate<SDTrainingProgram> { $0.id == id })
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func saveProgram(_ program: TrainingProgram) async throws -> TrainingProgram {
        let targetId = program.id
        let descriptor = FetchDescriptor<SDTrainingProgram>(predicate: #Predicate<SDTrainingProgram> { $0.id == targetId })
        let existing = try modelContext.fetch(descriptor).first

        var workoutModels: [SDProgramWorkout] = []
        for w in program.workouts {
            let sessionId = w.session.id
            let sessionDescriptor = FetchDescriptor<SDSession>(predicate: #Predicate<SDSession> { $0.id == sessionId })
            let sessionSD = try modelContext.fetch(sessionDescriptor).first
            let sdWorkout = SDProgramWorkout(
                id: w.id,
                dayOfWeek: w.dayOfWeek,
                focus: w.focus,
                session: sessionSD
            )
            workoutModels.append(sdWorkout)
        }

        if let existing {
            existing.name = program.name
            existing.programDescription = program.description
            existing.durationWeeks = program.durationWeeks
            existing.periodizationTypeRaw = program.periodizationType.rawValue
            existing.levelRaw = program.level.rawValue
            existing.isActive = program.isActive
            existing.workouts = workoutModels
            try modelContext.save()
            return existing.toDomain()
        } else {
            let allPrograms = try await getPrograms()
            let nextId: Int
            if program.id > 0 {
                nextId = program.id
            } else if let maxId = allPrograms.map(\.id).max() {
                nextId = maxId + 1
            } else {
                nextId = 1
            }
            let newProgram = SDTrainingProgram(
                id: nextId,
                name: program.name,
                programDescription: program.description,
                durationWeeks: program.durationWeeks,
                periodizationType: program.periodizationType,
                level: program.level,
                workouts: workoutModels,
                isActive: program.isActive
            )
            modelContext.insert(newProgram)
            try modelContext.save()
            return newProgram.toDomain()
        }
    }

    public func deleteProgram(id: Int) async throws {
        let descriptor = FetchDescriptor<SDTrainingProgram>(predicate: #Predicate<SDTrainingProgram> { $0.id == id })
        if let existing = try modelContext.fetch(descriptor).first {
            modelContext.delete(existing)
            try modelContext.save()
        }
    }

    public func programExists(id: Int) async throws -> Bool {
        let descriptor = FetchDescriptor<SDTrainingProgram>(predicate: #Predicate<SDTrainingProgram> { $0.id == id })
        return try modelContext.fetchCount(descriptor) > 0
    }

    public func cloneProgram(id: Int, newName: String?) async throws -> TrainingProgram {
        guard let original = try await getProgram(id: id) else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training program not found"])
        }
        let allPrograms = try await getPrograms()
        let nextId = allPrograms.map(\.id).max()! + 1

        let resolvedName: String
        if let newName, !newName.trimmingCharacters(in: .whitespaces).isEmpty {
            resolvedName = newName.trimmingCharacters(in: .whitespaces)
        } else {
            resolvedName = "\(original.name) (Copy)"
        }

        let clonedWorkouts = original.workouts.map { w in
            ProgramWorkout(
                id: UUID(),
                dayOfWeek: w.dayOfWeek,
                focus: w.focus,
                session: w.session
            )
        }

        let clonedProgram = TrainingProgram(
            id: nextId,
            name: resolvedName,
            description: original.description,
            durationWeeks: original.durationWeeks,
            periodizationType: original.periodizationType,
            level: original.level,
            workouts: clonedWorkouts,
            isActive: original.isActive
        )

        return try await saveProgram(clonedProgram)
    }

    public func generateSchedule(programId: Int, startDate: Date?) async throws -> [Training] {
        guard let program = try await getProgram(id: programId) else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training program not found"])
        }
        guard !program.workouts.isEmpty else {
            throw NSError(domain: "FOSSTraining", code: 400, userInfo: [NSLocalizedDescriptionKey: "Program has no workout days configured"])
        }

        let scheduledTrainings = program.generateSchedule(startDate: startDate)
        let allTrainingsDescriptor = FetchDescriptor<SDTraining>()
        var currentMaxId: Int
        let existingTrainings = try modelContext.fetch(allTrainingsDescriptor)
        if let maxId = existingTrainings.map(\.id).max() {
            currentMaxId = maxId
        } else {
            currentMaxId = 0
        }

        var createdTrainings: [Training] = []
        for t in scheduledTrainings {
            currentMaxId += 1
            let sdTraining = SDTraining(
                id: currentMaxId,
                name: t.name,
                trainingDescription: t.description,
                trainingDate: t.trainingDate,
                startTime: nil,
                endTime: nil,
                status: .planned,
                notes: nil,
                overallRpe: nil,
                programId: programId,
                loggedExercises: t.loggedExercises.map { SDSessionExercise.fromDomain($0) }
            )
            modelContext.insert(sdTraining)
            createdTrainings.append(sdTraining.toDomain())
        }

        try modelContext.save()
        return createdTrainings
    }

    public func getProgramAdherence(id: Int) async throws -> ProgramAdherence {
        guard let program = try await getProgram(id: id) else {
            throw NSError(domain: "FOSSTraining", code: 404, userInfo: [NSLocalizedDescriptionKey: "Training program not found"])
        }

        let allTrainingsDescriptor = FetchDescriptor<SDTraining>()
        let programTrainings = try modelContext.fetch(allTrainingsDescriptor)
            .filter { $0.programId == id }
            .map { $0.toDomain() }

        return ProgramAdherenceCalculator.calculate(program: program, trainings: programTrainings)
    }
}
