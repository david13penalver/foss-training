import SwiftUI

@Observable
@MainActor
public final class ProgramEditorViewModel {
    private let programRepository: TrainingProgramRepository
    private let sessionRepository: SessionRepository

    public let existingProgramId: Int?
    public var name: String = ""
    public var descriptionText: String = ""
    public var durationWeeks: Int = 4
    public var periodizationType: PeriodizationType = .linear
    public var level: ProgramLevel = .intermediate
    public var isActive: Bool = true
    public var workouts: [ProgramWorkout] = []

    public var availableSessions: [Session] = []
    public var isSaving: Bool = false
    public var errorMessage: String? = nil

    public init(
        programRepository: TrainingProgramRepository,
        sessionRepository: SessionRepository,
        programToEdit: TrainingProgram? = nil
    ) {
        self.programRepository = programRepository
        self.sessionRepository = sessionRepository

        if let program = programToEdit {
            self.existingProgramId = program.id
            self.name = program.name
            self.descriptionText = program.description ?? ""
            self.durationWeeks = program.durationWeeks
            self.periodizationType = program.periodizationType
            self.level = program.level
            self.isActive = program.isActive
            self.workouts = program.workouts
        } else {
            self.existingProgramId = nil
        }
    }

    public var isNewProgram: Bool {
        existingProgramId == nil
    }

    public var isValid: Bool {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard trimmed.count >= 2, trimmed.count <= 100 else { return false }
        guard durationWeeks >= 1, durationWeeks <= 52 else { return false }
        guard !workouts.isEmpty else { return false }
        return true
    }

    public func loadSessions() async {
        do {
            self.availableSessions = try await sessionRepository.getSessions()
        } catch {
            self.errorMessage = "Failed to load sessions: \(error.localizedDescription)"
        }
    }

    public func addWorkout(dayOfWeek: Int, session: Session, focus: String? = nil) {
        let workout = ProgramWorkout(
            id: UUID(),
            dayOfWeek: dayOfWeek,
            focus: focus,
            session: session
        )
        workouts.append(workout)
        workouts.sort { $0.dayOfWeek < $1.dayOfWeek }
    }

    public func removeWorkout(id: UUID) {
        workouts.removeAll { $0.id == id }
    }

    public func updateWorkout(id: UUID, dayOfWeek: Int, session: Session, focus: String?) {
        if let idx = workouts.firstIndex(where: { $0.id == id }) {
            workouts[idx] = ProgramWorkout(
                id: id,
                dayOfWeek: dayOfWeek,
                focus: focus,
                session: session
            )
            workouts.sort { $0.dayOfWeek < $1.dayOfWeek }
        }
    }

    public func saveProgram() async -> Bool {
        guard isValid else {
            errorMessage = "Please verify that the program name is at least 2 characters, duration is 1-52 weeks, and at least one workout day is configured."
            return false
        }

        isSaving = true
        errorMessage = nil
        do {
            let targetId: Int
            if let existingProgramId {
                targetId = existingProgramId
            } else {
                targetId = 0
            }
            let program = TrainingProgram(
                id: targetId,
                name: name.trimmingCharacters(in: .whitespacesAndNewlines),
                description: descriptionText.isEmpty ? nil : descriptionText.trimmingCharacters(in: .whitespacesAndNewlines),
                durationWeeks: durationWeeks,
                periodizationType: periodizationType,
                level: level,
                workouts: workouts,
                isActive: isActive
            )
            _ = try await programRepository.saveProgram(program)
            isSaving = false
            return true
        } catch {
            self.errorMessage = "Failed to save program: \(error.localizedDescription)"
            isSaving = false
            return false
        }
    }
}
