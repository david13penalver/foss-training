import Foundation
import SwiftData

@MainActor
public final class SwiftDataPortabilityRepository: DataPortabilityRepository {
    private let modelContext: ModelContext
    let clientFactory: @Sendable (URL) -> NetworkClient
    private let exerciseRepo: SwiftDataExerciseRepository
    private let sessionRepo: SwiftDataSessionRepository
    private let trainingRepo: SwiftDataTrainingRepository
    private let athleteRepo: SwiftDataAthleteRepository
    private let programRepo: SwiftDataTrainingProgramRepository

    public init(
        modelContext: ModelContext,
        clientFactory: (@Sendable (URL) -> NetworkClient)? = nil
    ) {
        self.modelContext = modelContext
        self.clientFactory = clientFactory ?? { url in NetworkClient(baseURLString: url.absoluteString) }
        self.exerciseRepo = SwiftDataExerciseRepository(modelContext: modelContext)
        self.sessionRepo = SwiftDataSessionRepository(modelContext: modelContext)
        self.trainingRepo = SwiftDataTrainingRepository(modelContext: modelContext)
        self.athleteRepo = SwiftDataAthleteRepository(modelContext: modelContext)
        self.programRepo = SwiftDataTrainingProgramRepository(modelContext: modelContext)
    }

    public func generateBackup() async throws -> FullBackupData {
        let exercises = try await exerciseRepo.getExercises(category: nil, search: nil)
        let sessions = try await sessionRepo.getSessions()
        let programs = try await programRepo.getPrograms()
        let trainings = try await trainingRepo.getTrainings()
        let bodyweights = try await athleteRepo.getBodyweightHistory()

        return FullBackupData(
            exportVersion: "1.0",
            exportedAt: Date(),
            exercises: exercises,
            sessions: sessions,
            programs: programs,
            trainings: trainings,
            bodyweightEntries: bodyweights
        )
    }

