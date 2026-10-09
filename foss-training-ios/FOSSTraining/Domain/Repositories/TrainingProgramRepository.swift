import Foundation

public protocol TrainingProgramRepository: Sendable {
    func getPrograms() async throws -> [TrainingProgram]
    func getProgram(id: Int) async throws -> TrainingProgram?
    func saveProgram(_ program: TrainingProgram) async throws -> TrainingProgram
    func deleteProgram(id: Int) async throws
    func programExists(id: Int) async throws -> Bool
    func generateSchedule(programId: Int, startDate: Date?) async throws -> [Training]
    func cloneProgram(id: Int, newName: String?) async throws -> TrainingProgram
    func getProgramAdherence(id: Int) async throws -> ProgramAdherence
}

public extension TrainingProgramRepository {
    func programExists(id: Int) async throws -> Bool {
        try await getProgram(id: id) != nil
    }
}
