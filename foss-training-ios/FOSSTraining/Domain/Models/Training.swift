import Foundation

public enum TrainingStateError: LocalizedError, Equatable, Sendable {
    case invalidTransition(from: TrainingStatus, to: TrainingStatus)
    case invalidRpe(value: Double)
    case emptyWorkout

    public var errorDescription: String? {
        switch self {
        case .invalidTransition(let from, let to):
            return "Cannot transition training from '\(from.displayName)' to '\(to.displayName)'."
        case .invalidRpe(let value):
            return "Rate of Perceived Exertion (RPE) must be between 1.0 and 10.0 (received: \(value))."
        case .emptyWorkout:
            return "Training contains no logged exercises."
        }
    }
}

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

    // MARK: - State Machine Transitions

    public mutating func start(at date: Date = Date()) throws(TrainingStateError) {
        guard status.canStart && status != .inProgress else {
            throw .invalidTransition(from: status, to: .inProgress)
        }
        if status == .planned {
            self.startTime = date
        }
        self.status = .inProgress
    }

    public mutating func pause() throws(TrainingStateError) {
        guard status == .inProgress else {
            throw .invalidTransition(from: status, to: .paused)
        }
        self.status = .paused
    }

    public mutating func resume() throws(TrainingStateError) {
        guard status == .paused else {
            throw .invalidTransition(from: status, to: .inProgress)
        }
        self.status = .inProgress
    }

    public mutating func complete(
        rpe: Double? = nil,
        notes: String? = nil,
        at date: Date = Date()
    ) throws(TrainingStateError) {
        guard status.canComplete else {
            throw .invalidTransition(from: status, to: .completed)
        }
        if let rpe = rpe {
            guard rpe >= 1.0 && rpe <= 10.0 else {
                throw .invalidRpe(value: rpe)
            }
            self.overallRpe = rpe
        }
        if let notes = notes {
            self.notes = notes
        }
        self.status = .completed
        self.endTime = date
    }

    public mutating func cancel(at date: Date = Date()) throws(TrainingStateError) {
        guard status != .completed && status != .cancelled else {
            throw .invalidTransition(from: status, to: .cancelled)
        }
        self.status = .cancelled
        self.endTime = date
    }

    // MARK: - Aggregates & Metrics

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

    public var completionPercentage: Double {
        totalSets > 0 ? (Double(totalCompletedSets) / Double(totalSets)) * 100.0 : 0.0
    }

    public var durationSeconds: Int {
        guard let start = startTime else { return 0 }
        let end = endTime ?? Date()
        return max(0, Int(end.timeIntervalSince(start)))
    }

    public var formattedDuration: String {
        let totalSeconds = durationSeconds
        let hours = totalSeconds / 3600
        let minutes = (totalSeconds % 3600) / 60
        let seconds = totalSeconds % 60
        return String(format: "%02d:%02d:%02d", hours, minutes, seconds)
    }

    // MARK: - Set & Exercise Operations

    public mutating func toggleSet(exerciseId: Int, setNumber: Int) {
        guard let exIdx = loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }),
              let setIdx = loggedExercises[exIdx].sets.firstIndex(where: { $0.setNumber == setNumber }) else {
            return
        }
        loggedExercises[exIdx].sets[setIdx].isCompleted.toggle()
    }

    public mutating func updateSet(exerciseId: Int, set: ResistanceSet) {
        guard let exIdx = loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }),
              let setIdx = loggedExercises[exIdx].sets.firstIndex(where: { $0.setNumber == set.setNumber }) else {
            return
        }
        loggedExercises[exIdx].sets[setIdx] = set
    }

    public mutating func addSet(to exerciseId: Int, set: ResistanceSet) {
        guard let exIdx = loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }) else {
            return
        }
        loggedExercises[exIdx].sets.append(set)
    }

    public mutating func removeSet(exerciseId: Int, setNumber: Int) {
        guard let exIdx = loggedExercises.firstIndex(where: { $0.exerciseId == exerciseId }) else {
            return
        }
        var currentSets = loggedExercises[exIdx].sets
        currentSets.removeAll { $0.setNumber == setNumber }
        for (idx, var s) in currentSets.enumerated() {
            s.setNumber = idx + 1
            currentSets[idx] = s
        }
        loggedExercises[exIdx].sets = currentSets
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
