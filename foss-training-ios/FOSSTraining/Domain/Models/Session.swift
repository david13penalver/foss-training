import Foundation

public struct ResistanceSet: Identifiable, Codable, Hashable, Sendable {
    public var id: String { "\(setNumber)" }
    public var setNumber: Int
    public var setType: SetType
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
        self.setType = setType
        self.weightKg = weightKg
        self.repetitions = repetitions
        self.rpe = rpe
        self.restSeconds = restSeconds
        self.isCompleted = isCompleted
    }

    public var volumeKg: Double {
        weightKg * Double(repetitions)
    }
}

public struct SessionExerciseItem: Identifiable, Codable, Hashable, Sendable {
    public var id: String { "\(exerciseId)-\(orderIndex)" }
    public var orderIndex: Int
    public var exerciseId: Int
    public var exerciseName: String
    public var part: SessionPartEnum
    public var restSeconds: Int
    public var sets: [ResistanceSet]

    public init(
        orderIndex: Int,
        exerciseId: Int,
        exerciseName: String,
        part: SessionPartEnum = .main,
        restSeconds: Int = 90,
        sets: [ResistanceSet] = []
    ) {
        self.orderIndex = orderIndex
        self.exerciseId = exerciseId
        self.exerciseName = exerciseName
        self.part = part
        self.restSeconds = restSeconds
        self.sets = sets
    }
}

public struct Session: Identifiable, Codable, Hashable, Sendable {
    public let id: Int
    public var name: String
    public var description: String?
    public var notes: String?
    public var estimatedDurationMinutes: Int?
    public var exercises: [SessionExerciseItem]

    public init(
        id: Int,
        name: String,
        description: String? = nil,
        notes: String? = nil,
        estimatedDurationMinutes: Int? = 60,
        exercises: [SessionExerciseItem] = []
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.notes = notes
        self.estimatedDurationMinutes = estimatedDurationMinutes
        self.exercises = exercises
    }
}
