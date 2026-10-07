import Foundation
import SwiftData

@MainActor
public final class SwiftDataPortabilityRepository: DataPortabilityRepository {
    private let modelContext: ModelContext
    private let exerciseRepo: SwiftDataExerciseRepository
    private let sessionRepo: SwiftDataSessionRepository
    private let trainingRepo: SwiftDataTrainingRepository
    private let athleteRepo: SwiftDataAthleteRepository

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
        self.exerciseRepo = SwiftDataExerciseRepository(modelContext: modelContext)
        self.sessionRepo = SwiftDataSessionRepository(modelContext: modelContext)
        self.trainingRepo = SwiftDataTrainingRepository(modelContext: modelContext)
        self.athleteRepo = SwiftDataAthleteRepository(modelContext: modelContext)
    }

    public func exportFullBackup() async throws -> BackupDataPayload {
        let exercises = try await exerciseRepo.getExercises(category: nil, search: nil)
        let sessions = try await sessionRepo.getSessions()
        let trainings = try await trainingRepo.getTrainings()
        let bodyweights = try await athleteRepo.getBodyweightHistory()

        return BackupDataPayload(
            exportVersion: "1.0",
            exportedAt: Date(),
            exercises: exercises,
            sessions: sessions,
            trainings: trainings,
            bodyweightEntries: bodyweights
        )
    }

    public func importFullBackup(payload: BackupDataPayload) async throws -> Int {
        var count = 0
        for ex in payload.exercises {
            _ = try await exerciseRepo.saveExercise(ex)
            count += 1
        }
        for ses in payload.sessions {
            _ = try await sessionRepo.saveSession(ses)
            count += 1
        }
        for tr in payload.trainings {
            let sdTr = SDTraining.fromDomain(tr)
            modelContext.insert(sdTr)
            count += 1
        }
        for bw in payload.bodyweightEntries {
            _ = try await athleteRepo.logBodyweight(entry: bw)
            count += 1
        }
        try modelContext.save()
        return count
    }

    public func exportWorkoutsCSV() async throws -> String {
        let trainings = try await trainingRepo.getTrainings()
        var csv = "training_id,training_name,date,exercise,set_number,weight_kg,reps,rpe,completed\n"

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"

        for tr in trainings {
            let dateStr = dateFormatter.string(from: tr.trainingDate)
            for ex in tr.loggedExercises {
                for s in ex.sets {
                    let rpeStr = s.rpe != nil ? String(s.rpe!) : ""
                    csv.append("\(tr.id),\"\(tr.name)\",\(dateStr),\"\(ex.exerciseName)\",\(s.setNumber),\(s.weightKg),\(s.repetitions),\(rpeStr),\(s.isCompleted)\n")
                }
            }
        }

        return csv
    }
}
