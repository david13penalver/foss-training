import Foundation
import ActivityKit

public struct WorkoutActivityAttributes: ActivityAttributes, Sendable {
    public struct ContentState: Codable, Hashable, Sendable {
        public var currentExerciseName: String
        public var currentSetNumber: Int
        public var totalSets: Int
        public var restTimeRemaining: Int?
        public var isRestActive: Bool

        public init(
            currentExerciseName: String,
            currentSetNumber: Int,
            totalSets: Int,
            restTimeRemaining: Int? = nil,
            isRestActive: Bool = false
        ) {
            self.currentExerciseName = currentExerciseName
            self.currentSetNumber = currentSetNumber
            self.totalSets = totalSets
            self.restTimeRemaining = restTimeRemaining
            self.isRestActive = isRestActive
        }
    }

    public var workoutName: String
    public var startTime: Date

    public init(workoutName: String, startTime: Date = Date()) {
        self.workoutName = workoutName
        self.startTime = startTime
    }
}
