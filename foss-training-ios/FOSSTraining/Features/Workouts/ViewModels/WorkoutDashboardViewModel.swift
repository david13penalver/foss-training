import SwiftUI

@Observable
@MainActor
public final class WorkoutDashboardViewModel {
    private let trainingRepository: TrainingRepository

    public var trainings: [Training] = []
    public var searchText: String = ""
    public var isLoading: Bool = false
    public var selectedTraining: Training? = nil
    public var errorMessage: String?

    public init(trainingRepository: TrainingRepository) {
        self.trainingRepository = trainingRepository
    }

    public var activeWorkout: Training? {
        trainings.first { $0.status == .inProgress || $0.status == .paused }
    }

    public var plannedTrainings: [Training] {
        trainings.filter { $0.status == .planned }
    }

    public var completedTrainings: [Training] {
        trainings.filter { $0.status == .completed || $0.status == .cancelled }
    }

    public var filteredCompletedTrainings: [Training] {
        let trimmed = searchText.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty {
            return completedTrainings
        }
        return completedTrainings.filter {
            $0.name.localizedCaseInsensitiveContains(trimmed) ||
            ($0.description?.localizedCaseInsensitiveContains(trimmed) == true) ||
            ($0.notes?.localizedCaseInsensitiveContains(trimmed) == true)
        }
    }

    public func loadTrainings() async {
        isLoading = true
        errorMessage = nil
        do {
            self.trainings = try await trainingRepository.getTrainings()
        } catch {
            self.errorMessage = "Failed to load workouts: \(error.localizedDescription)"
        }
        isLoading = false
    }

    public func startWorkoutFromSession(sessionId: Int) async -> Training? {
        errorMessage = nil
        do {
            let planned = try await trainingRepository.createTrainingFromSession(sessionId: sessionId)
            let started = try await trainingRepository.startTraining(id: planned.id)
            await loadTrainings()
            self.selectedTraining = started
            return started
        } catch {
            self.errorMessage = "Failed to start workout: \(error.localizedDescription)"
            return nil
        }
    }

    public func deleteTraining(id: Int) async {
        errorMessage = nil
        trainings.removeAll { $0.id == id }
    }
}
