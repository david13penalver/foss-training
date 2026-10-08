import Foundation
import SwiftData

@Model
public final class SDProgramWorkout {
    @Attribute(.unique) public var id: UUID
    public var dayOfWeek: Int
    public var focus: String?
    @Relationship public var session: SDSession?

    public init(
        id: UUID = UUID(),
        dayOfWeek: Int,
        focus: String? = nil,
        session: SDSession? = nil
    ) {
        self.id = id
        self.dayOfWeek = dayOfWeek
        self.focus = focus
        self.session = session
    }

    public func toDomain() -> ProgramWorkout {
        let domainSession = session?.toDomain() ?? Session(id: 0, name: "Workout")
        return ProgramWorkout(
            id: id,
            dayOfWeek: dayOfWeek,
            focus: focus,
            session: domainSession
        )
    }

    public static func fromDomain(_ workout: ProgramWorkout, sessionModel: SDSession? = nil) -> SDProgramWorkout {
        SDProgramWorkout(
            id: workout.id,
            dayOfWeek: workout.dayOfWeek,
            focus: workout.focus,
            session: sessionModel
        )
    }
}

@Model
public final class SDTrainingProgram {
    @Attribute(.unique) public var id: Int
    public var name: String
    public var programDescription: String?
    public var durationWeeks: Int
    public var periodizationTypeRaw: String
    public var levelRaw: String
    public var isActive: Bool
    @Relationship(deleteRule: .cascade) public var workouts: [SDProgramWorkout]

    public init(
        id: Int,
        name: String,
        programDescription: String? = nil,
        durationWeeks: Int = 4,
        periodizationType: PeriodizationType = .linear,
        level: ProgramLevel = .intermediate,
        workouts: [SDProgramWorkout] = [],
        isActive: Bool = true
    ) {
        self.id = id
        self.name = name
        self.programDescription = programDescription
        self.durationWeeks = durationWeeks
        self.periodizationTypeRaw = periodizationType.rawValue
        self.levelRaw = level.rawValue
        self.workouts = workouts
        self.isActive = isActive
    }

    public func toDomain() -> TrainingProgram {
        TrainingProgram(
            id: id,
            name: name,
            description: programDescription,
            durationWeeks: durationWeeks,
            periodizationType: PeriodizationType(rawValue: periodizationTypeRaw) ?? .linear,
            level: ProgramLevel(rawValue: levelRaw) ?? .intermediate,
            workouts: workouts.sorted { $0.dayOfWeek < $1.dayOfWeek }.map { $0.toDomain() },
            isActive: isActive
        )
    }

    public static func fromDomain(_ program: TrainingProgram, workoutModels: [SDProgramWorkout] = []) -> SDTrainingProgram {
        SDTrainingProgram(
            id: program.id,
            name: program.name,
            programDescription: program.description,
            durationWeeks: program.durationWeeks,
            periodizationType: program.periodizationType,
            level: program.level,
            workouts: workoutModels,
            isActive: program.isActive
        )
    }
}
