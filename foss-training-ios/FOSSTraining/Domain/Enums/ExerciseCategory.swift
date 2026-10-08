import Foundation

public enum ExerciseCategory: String, Codable, CaseIterable, Identifiable, Sendable {
    case resistance = "RESISTANCE"
    case endurance = "ENDURANCE"
    case mobility = "MOBILITY"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .resistance: return "Resistance"
        case .endurance: return "Endurance"
        case .mobility: return "Mobility"
        }
    }

    public var systemIcon: String {
        switch self {
        case .resistance: return "dumbbell.fill"
        case .endurance: return "figure.run"
        case .mobility: return "figure.flexibility"
        }
    }
}

public enum DifficultyLevel: String, Codable, CaseIterable, Identifiable, Sendable {
    case beginner = "BEGINNER"
    case intermediate = "INTERMEDIATE"
    case advanced = "ADVANCED"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .beginner: return "Beginner"
        case .intermediate: return "Intermediate"
        case .advanced: return "Advanced"
        }
    }
}

public enum EquipmentCategory: String, Codable, CaseIterable, Identifiable, Sendable {
    case barbell = "BARBELL"
    case dumbbell = "DUMBBELL"
    case kettlebell = "KETTLEBELL"
    case machine = "MACHINE"
    case cable = "CABLE"
    case bodyweight = "BODYWEIGHT"
    case band = "BAND"
    case cardioMachine = "CARDIO_MACHINE"
    case other = "OTHER"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .barbell: return "Barbell"
        case .dumbbell: return "Dumbbell"
        case .kettlebell: return "Kettlebell"
        case .machine: return "Machine"
        case .cable: return "Cable"
        case .bodyweight: return "Bodyweight"
        case .band: return "Resistance Band"
        case .cardioMachine: return "Cardio Machine"
        case .other: return "Other"
        }
    }
}

public enum MovementPattern: String, Codable, CaseIterable, Identifiable, Sendable {
    case squat = "SQUAT"
    case hinge = "HINGE"
    case push = "PUSH"
    case pull = "PULL"
    case carry = "CARRY"
    case rotation = "ROTATION"
    case isolation = "ISOLATION"
    case other = "OTHER"

    public var id: String { rawValue }

    public var displayName: String {
        rawValue.capitalized
    }
}

public enum SetType: String, Codable, CaseIterable, Identifiable, Sendable {
    case normal = "NORMAL"
    case warmUp = "WARM_UP"
    case dropSet = "DROP_SET"
    case failure = "FAILURE"
    case restPause = "REST_PAUSE"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .normal: return "Work Set"
        case .warmUp: return "Warm-up"
        case .dropSet: return "Drop Set"
        case .failure: return "Failure"
        case .restPause: return "Rest Pause"
        }
    }

    public var shortTag: String {
        switch self {
        case .normal: return "W"
        case .warmUp: return "WU"
        case .dropSet: return "D"
        case .failure: return "F"
        case .restPause: return "RP"
        }
    }

    public var countsAsWorkingVolume: Bool {
        self != .warmUp
    }
}

public enum TrainingStatus: String, Codable, CaseIterable, Identifiable, Sendable {
    case planned = "PLANNED"
    case inProgress = "IN_PROGRESS"
    case paused = "PAUSED"
    case completed = "COMPLETED"
    case cancelled = "CANCELLED"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .planned: return "Scheduled"
        case .inProgress: return "In Progress"
        case .paused: return "Paused"
        case .completed: return "Completed"
        case .cancelled: return "Cancelled"
        }
    }

    public var canStart: Bool {
        self == .planned || self == .paused
    }

    public var canComplete: Bool {
        self == .inProgress || self == .paused
    }
}

public enum SessionPartEnum: String, Codable, CaseIterable, Identifiable, Sendable {
    case warmUp = "WARM_UP"
    case main = "MAIN"
    case coolDown = "COOLDOWN"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .warmUp: return "Warm-Up"
        case .main: return "Main Work"
        case .coolDown: return "Cool-Down"
        }
    }
}
