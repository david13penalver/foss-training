import Foundation
import SwiftData

@MainActor
public final class SwiftDataAnalyticsRepository: AnalyticsRepository {
    private let modelContext: ModelContext

    public init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    public func calculateOneRepMax(weightKg: Double, reps: Int) async throws -> [OneRepMaxEstimate] {
        OneRepMaxCalculator.calculateAll(weightKg: weightKg, reps: reps)
    }

    public func getPersonalRecords(exerciseId: Int?) async throws -> [PersonalRecord] {
        let trainingDescriptor = FetchDescriptor<SDTraining>()
        let trainings = try modelContext.fetch(trainingDescriptor).map { $0.toDomain() }

        let exerciseDescriptor = FetchDescriptor<SDExercise>()
        let exercises = try modelContext.fetch(exerciseDescriptor).map { $0.toDomain() }

        if let exId = exerciseId {
            if let ex = exercises.first(where: { $0.id == exId }) {
                return PersonalRecordTracker.computeForExercise(exerciseId: ex.id, exerciseName: ex.name, trainings: trainings)
            } else {
                return []
            }
        } else {
            return PersonalRecordTracker.computeAll(exercises: exercises, trainings: trainings)
        }
    }

    public func calculateAcwr(asOfDate: Date?) async throws -> WorkloadRatio {
        let trainingDescriptor = FetchDescriptor<SDTraining>()
        let trainings = try modelContext.fetch(trainingDescriptor).map { $0.toDomain() }

        let target = asOfDate != nil ? asOfDate! : Date()
        return AcwrCalculator.calculate(
            trainings: trainings,
            targetDate: target
        )
    }

    public func getWeeklyMuscleVolume(weekStartDate: Date?) async throws -> WeeklyMuscleVolume {
        let trainingDescriptor = FetchDescriptor<SDTraining>()
        let trainings = try modelContext.fetch(trainingDescriptor).map { $0.toDomain() }

        let exerciseDescriptor = FetchDescriptor<SDExercise>()
        let exercises = try modelContext.fetch(exerciseDescriptor).map { $0.toDomain() }

        let startDate = weekStartDate
        let endDate = weekStartDate != nil ? Calendar.current.date(byAdding: .day, value: 6, to: weekStartDate!) : nil

        return MuscleVolumeCalculator.compute(
            exercises: exercises,
            trainings: trainings,
            startDate: startDate,
            endDate: endDate
        )
    }

    public func getExerciseProgression(exerciseId: Int, months: Int) async throws -> ExerciseProgression {
        let exerciseDescriptor = FetchDescriptor<SDExercise>(predicate: #Predicate<SDExercise> { $0.id == exerciseId })
        guard let sdExercise = try modelContext.fetch(exerciseDescriptor).first else {
            throw NSError(
                domain: "FOSSTraining",
                code: 404,
                userInfo: [NSLocalizedDescriptionKey: "Exercise not found with ID \(exerciseId)"]
            )
        }

        let trainingDescriptor = FetchDescriptor<SDTraining>()
        let trainings = try modelContext.fetch(trainingDescriptor).map { $0.toDomain() }

        let startDate = months > 0
            ? Calendar.current.date(byAdding: .month, value: -months, to: Date())
            : nil

        return ExerciseProgressionCalculator.compute(
            exercise: sdExercise.toDomain(),
            trainings: trainings,
            startDate: startDate,
            endDate: nil,
            formula: .epley
        )
    }

    public func calculateHeartRateZones(restingHr: Int, maxHr: Int, method: HeartRateZoneMethod) async throws -> HeartRateZones {
        try HeartRateZoneCalculator.calculate(restingHr: restingHr, maxHr: maxHr, method: method)
    }
}
