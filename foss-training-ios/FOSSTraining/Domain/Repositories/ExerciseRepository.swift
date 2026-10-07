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
}

public protocol AthleteRepository: Sendable {
    func getBodyweightHistory() async throws -> [BodyweightEntry]
    func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry
    func deleteBodyweight(id: Int) async throws
}

public struct BackupDataPayload: Codable, Sendable {
    public let exportVersion: String
    public let exportedAt: Date
    public let exercises: [Exercise]
    public let sessions: [Session]
    public let trainings: [Training]
    public let bodyweightEntries: [BodyweightEntry]

    public init(
        exportVersion: String = "1.0",
        exportedAt: Date = Date(),
        exercises: [Exercise],
        sessions: [Session],
        trainings: [Training],
        bodyweightEntries: [BodyweightEntry]
    ) {
        self.exportVersion = exportVersion
        self.exportedAt = exportedAt
        self.exercises = exercises
        self.sessions = sessions
        self.trainings = trainings
        self.bodyweightEntries = bodyweightEntries
    }
}

public protocol DataPortabilityRepository: Sendable {
    func exportFullBackup() async throws -> BackupDataPayload
    func importFullBackup(payload: BackupDataPayload) async throws -> Int
    func exportWorkoutsCSV() async throws -> String
}
