import Foundation
import SwiftUI

@Observable
@MainActor
public final class AthleteProfileViewModel {
    private let athleteRepository: AthleteRepository
    private let trainingRepository: TrainingRepository

    public var profile: AthleteProfile = .default
    public var bodyweightHistory: [BodyweightEntry] = []
    public var trendPoints: [BodyweightTrendPoint] = []
    public var squatPrKg: Double = 0.0
    public var benchPrKg: Double = 0.0
    public var deadliftPrKg: Double = 0.0
    public var selectedFormula: ScoringFormula = .dots
    public var relativeStrengthScore: RelativeStrengthScore?
    public var isLoading: Bool = false
    public var errorMessage: String?

    public var bigThreeTotalKg: Double {
        squatPrKg + benchPrKg + deadliftPrKg
    }

    public var currentWeightKg: Double {
        bodyweightHistory.last?.weightKg ?? 75.0
    }

    public var currentEmaKg: Double {
        trendPoints.last?.movingAverageKg ?? currentWeightKg
    }

    public var relativeRatio: Double {
        guard currentWeightKg > 0 else { return 0.0 }
        return ((bigThreeTotalKg / currentWeightKg) * 100.0).rounded() / 100.0
    }

    public init(
        athleteRepository: AthleteRepository,
        trainingRepository: TrainingRepository
    ) {
        self.athleteRepository = athleteRepository
        self.trainingRepository = trainingRepository
    }

    public func loadProfile() async {
        isLoading = true
        errorMessage = nil

        do {
            if let saved = try await athleteRepository.getProfile() {
                self.profile = saved
            }

            let history = try await athleteRepository.getBodyweightHistory()
            self.bodyweightHistory = history
            self.trendPoints = BodyweightMovingAverageCalculator.calculateTrend(entries: history)

            await loadBigThreePrs()
            await recalculateRelativeStrength()
        } catch {
            self.errorMessage = error.localizedDescription
        }

        isLoading = false
    }

    public func saveProfile(_ updated: AthleteProfile) async {
        do {
            let saved = try await athleteRepository.saveProfile(updated)
            self.profile = saved
            await recalculateRelativeStrength()
        } catch {
            self.errorMessage = error.localizedDescription
        }
    }

    public func setScoringFormula(_ formula: ScoringFormula) async {
        self.selectedFormula = formula
        await recalculateRelativeStrength()
    }

    public func logBodyweight(weightKg: Double, notes: String? = nil, date: Date = Date()) async {
        do {
            let entry = BodyweightEntry(id: 0, weightKg: weightKg, measuredDate: date, notes: notes)
            _ = try await athleteRepository.logBodyweight(entry: entry)
            let updatedHistory = try await athleteRepository.getBodyweightHistory()
            self.bodyweightHistory = updatedHistory
            self.trendPoints = BodyweightMovingAverageCalculator.calculateTrend(entries: updatedHistory)
            await recalculateRelativeStrength()
        } catch {
            self.errorMessage = error.localizedDescription
        }
    }

    public func deleteBodyweight(id: Int) async {
        do {
            try await athleteRepository.deleteBodyweight(id: id)
            let updatedHistory = try await athleteRepository.getBodyweightHistory()
            self.bodyweightHistory = updatedHistory
            self.trendPoints = BodyweightMovingAverageCalculator.calculateTrend(entries: updatedHistory)
            await recalculateRelativeStrength()
        } catch {
            self.errorMessage = error.localizedDescription
        }
    }

    private func loadBigThreePrs() async {
        do {
            let trainings = try await trainingRepository.getTrainings()
            var maxSquat: Double = 0.0
            var maxBench: Double = 0.0
            var maxDeadlift: Double = 0.0

            for t in trainings where t.status == .completed {
                for ex in t.loggedExercises {
                    let name = ex.exerciseName.lowercased()
                    for s in ex.sets where s.isCompleted {
                        if name.contains("squat") {
                            maxSquat = max(maxSquat, s.weightKg)
                        } else if name.contains("bench") {
                            maxBench = max(maxBench, s.weightKg)
                        } else if name.contains("deadlift") {
                            maxDeadlift = max(maxDeadlift, s.weightKg)
                        }
                    }
                }
            }

            self.squatPrKg = maxSquat
            self.benchPrKg = maxBench
            self.deadliftPrKg = maxDeadlift
        } catch {}
    }

    public func recalculateRelativeStrength() async {
        let total = bigThreeTotalKg > 0 ? bigThreeTotalKg : 400.0 // Default demo baseline if no workouts completed
        let bw = currentWeightKg
        do {
            self.relativeStrengthScore = try await athleteRepository.calculateRelativeStrength(
                totalKg: total,
                bodyweightKg: bw,
                gender: profile.gender,
                formula: selectedFormula
            )
        } catch {
            self.relativeStrengthScore = RelativeStrengthCalculator.calculate(
                totalKg: total,
                bodyweightKg: bw,
                gender: profile.gender,
                formula: selectedFormula
            )
        }
    }
}
