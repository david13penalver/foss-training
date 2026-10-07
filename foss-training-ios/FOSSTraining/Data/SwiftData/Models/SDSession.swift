import Foundation
import SwiftData

@Model
public final class SDResistanceSet {
    public var setNumber: Int
    public var setTypeRaw: String
    public var weightKg: Double
    public var repetitions: Int
    public var rpe: Double?
    public var restSeconds: Int
    public var isCompleted: Bool

    public init(
        setNumber: Int,
        setType: SetType = .normal,
        weightKg: Double = 0.0,
        repetitions: Int = 0,
        rpe: Double? = nil,
        restSeconds: Int = 90,
        isCompleted: Bool = false
    ) {
        self.setNumber = setNumber
        self.setTypeRaw = setType.rawValue
        self.weightKg = weightKg
        self.repetitions = repetitions
        self.rpe = rpe
        self.restSeconds = restSeconds
        self.isCompleted = isCompleted
    }

    public func toDomain() -> ResistanceSet {
        ResistanceSet(
            setNumber: setNumber,
            setType: SetType(rawValue: setTypeRaw) ?? .normal,
            weightKg: weightKg,
            repetitions: repetitions,
            rpe: rpe,
            restSeconds: restSeconds,
            isCompleted: isCompleted
        )
    }

    public static func fromDomain(_ set: ResistanceSet) -> SDResistanceSet {
        SDResistanceSet(
            setNumber: set.setNumber,
            setType: set.setType,
            weightKg: set.weightKg,
            repetitions: set.repetitions,
            rpe: set.rpe,
            restSeconds: set.restSeconds,
            isCompleted: set.isCompleted
        )
    }
}

@Model
public final class SDSessionExercise {
    public var orderIndex: Int
    public var exerciseId: Int
    public var exerciseName: String
    public var partRaw: String
    public var restSeconds: Int
    @Relationship(deleteRule: .cascade) public var sets: [SDResistanceSet]

    public init(
        orderIndex: Int,
        exerciseId: Int,
        exerciseName: String,
        part: SessionPartEnum = .main,
        restSeconds: Int = 90,
        sets: [SDResistanceSet] = []
    ) {
        self.orderIndex = orderIndex
        self.exerciseId = exerciseId
        self.exerciseName = exerciseName
        self.partRaw = part.rawValue
        self.restSeconds = restSeconds
        self.sets = sets
    }

    public func toDomain() -> SessionExerciseItem {
        SessionExerciseItem(
            orderIndex: orderIndex,
            exerciseId: exerciseId,
            exerciseName: exerciseName,
            part: SessionPartEnum(rawValue: partRaw) ?? .main,
            restSeconds: restSeconds,
            sets: sets.map { $0.toDomain() }
        )
    }

    public static func fromDomain(_ item: SessionExerciseItem) -> SDSessionExercise {
        SDSessionExercise(
            orderIndex: item.orderIndex,
            exerciseId: item.exerciseId,
            exerciseName: item.exerciseName,
            part: item.part,
            restSeconds: item.restSeconds,
            sets: item.sets.map { SDResistanceSet.fromDomain($0) }
        )
    }
}

@Model
public final class SDSession {
    @Attribute(.unique) public var id: Int
    public var name: String
    public var sessionDescription: String?
    public var notes: String?
    public var estimatedDurationMinutes: Int?
    @Relationship(deleteRule: .cascade) public var exercises: [SDSessionExercise]

    public init(
        id: Int,
        name: String,
        sessionDescription: String? = nil,
        notes: String? = nil,
        estimatedDurationMinutes: Int? = 60,
        exercises: [SDSessionExercise] = []
    ) {
        self.id = id
        self.name = name
        self.sessionDescription = sessionDescription
        self.notes = notes
        self.estimatedDurationMinutes = estimatedDurationMinutes
        self.exercises = exercises
    }

    public func toDomain() -> Session {
        Session(
            id: id,
            name: name,
            description: sessionDescription,
            notes: notes,
            estimatedDurationMinutes: estimatedDurationMinutes,
            exercises: exercises.map { $0.toDomain() }
        )
    }

    public static func fromDomain(_ session: Session) -> SDSession {
        SDSession(
            id: session.id,
            name: session.name,
            sessionDescription: session.description,
            notes: session.notes,
            estimatedDurationMinutes: session.estimatedDurationMinutes,
            exercises: session.exercises.map { SDSessionExercise.fromDomain($0) }
        )
    }
}
