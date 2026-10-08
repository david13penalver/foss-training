import SwiftUI

public struct DayScheduleItem: Identifiable, Sendable {
    public var id: Int { dayOfWeek }
    public let dayOfWeek: Int
    public let dayName: String
    public let workout: ProgramWorkout?
    public var isRestDay: Bool { workout == nil }
}

@Observable
@MainActor
public final class ProgramDetailViewModel {
    private let programRepository: TrainingProgramRepository

    public var program: TrainingProgram
    public var adherence: ProgramAdherence? = nil
    public var isLoading: Bool = false
    public var isGenerating: Bool = false
    public var isScheduleSheetPresented: Bool = false
    public var errorMessage: String? = nil
    public var scheduleSuccessMessage: String? = nil

    public init(programRepository: TrainingProgramRepository, program: TrainingProgram) {
        self.programRepository = programRepository
        self.program = program
    }

    public func workoutForDay(_ dayOfWeek: Int) -> ProgramWorkout? {
        program.workouts.first { $0.dayOfWeek == dayOfWeek }
    }

    private static let dayNames = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]

    public var daysOfWeekSchedule: [DayScheduleItem] {
        (1...7).map { day in
            let workout = workoutForDay(day)
            let dayName = Self.dayNames[day - 1]
            return DayScheduleItem(dayOfWeek: day, dayName: dayName, workout: workout)
        }
    }

    public func loadDetails() async {
        isLoading = true
        errorMessage = nil
        do {
            if let fresh = try await programRepository.getProgram(id: program.id) {
                self.program = fresh
            }
            self.adherence = try? await programRepository.getProgramAdherence(id: program.id)
        } catch {
            self.errorMessage = error.localizedDescription
        }
        isLoading = false
    }

    public func generateSchedule(startDate: Date?) async {
        isGenerating = true
        errorMessage = nil
        scheduleSuccessMessage = nil
        do {
            let generated = try await programRepository.generateSchedule(programId: program.id, startDate: startDate)
            self.scheduleSuccessMessage = "Successfully generated \(generated.count) scheduled workouts."
            self.adherence = try? await programRepository.getProgramAdherence(id: program.id)
        } catch {
            self.errorMessage = "Failed to generate schedule: \(error.localizedDescription)"
        }
        isGenerating = false
    }
}
