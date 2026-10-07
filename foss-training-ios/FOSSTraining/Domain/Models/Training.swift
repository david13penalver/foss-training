import Foundation

public struct Training: Identifiable, Codable, Hashable, Sendable {
    public let id: Int
    public var name: String
    public var description: String?
    public var trainingDate: Date
    public var startTime: Date?
    public var endTime: Date?
    public var status: TrainingStatus
    public var notes: String?
    public var overallRpe: Double?
    public var programId: Int?
    public var loggedExercises: [SessionExerciseItem]

    public init(
        id: Int,
        name: String,
        description: String? = nil,
        trainingDate: Date = Date(),
        startTime: Date? = nil,
        endTime: Date? = nil,
        status: TrainingStatus = .planned,
        notes: String? = nil,
        overallRpe: Double? = nil,
        programId: Int? = nil,
        loggedExercises: [SessionExerciseItem] = []
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.trainingDate = trainingDate
        self.startTime = startTime
        self.endTime = endTime
        self.status = status
        self.notes = notes
        self.overallRpe = overallRpe
        self.programId = programId
        self.loggedExercises = loggedExercises
    }

    public var totalCompletedSets: Int {
        loggedExercises.reduce(0) { total, ex in
            total + ex.sets.filter(\.isCompleted).count
        }
    }

    public var totalSets: Int {
        loggedExercises.reduce(0) { total, ex in
            total + ex.sets.count
        }
    }

    public var totalVolumeKg: Double {
        loggedExercises.reduce(0.0) { total, ex in
            total + ex.sets.filter(\.isCompleted).reduce(0.0) { setTotal, s in
                setTotal + s.volumeKg
            }
        }
    }
}

public struct BodyweightEntry: Identifiable, Codable, Hashable, Sendable {
    public let id: Int
    public var weightKg: Double
    public var measuredDate: Date
    public var notes: String?

    public init(id: Int, weightKg: Double, measuredDate: Date = Date(), notes: String? = nil) {
        self.id = id
        self.weightKg = weightKg
        self.measuredDate = measuredDate
        self.notes = notes
    }
}
