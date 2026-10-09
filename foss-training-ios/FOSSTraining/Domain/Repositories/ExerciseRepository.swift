import Foundation

public protocol ExerciseRepository: Sendable {
    func getExercises(category: ExerciseCategory?, search: String?) async throws -> [Exercise]
    func getExercise(id: Int) async throws -> Exercise?
    func saveExercise(_ exercise: Exercise) async throws -> Exercise
    func deleteExercise(id: Int) async throws
}

public protocol SessionRepository: Sendable {
    func getSessions() async throws -> [Session]
    func getSession(id: Int) async throws -> Session?
    func saveSession(_ session: Session) async throws -> Session
    func deleteSession(id: Int) async throws
    func cloneSession(id: Int) async throws -> Session
    func sessionExists(id: Int) async throws -> Bool
}

public extension SessionRepository {
    func sessionExists(id: Int) async throws -> Bool {
        try await getSession(id: id) != nil
    }
}

public protocol TrainingRepository: Sendable {
    func getTrainings() async throws -> [Training]
    func getTraining(id: Int) async throws -> Training?
    func createTrainingFromSession(sessionId: Int) async throws -> Training
    func startTraining(id: Int) async throws -> Training
    func pauseTraining(id: Int) async throws -> Training
    func resumeTraining(id: Int) async throws -> Training
    func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training
    func cancelTraining(id: Int) async throws -> Training
    func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet
    func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet
    func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws
    func trainingExists(id: Int) async throws -> Bool
}

extension TrainingRepository {
    public func trainingExists(id: Int) async throws -> Bool {
        try await getTraining(id: id) != nil
    }
}

