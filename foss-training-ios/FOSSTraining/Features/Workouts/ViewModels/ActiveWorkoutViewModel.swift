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
    public var isCancelled: Bool = false
    public var errorMessage: String?

    public init(training: Training, trainingRepository: TrainingRepository) {
        self.training = training
        self.trainingRepository = trainingRepository
        self.isTimerRunning = training.status == .inProgress

        if let start = training.startTime {
            let end = training.endTime ?? Date()
            self.elapsedSeconds = max(0, Int(end.timeIntervalSince(start)))
        }

        WorkoutActivityManager.shared.startActivity(
            workoutName: training.name,
            startTime: training.startTime ?? Date(),
            initialExercise: training.loggedExercises.first?.exerciseName ?? "Workout"
        )
    }

    public var formattedElapsed: String {
        let h = elapsedSeconds / 3600
        let m = (elapsedSeconds % 3600) / 60
        let s = elapsedSeconds % 60
        if h > 0 {
            return String(format: "%02d:%02d:%02d", h, m, s)
        } else {
            return String(format: "%02d:%02d", m, s)
        }
    }

    public var formattedRestTimer: String {
        let m = restTimerSecondsRemaining / 60
        let s = restTimerSecondsRemaining % 60
        return String(format: "%02d:%02d", m, s)
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

    public func skipRestTimer() {
        restTimerSecondsRemaining = 0
        isRestTimerActive = false
    }

    public func adjustRestTimer(by seconds: Int) {
        restTimerSecondsRemaining = max(0, restTimerSecondsRemaining + seconds)
        if restTimerSecondsRemaining == 0 {
            isRestTimerActive = false
        } else {
            isRestTimerActive = true
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

        // Start rest timer if marked completed
        if currentSet.isCompleted {
            self.restTimerSecondsRemaining = currentSet.restSeconds > 0 ? currentSet.restSeconds : 90
            self.isRestTimerActive = true
            Task {
                await WorkoutActivityManager.shared.updateActivity(
                    exerciseName: training.loggedExercises[exIndex].exerciseName,
                    currentSet: currentSet.setNumber,
                    totalSets: training.totalSets,
                    restRemaining: currentSet.restSeconds,
                    isRestActive: true
                )
            }
        } else {
            self.isRestTimerActive = false
            self.restTimerSecondsRemaining = 0
        }

        do {
            _ = try await trainingRepository.updateSet(trainingId: training.id, exerciseId: exerciseId, set: currentSet)
        } catch {
            self.errorMessage = "Failed to update set: \(error.localizedDescription)"
        }
    }

    public func updateSetValues(
        exerciseId: Int,
        setNumber: Int,
        weight: Double,
        reps: Int,
        rpe: Double? = nil,
        setType: SetType = .normal
    ) async {
        guard let exIndex = training.loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }),
              let setIndex = training.loggedExercises[exIndex].sets.firstIndex(where: { $0.setNumber == setNumber }) else {
            return
        }

        var currentSet = training.loggedExercises[exIndex].sets[setIndex]
        currentSet.weightKg = weight
        currentSet.repetitions = reps
        currentSet.rpe = rpe
        currentSet.setType = setType
        training.loggedExercises[exIndex].sets[setIndex] = currentSet

        do {
            _ = try await trainingRepository.updateSet(trainingId: training.id, exerciseId: exerciseId, set: currentSet)
        } catch {
            self.errorMessage = "Failed to update set values: \(error.localizedDescription)"
        }
    }

    public func addSet(to exerciseId: Int) async {
        guard let exIndex = training.loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }) else {
            return
        }

        let existingSets = training.loggedExercises[exIndex].sets
        let nextSetNum = existingSets.count + 1
        let lastWeight = existingSets.last?.weightKg ?? 20.0
        let lastReps = existingSets.last?.repetitions ?? 10
        let lastRest = existingSets.last?.restSeconds ?? 90

        let newSet = ResistanceSet(
            setNumber: nextSetNum,
            setType: .normal,
            weightKg: lastWeight,
            repetitions: lastReps,
            restSeconds: lastRest,
            isCompleted: false
        )

        training.loggedExercises[exIndex].sets.append(newSet)

        do {
            _ = try await trainingRepository.logSet(trainingId: training.id, exerciseId: exerciseId, set: newSet)
        } catch {
            self.errorMessage = "Failed to add set: \(error.localizedDescription)"
        }
    }

    public func deleteSet(exerciseId: Int, setNumber: Int) async {
        guard let exIndex = training.loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }) else {
            return
        }

        training.removeSet(exerciseId: exerciseId, setNumber: setNumber)

        do {
            try await trainingRepository.deleteSet(trainingId: training.id, exerciseId: exerciseId, setNumber: setNumber)
        } catch {
            self.errorMessage = "Failed to delete set: \(error.localizedDescription)"
        }
    }

    public func pauseWorkout() async {
        do {
            self.training = try await trainingRepository.pauseTraining(id: training.id)
            self.isTimerRunning = false
        } catch {
            self.errorMessage = "Failed to pause workout: \(error.localizedDescription)"
        }
    }

    public func resumeWorkout() async {
        do {
            self.training = try await trainingRepository.resumeTraining(id: training.id)
            self.isTimerRunning = true
        } catch {
            self.errorMessage = "Failed to resume workout: \(error.localizedDescription)"
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
            self.isRestTimerActive = false
            self.isFinished = true
            Task { await WorkoutActivityManager.shared.endActivity() }
        } catch {
            self.errorMessage = "Failed to finish workout: \(error.localizedDescription)"
        }
    }

    public func cancelWorkout() async {
        do {
            self.training = try await trainingRepository.cancelTraining(id: training.id)
            self.isTimerRunning = false
            self.isRestTimerActive = false
            self.isCancelled = true
            Task { await WorkoutActivityManager.shared.endActivity() }
        } catch {
            self.errorMessage = "Failed to cancel workout: \(error.localizedDescription)"
        }
    }
}
