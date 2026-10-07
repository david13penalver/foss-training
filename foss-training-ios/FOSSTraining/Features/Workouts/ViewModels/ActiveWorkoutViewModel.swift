import SwiftUI

@Observable
@MainActor
public final class ActiveWorkoutViewModel {
    private let trainingRepository: TrainingRepository

    public var training: Training
    public var elapsedSeconds: Int = 0
    public var isTimerRunning: Bool = false
    public var restTimerSecondsRemaining: Int = 0
    public var isRestTimerActive: Bool = false

    public var selectedRpe: Double = 7.5
    public var completionNotes: String = ""
    public var isCompleting: Bool = false
    public var isFinished: Bool = false
    public var errorMessage: String?

    public init(training: Training, trainingRepository: TrainingRepository) {
        self.training = training
        self.trainingRepository = trainingRepository
        self.isTimerRunning = training.status == .inProgress
    }

    public func tickElapsed() {
        if isTimerRunning {
            elapsedSeconds += 1
        }
        if isRestTimerActive && restTimerSecondsRemaining > 0 {
            restTimerSecondsRemaining -= 1
            if restTimerSecondsRemaining == 0 {
                isRestTimerActive = false
            }
        }
    }

    public func toggleSetCompleted(exerciseId: Int, setNumber: Int) async {
        guard let exIndex = training.loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }),
              let setIndex = training.loggedExercises[exIndex].sets.firstIndex(where: { $0.setNumber == setNumber }) else {
            return
        }

        var currentSet = training.loggedExercises[exIndex].sets[setIndex]
        currentSet.isCompleted.toggle()
        training.loggedExercises[exIndex].sets[setIndex] = currentSet

        // Start rest timer if completed
        if currentSet.isCompleted {
            self.restTimerSecondsRemaining = currentSet.restSeconds
            self.isRestTimerActive = true
        }

        do {
            _ = try await trainingRepository.updateSet(trainingId: training.id, exerciseId: exerciseId, set: currentSet)
        } catch {
            self.errorMessage = "Failed to update set: \(error.localizedDescription)"
        }
    }

    public func updateSetValues(exerciseId: Int, setNumber: Int, weight: Double, reps: Int) async {
        guard let exIndex = training.loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }),
              let setIndex = training.loggedExercises[exIndex].sets.firstIndex(where: { $0.setNumber == setNumber }) else {
            return
        }

        var currentSet = training.loggedExercises[exIndex].sets[setIndex]
        currentSet.weightKg = weight
        currentSet.repetitions = reps
        training.loggedExercises[exIndex].sets[setIndex] = currentSet

        do {
            _ = try await trainingRepository.updateSet(trainingId: training.id, exerciseId: exerciseId, set: currentSet)
        } catch {
            self.errorMessage = "Failed to update set values: \(error.localizedDescription)"
        }
    }

    public func finishWorkout() async {
        do {
            self.training = try await trainingRepository.completeTraining(
                id: training.id,
                overallRpe: selectedRpe,
                notes: completionNotes.isEmpty ? nil : completionNotes
            )
            self.isTimerRunning = false
            self.isFinished = true
        } catch {
            self.errorMessage = "Failed to finish workout: \(error.localizedDescription)"
        }
    }
}
