import SwiftUI

@Observable
@MainActor
public final class SessionListViewModel {
    private let sessionRepository: SessionRepository
    private let trainingRepository: TrainingRepository

    public var sessions: [Session] = []
    public var isLoading: Bool = false
    public var errorMessage: String?
    public var launchedTraining: Training?

    public init(sessionRepository: SessionRepository, trainingRepository: TrainingRepository) {
        self.sessionRepository = sessionRepository
        self.trainingRepository = trainingRepository
    }

    public func loadSessions() async {
        isLoading = true
        errorMessage = nil
        do {
            self.sessions = try await sessionRepository.getSessions()
        } catch {
            self.errorMessage = error.localizedDescription
        }
        isLoading = false
    }

    public func startWorkout(from session: Session) async {
        do {
            let training = try await trainingRepository.createTrainingFromSession(sessionId: session.id)
            let started = try await trainingRepository.startTraining(id: training.id)
            self.launchedTraining = started
        } catch {
            self.errorMessage = "Failed to launch workout: \(error.localizedDescription)"
        }
    }
}
