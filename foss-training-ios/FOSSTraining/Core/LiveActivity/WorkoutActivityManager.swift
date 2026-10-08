import Foundation
import ActivityKit

public final class WorkoutActivityManager: @unchecked Sendable {
    public static let shared = WorkoutActivityManager()

    public typealias ActivityStarter = @Sendable (WorkoutActivityAttributes, WorkoutActivityAttributes.ContentState) throws -> String
    public typealias ActivityUpdater = @Sendable (String, WorkoutActivityAttributes.ContentState) async -> Void
    public typealias ActivityEnder = @Sendable (String) async -> Void

    nonisolated(unsafe) public static var defaultActivityStarter: ActivityStarter = { attributes, content in
        let activity = try Activity<WorkoutActivityAttributes>.request(
            attributes: attributes,
            content: .init(state: content, staleDate: nil),
            pushType: nil
        )
        return activity.id
    }

    nonisolated(unsafe) public static var defaultActivityUpdater: ActivityUpdater = { activityId, content in
        for activity in Activity<WorkoutActivityAttributes>.activities where activity.id == activityId {
            await activity.update(.init(state: content, staleDate: nil))
        }
    }

    nonisolated(unsafe) public static var defaultActivityEnder: ActivityEnder = { activityId in
        for activity in Activity<WorkoutActivityAttributes>.activities where activity.id == activityId {
            await activity.end(nil, dismissalPolicy: .immediate)
        }
    }

    nonisolated(unsafe) public static var activityStarter: ActivityStarter = defaultActivityStarter
    nonisolated(unsafe) public static var activityUpdater: ActivityUpdater = defaultActivityUpdater
    nonisolated(unsafe) public static var activityEnder: ActivityEnder = defaultActivityEnder
    nonisolated(unsafe) public static var forceActivitiesEnabled: Bool?

    private let lock = NSLock()
    private var _activeActivityId: String?

    public var activeActivityId: String? {
        get {
            lock.lock()
            defer { lock.unlock() }
            return _activeActivityId
        }
        set {
            lock.lock()
            defer { lock.unlock() }
            _activeActivityId = newValue
        }
    }

    public init() {}

    public var areActivitiesEnabled: Bool {
        Self.forceActivitiesEnabled ?? ActivityAuthorizationInfo().areActivitiesEnabled
    }

    public func startActivity(workoutName: String, startTime: Date = Date(), initialExercise: String = "Warm-Up") {
        guard areActivitiesEnabled else { return }

        let attributes = WorkoutActivityAttributes(workoutName: workoutName, startTime: startTime)
        let initialContent = WorkoutActivityAttributes.ContentState(
            currentExerciseName: initialExercise,
            currentSetNumber: 1,
            totalSets: 1,
            restTimeRemaining: nil,
            isRestActive: false
        )

        do {
            self.activeActivityId = try Self.activityStarter(attributes, initialContent)
        } catch {
            self.activeActivityId = nil
        }
    }

    public func updateActivity(
        exerciseName: String,
        currentSet: Int,
        totalSets: Int,
        restRemaining: Int?,
        isRestActive: Bool
    ) async {
        guard let activityId = activeActivityId else {
            return
        }

        let updatedContent = WorkoutActivityAttributes.ContentState(
            currentExerciseName: exerciseName,
            currentSetNumber: currentSet,
            totalSets: totalSets,
            restTimeRemaining: restRemaining,
            isRestActive: isRestActive
        )

        await Self.activityUpdater(activityId, updatedContent)
    }

    public func endActivity() async {
        guard let activityId = activeActivityId else {
            self.activeActivityId = nil
            return
        }

        await Self.activityEnder(activityId)
        self.activeActivityId = nil
    }
}
