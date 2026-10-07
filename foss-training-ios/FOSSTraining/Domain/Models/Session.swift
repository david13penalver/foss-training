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

    public var totalVolumeKg: Double {
        sets.reduce(0.0) { $0 + $1.volumeKg }
    }
}

public enum SessionValidationError: LocalizedError, Equatable, Sendable {
    case nameTooShort
    case nameTooLong
    case invalidDuration
    case noExercises
    case missingSets(exerciseName: String)

    public var errorDescription: String? {
        switch self {
        case .nameTooShort:
            return "Session name must be at least 2 characters."
        case .nameTooLong:
            return "Session name must be at most 100 characters."
        case .invalidDuration:
            return "Estimated duration must be greater than zero."
        case .noExercises:
            return "Session must contain at least one exercise."
        case .missingSets(let exerciseName):
            return "Exercise '\(exerciseName)' must contain at least one set."
        }
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

    public func validate() throws(SessionValidationError) {
        let trimmedName = name.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmedName.count < 2 {
            throw .nameTooShort
        }
        if trimmedName.count > 100 {
            throw .nameTooLong
        }
        if let duration = estimatedDurationMinutes, duration <= 0 {
            throw .invalidDuration
        }
        if exercises.isEmpty {
            throw .noExercises
        }
        for ex in exercises {
            if ex.sets.isEmpty {
                throw .missingSets(exerciseName: ex.exerciseName)
            }
        }
    }

    public var warmUpExercises: [SessionExerciseItem] {
        exercises.filter { $0.part == .warmUp }.sorted { $0.orderIndex < $1.orderIndex }
    }

    public var mainExercises: [SessionExerciseItem] {
        exercises.filter { $0.part == .main }.sorted { $0.orderIndex < $1.orderIndex }
    }

    public var coolDownExercises: [SessionExerciseItem] {
        exercises.filter { $0.part == .coolDown }.sorted { $0.orderIndex < $1.orderIndex }
    }

    public var totalSetsCount: Int {
        exercises.reduce(0) { $0 + $1.sets.count }
    }
}
