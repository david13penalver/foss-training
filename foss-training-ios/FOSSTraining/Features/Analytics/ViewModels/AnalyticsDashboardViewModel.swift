import Foundation
import SwiftUI

public enum AnalyticsTab: String, CaseIterable, Identifiable, Sendable {
    case overview = "OVERVIEW"
    case acwr = "ACWR"
    case hypertrophy = "HYPERTROPHY"
    case progression = "PROGRESSION"
    case records = "RECORDS"
    case oneRepMax = "1RM"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .overview: return "Overview"
        case .acwr: return "Workload (ACWR)"
        case .hypertrophy: return "Muscle Volume"
        case .progression: return "Progression"
        case .records: return "Personal Records"
        case .oneRepMax: return "1RM Calculator"
        }
    }
}

@Observable
@MainActor
public final class AnalyticsDashboardViewModel {
    private let analyticsRepository: AnalyticsRepository
    private let exerciseRepository: ExerciseRepository

    public var selectedTab: AnalyticsTab = .overview
    public var workloadRatio: WorkloadRatio?
    public var weeklyMuscleVolume: WeeklyMuscleVolume?
    public var personalRecords: [PersonalRecord] = []
    public var exercises: [Exercise] = []
    public var selectedExerciseForProgression: Exercise?
    public var progressionMonths: Int = 3
    public var exerciseProgression: ExerciseProgression?
    public var isLoading: Bool = false
    public var errorMessage: String?

    public init(
        analyticsRepository: AnalyticsRepository,
        exerciseRepository: ExerciseRepository
    ) {
        self.analyticsRepository = analyticsRepository
        self.exerciseRepository = exerciseRepository
    }

    public func loadDashboard() async {
        isLoading = true
        errorMessage = nil

        do {
            async let acwrTask = analyticsRepository.calculateAcwr(asOfDate: nil)
            async let volTask = analyticsRepository.getWeeklyMuscleVolume(weekStartDate: nil)
            async let prsTask = analyticsRepository.getPersonalRecords(exerciseId: nil)
            async let exTask = exerciseRepository.getExercises(category: nil, search: nil)

            let (acwr, vol, prs, exList) = try await (acwrTask, volTask, prsTask, exTask)
            self.workloadRatio = acwr
            self.weeklyMuscleVolume = vol
            self.personalRecords = prs
            self.exercises = exList

            if selectedExerciseForProgression == nil {
                self.selectedExerciseForProgression = exList.first
            }

            if let selected = self.selectedExerciseForProgression {
                await loadProgression(for: selected)
            }
        } catch {
            self.errorMessage = error.localizedDescription
        }

        isLoading = false
    }

    public func selectExerciseForProgression(_ exercise: Exercise) async {
        self.selectedExerciseForProgression = exercise
        await loadProgression(for: exercise)
    }

    public func setProgressionMonths(_ months: Int) async {
        self.progressionMonths = max(1, months)
        if let selected = selectedExerciseForProgression {
            await loadProgression(for: selected)
        }
    }

    public func loadProgression(for exercise: Exercise) async {
        do {
            self.exerciseProgression = try await analyticsRepository.getExerciseProgression(
                exerciseId: exercise.id,
                months: progressionMonths
            )
        } catch {
            self.errorMessage = error.localizedDescription
        }
    }
}
