import Foundation

public enum PeriodizationType: String, Codable, CaseIterable, Sendable {
    case linear = "LINEAR"
    case undulating = "UNDULATING"
    case block = "BLOCK"
    case reverseLinear = "REVERSE_LINEAR"
    case conjugated = "CONJUGATED"

    public var displayName: String {
        switch self {
        case .linear: return "Linear Periodization"
        case .undulating: return "Daily Undulating (DUP)"
        case .block: return "Block Periodization"
        case .reverseLinear: return "Reverse Linear"
        case .conjugated: return "Conjugated / Westside"
        }
    }
}

public enum ProgramLevel: String, Codable, CaseIterable, Identifiable, Sendable {
    case beginner = "BEGINNER"
    case intermediate = "INTERMEDIATE"
    case advanced = "ADVANCED"
    case elite = "ELITE"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .beginner: return "Beginner"
        case .intermediate: return "Intermediate"
        case .advanced: return "Advanced"
        case .elite: return "Elite"
        }
    }
}

public enum ProgramValidationError: LocalizedError, Equatable {
    case nameTooShort
    case nameTooLong
    case invalidDurationWeeks
    case invalidDayOfWeek
    case noWorkoutsConfigured

    public var errorDescription: String? {
        switch self {
        case .nameTooShort:
            return "Program name must be at least 2 characters long."
        case .nameTooLong:
            return "Program name must not exceed 100 characters."
        case .invalidDurationWeeks:
            return "Program duration must be between 1 and 52 weeks."
        case .invalidDayOfWeek:
            return "Workout day must be between 1 (Monday) and 7 (Sunday)."
        case .noWorkoutsConfigured:
            return "Program must contain at least one workout day."
        }
    }
}

public struct ProgramWorkout: Identifiable, Codable, Hashable, Sendable {
    public var id: UUID
    public var dayOfWeek: Int
    public var focus: String?
    public var session: Session

    public init(
        id: UUID = UUID(),
        dayOfWeek: Int,
        focus: String? = nil,
        session: Session
    ) {
        self.id = id
        self.dayOfWeek = dayOfWeek
        self.focus = focus
        self.session = session
    }

    public var dayName: String {
        switch dayOfWeek {
        case 1: return "Monday"
        case 2: return "Tuesday"
        case 3: return "Wednesday"
        case 4: return "Thursday"
        case 5: return "Friday"
        case 6: return "Saturday"
        case 7: return "Sunday"
        default: return "Day \(dayOfWeek)"
        }
    }

    public func validate() throws {
        guard dayOfWeek >= 1 && dayOfWeek <= 7 else {
            throw ProgramValidationError.invalidDayOfWeek
        }
    }
}

public struct TrainingProgram: Identifiable, Codable, Hashable, Sendable {
    public let id: Int
    public var name: String
    public var description: String?
    public var durationWeeks: Int
    public var periodizationType: PeriodizationType
    public var level: ProgramLevel
    public var workouts: [ProgramWorkout]
    public var isActive: Bool

    public init(
        id: Int,
        name: String,
        description: String? = nil,
        durationWeeks: Int = 4,
        periodizationType: PeriodizationType = .linear,
        level: ProgramLevel = .intermediate,
        workouts: [ProgramWorkout] = [],
        isActive: Bool = true
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.durationWeeks = durationWeeks
        self.periodizationType = periodizationType
        self.level = level
        self.workouts = workouts
        self.isActive = isActive
    }

    public var totalWorkoutsPerCycle: Int {
        durationWeeks * workouts.count
    }

    public func validate() throws {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard trimmed.count >= 2 else {
            throw ProgramValidationError.nameTooShort
        }
        guard trimmed.count <= 100 else {
            throw ProgramValidationError.nameTooLong
        }
        guard durationWeeks >= 1 && durationWeeks <= 52 else {
            throw ProgramValidationError.invalidDurationWeeks
        }
        for workout in workouts {
            try workout.validate()
        }
    }

    public func generateSchedule(startDate: Date? = nil) -> [Training] {
        let calendar = Calendar.current
        let baseDate = startDate ?? Date()
        var result: [Training] = []

        for week in 0..<durationWeeks {
            for workout in workouts {
                let dayOffset = workout.dayOfWeek - 1
                var dateComponents = DateComponents()
                dateComponents.day = (week * 7) + dayOffset
                let scheduledDate = calendar.date(byAdding: dateComponents, to: baseDate)!

                let sessionName = !workout.session.name.trimmingCharacters(in: .whitespaces).isEmpty
                    ? workout.session.name
                    : "Workout"
                let workoutTitle = "\(name) - W\(week + 1)D\(workout.dayOfWeek): \(sessionName)"

                let training = Training(
                    id: 0,
                    name: workoutTitle,
                    description: workout.focus,
                    trainingDate: scheduledDate,
                    status: .planned,
                    programId: id,
                    loggedExercises: workout.session.exercises
                )
                result.append(training)
            }
        }

        return result
    }
}