    public func restoreBackup(_ backup: FullBackupData, mode: ImportMode) async throws -> ImportSummary {
        var exCount = 0
        var sesCount = 0
        var progCount = 0
        var trCount = 0
        var bwCount = 0

        // 1. Exercises
        let existingExercises = try await exerciseRepo.getExercises(category: nil, search: nil)
        var maxExerciseId = existingExercises.map(\.id).max() ?? 0

        for ex in backup.exercises {
            let exists = existingExercises.first(where: { $0.id == ex.id })
            switch mode {
            case .skipExisting:
                if exists == nil {
                    _ = try await exerciseRepo.saveExercise(ex)
                    exCount += 1
                }
            case .overwrite:
                _ = try await exerciseRepo.saveExercise(ex)
                exCount += 1
            case .merge:
                if exists != nil {
                    maxExerciseId += 1
                    let merged = Exercise(
                        id: maxExerciseId,
                        name: ex.name,
                        description: ex.description,
                        images: ex.images,
                        video: ex.video,
                        primaryCategory: ex.primaryCategory,
                        secondaryCategories: ex.secondaryCategories,
                        primaryMuscleGroup: ex.primaryMuscleGroup,
                        secondaryMuscleGroups: ex.secondaryMuscleGroups,
                        movementPattern: ex.movementPattern,
                        enduranceType: ex.enduranceType,
                        mobilityType: ex.mobilityType,
                        targetJoints: ex.targetJoints,
                        equipmentRequired: ex.equipmentRequired,
                        difficultyLevel: ex.difficultyLevel,
                        stepByStepInstructions: ex.stepByStepInstructions,
                        commonMistakes: ex.commonMistakes,
                        safetyTips: ex.safetyTips,
                        tags: ex.tags,
                        isActive: ex.isActive
                    )
                    _ = try await exerciseRepo.saveExercise(merged)
                } else {
                    _ = try await exerciseRepo.saveExercise(ex)
                }
                exCount += 1
            }
        }

        // 2. Sessions
        let existingSessions = try await sessionRepo.getSessions()
        var maxSessionId = existingSessions.map(\.id).max() ?? 0

        for ses in backup.sessions {
            let exists = existingSessions.first(where: { $0.id == ses.id })
            switch mode {
            case .skipExisting:
                if exists == nil {
                    _ = try await sessionRepo.saveSession(ses)
                    sesCount += 1
                }
            case .overwrite:
                _ = try await sessionRepo.saveSession(ses)
                sesCount += 1
            case .merge:
                if exists != nil {
                    maxSessionId += 1
                    let merged = Session(
                        id: maxSessionId,
                        name: ses.name,
                        description: ses.description,
                        notes: ses.notes,
                        estimatedDurationMinutes: ses.estimatedDurationMinutes,
                        exercises: ses.exercises
                    )
                    _ = try await sessionRepo.saveSession(merged)
                } else {
                    _ = try await sessionRepo.saveSession(ses)
                }
                sesCount += 1
            }
        }

        // 3. Programs
        let existingPrograms = try await programRepo.getPrograms()
        var maxProgId = existingPrograms.map(\.id).max() ?? 0

        for prog in backup.programs {
            let exists = existingPrograms.first(where: { $0.id == prog.id })
            switch mode {
            case .skipExisting:
                if exists == nil {
                    _ = try await programRepo.saveProgram(prog)
                    progCount += 1
                }
            case .overwrite:
                _ = try await programRepo.saveProgram(prog)
                progCount += 1
            case .merge:
                if exists != nil {
                    maxProgId += 1
                    let merged = TrainingProgram(
                        id: maxProgId,
                        name: prog.name,
                        description: prog.description,
                        durationWeeks: prog.durationWeeks,
                        periodizationType: prog.periodizationType,
                        level: prog.level,
                        workouts: prog.workouts,
                        isActive: prog.isActive
                    )
                    _ = try await programRepo.saveProgram(merged)
                } else {
                    _ = try await programRepo.saveProgram(prog)
                }
                progCount += 1
            }
        }

        // 4. Trainings
        let existingTrainings = try await trainingRepo.getTrainings()
        var maxTrId = existingTrainings.map(\.id).max() ?? 0

        for tr in backup.trainings {
            let exists = existingTrainings.first(where: { $0.id == tr.id })
            switch mode {
            case .skipExisting:
                if exists == nil {
                    modelContext.insert(SDTraining.fromDomain(tr))
                    trCount += 1
                }
            case .overwrite:
                let targetId = tr.id
                let desc = FetchDescriptor<SDTraining>(predicate: #Predicate<SDTraining> { $0.id == targetId })
                if let old = try? modelContext.fetch(desc).first {
                    modelContext.delete(old)
                }
                modelContext.insert(SDTraining.fromDomain(tr))
                trCount += 1
            case .merge:
                if exists != nil {
                    maxTrId += 1
                    let merged = Training(
                        id: maxTrId,
                        name: tr.name,
                        description: tr.description,
                        trainingDate: tr.trainingDate,
                        startTime: tr.startTime,
                        endTime: tr.endTime,
                        status: tr.status,
                        notes: tr.notes,
                        overallRpe: tr.overallRpe,
                        programId: tr.programId,
                        loggedExercises: tr.loggedExercises
                    )
                    modelContext.insert(SDTraining.fromDomain(merged))
                } else {
                    modelContext.insert(SDTraining.fromDomain(tr))
                }
                trCount += 1
            }
        }

        // 5. Bodyweight entries
        let existingBw = try await athleteRepo.getBodyweightHistory()
        var maxBwId = existingBw.map(\.id).max() ?? 0

        for bw in backup.bodyweightEntries {
            let exists = existingBw.first(where: { $0.id == bw.id })
            switch mode {
            case .skipExisting:
                if exists == nil {
                    _ = try await athleteRepo.logBodyweight(entry: bw)
                    bwCount += 1
                }
            case .overwrite:
                if exists != nil {
                    try? await athleteRepo.deleteBodyweight(id: bw.id)
                }
                _ = try await athleteRepo.logBodyweight(entry: bw)
                bwCount += 1
            case .merge:
                if exists != nil {
                    maxBwId += 1
                    let merged = BodyweightEntry(
                        id: maxBwId,
                        weightKg: bw.weightKg,
                        measuredDate: bw.measuredDate,
                        notes: bw.notes
                    )
                    _ = try await athleteRepo.logBodyweight(entry: merged)
                } else {
                    _ = try await athleteRepo.logBodyweight(entry: bw)
                }
                bwCount += 1
            }
        }

        try modelContext.save()

        return ImportSummary(
            exercisesImported: exCount,
            sessionsImported: sesCount,
            programsImported: progCount,
            trainingsImported: trCount,
            bodyweightImported: bwCount
        )
    }

    public func exportWorkoutsCsv() async throws -> String {
        let trainings = try await trainingRepo.getTrainings()
        return WorkoutsCsvFormatter.formatWorkoutsCsv(trainings: trainings)
    }

    public func importWorkoutsCsv(_ csvContent: String) async throws -> ImportSummary {
        let parsedTrainings = WorkoutsCsvFormatter.parseWorkoutsCsv(csvContent)
        var count = 0
        for tr in parsedTrainings {
            modelContext.insert(SDTraining.fromDomain(tr))
            count += 1
        }
        try modelContext.save()
        return ImportSummary(trainingsImported: count)
    }

    public func migrateToRemoteServer(serverUrl: URL, token: String? = nil) async throws -> ImportSummary {
        let backup = try await generateBackup()
        let client = clientFactory(serverUrl)
        let summaryDTO: ImportSummaryDTO = try await client.post(endpoint: "/api/data/import/backup", body: backup)
        return ImportSummary(
            exercisesImported: summaryDTO.exercisesImported ?? 0,
            sessionsImported: summaryDTO.sessionsImported ?? 0,
            programsImported: summaryDTO.programsImported ?? 0,
            trainingsImported: summaryDTO.trainingsImported ?? 0,
            bodyweightImported: summaryDTO.bodyweightsImported ?? 0
        )
    }

    public func purgeLocalDatabase() async throws {
        try modelContext.delete(model: SDResistanceSet.self)
        try modelContext.delete(model: SDSessionExercise.self)
        try modelContext.delete(model: SDSession.self)
        try modelContext.delete(model: SDExercise.self)
        try modelContext.delete(model: SDProgramWorkout.self)
        try modelContext.delete(model: SDTrainingProgram.self)
        try modelContext.delete(model: SDTraining.self)
        try modelContext.delete(model: SDBodyweightEntry.self)
        try modelContext.delete(model: SDAthleteProfile.self)
        try modelContext.save()
    }
}
