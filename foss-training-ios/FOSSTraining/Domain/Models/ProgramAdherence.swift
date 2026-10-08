import Foundation

public enum ProgramAdherenceStatus: String, Codable, CaseIterable, Sendable {
    case notStarted = "NOT_STARTED"
    case onTrack = "ON_TRACK"
    case behindSchedule = "BEHIND_SCHEDULE"
    case atRisk = "AT_RISK"
    case completed = "COMPLETED"

    public var displayName: String {
        switch self {
        case .notStarted: return "Not Started"
        case .onTrack: return "On Track"
        case .behindSchedule: return "Behind Schedule"
        case .atRisk: return "At Risk"
        case .completed: return "Completed"
        }
    }
}

public struct WeeklyAdherence: Identifiable, Codable, Hashable, Sendable {
    public var id: Int { weekNumber }
    public let weekNumber: Int
    public let weekStartDate: Date?
    public let weekEndDate: Date?
    public let scheduledWorkouts: Int
    public let completedWorkouts: Int
    public let missedWorkouts: Int
    public let adherenceRate: Double
    public let completed: Bool

    public init(
        weekNumber: Int,
        weekStartDate: Date? = nil,
        weekEndDate: Date? = nil,
        scheduledWorkouts: Int,
        completedWorkouts: Int,
        missedWorkouts: Int,
        adherenceRate: Double,
        completed: Bool
    ) {
        self.weekNumber = weekNumber
        self.weekStartDate = weekStartDate
        self.weekEndDate = weekEndDate
        self.scheduledWorkouts = scheduledWorkouts
        self.completedWorkouts = completedWorkouts
        self.missedWorkouts = missedWorkouts
        self.adherenceRate = adherenceRate
        self.completed = completed
    }
}

public struct WorkoutAdherenceItem: Identifiable, Codable, Hashable, Sendable {
    public var id: Int { trainingId }
    public let trainingId: Int
    public let workoutName: String
    public let scheduledDate: Date?
    public let completedDate: Date?
    public let status: TrainingStatus
    public let sessionRpe: Double?
    public let volumeKg: Double
    public let onTime: Bool

    public init(
        trainingId: Int,
        workoutName: String,
        scheduledDate: Date? = nil,
        completedDate: Date? = nil,
        status: TrainingStatus = .planned,
        sessionRpe: Double? = nil,
        volumeKg: Double = 0.0,
        onTime: Bool = false
    ) {
        self.trainingId = trainingId
        self.workoutName = workoutName
        self.scheduledDate = scheduledDate
        self.completedDate = completedDate
        self.status = status
        self.sessionRpe = sessionRpe
        self.volumeKg = volumeKg
        self.onTime = onTime
    }
}

public struct ProgramAdherence: Codable, Hashable, Sendable {
    public let programId: Int
    public let programName: String
    public let durationWeeks: Int
    public let totalScheduledWorkouts: Int
    public let completedWorkouts: Int
    public let inProgressWorkouts: Int
    public let plannedWorkouts: Int
    public let missedWorkouts: Int
    public let cancelledWorkouts: Int
    public let overallCompletionRate: Double
    public let currentAdherenceRate: Double
    public let currentStreak: Int
    public let longestStreak: Int
    public let status: ProgramAdherenceStatus
    public let statusDescription: String
    public let weeklyBreakdowns: [WeeklyAdherence]
    public let workoutDetails: [WorkoutAdherenceItem]

    public init(
        programId: Int,
        programName: String,
        durationWeeks: Int,
        totalScheduledWorkouts: Int,
        completedWorkouts: Int,
        inProgressWorkouts: Int,
        plannedWorkouts: Int,
        missedWorkouts: Int,
        cancelledWorkouts: Int,
        overallCompletionRate: Double,
        currentAdherenceRate: Double,
        currentStreak: Int,
        longestStreak: Int,
        status: ProgramAdherenceStatus,
        statusDescription: String,
        weeklyBreakdowns: [WeeklyAdherence] = [],
        workoutDetails: [WorkoutAdherenceItem] = []
    ) {
        self.programId = programId
        self.programName = programName
        self.durationWeeks = durationWeeks
        self.totalScheduledWorkouts = totalScheduledWorkouts
        self.completedWorkouts = completedWorkouts
        self.inProgressWorkouts = inProgressWorkouts
        self.plannedWorkouts = plannedWorkouts
        self.missedWorkouts = missedWorkouts
        self.cancelledWorkouts = cancelledWorkouts
        self.overallCompletionRate = overallCompletionRate
        self.currentAdherenceRate = currentAdherenceRate
        self.currentStreak = currentStreak
        self.longestStreak = longestStreak
        self.status = status
        self.statusDescription = statusDescription
        self.weeklyBreakdowns = weeklyBreakdowns
        self.workoutDetails = workoutDetails
    }
}

public enum ProgramAdherenceCalculator {

    public static func calculate(
        program: TrainingProgram,
        trainings: [Training],
        referenceDate: Date = Date()
    ) -> ProgramAdherence {
        let calendar = Calendar.current
        let workoutsPerWeek = program.workouts.count
        let expectedTotal = program.durationWeeks * workoutsPerWeek

        if trainings.isEmpty {
            return ProgramAdherence(
                programId: program.id,
                programName: program.name,
                durationWeeks: program.durationWeeks,
                totalScheduledWorkouts: expectedTotal,
                completedWorkouts: 0,
                inProgressWorkouts: 0,
                plannedWorkouts: expectedTotal,
                missedWorkouts: 0,
                cancelledWorkouts: 0,
                overallCompletionRate: 0.0,
                currentAdherenceRate: 0.0,
                currentStreak: 0,
                longestStreak: 0,
                status: .notStarted,
                statusDescription: ProgramAdherenceStatus.notStarted.displayName,
                weeklyBreakdowns: [],
                workoutDetails: []
            )
        }

        let sortedTrainings = trainings.sorted { $0.trainingDate < $1.trainingDate }

        var completedCount = 0
        var inProgressCount = 0
        var plannedCount = 0
        var missedCount = 0
        var cancelledCount = 0

        var details: [WorkoutAdherenceItem] = []

        for t in sortedTrainings {
            let status = t.status
            let schedDate = t.trainingDate

            let isCompleted = (status == .completed)
            let isInProgress = (status == .inProgress || status == .paused)
            let isCancelled = (status == .cancelled)
            let isOverduePlanned = (status == .planned && schedDate < referenceDate)

            var completedDate: Date? = nil
            var onTime = false
            if isCompleted {
                let compDate = t.endTime ?? t.startTime ?? schedDate
                completedDate = compDate
                completedCount += 1
                let deadline = calendar.date(byAdding: .day, value: 1, to: schedDate)!
                onTime = compDate <= deadline
            } else if isInProgress {
                inProgressCount += 1
            } else if isCancelled {
                cancelledCount += 1
            } else if isOverduePlanned {
                missedCount += 1
            } else {
                plannedCount += 1
            }

            details.append(
                WorkoutAdherenceItem(
                    trainingId: t.id,
                    workoutName: t.name,
                    scheduledDate: schedDate,
                    completedDate: completedDate,
                    status: status,
                    sessionRpe: t.overallRpe,
                    volumeKg: t.totalVolumeKg,
                    onTime: onTime
                )
            )
        }

        let totalScheduled = max(sortedTrainings.count, expectedTotal)
        let overallCompletionRate = roundToOneDecimal((Double(completedCount) * 100.0) / Double(totalScheduled))

        let expectedElapsed = completedCount + missedCount + cancelledCount
        let currentAdherenceRate = expectedElapsed > 0
            ? roundToOneDecimal((Double(completedCount) * 100.0) / Double(expectedElapsed))
            : 0.0

        // Streak calculation
        let elapsedTrainings = sortedTrainings.filter { t in
            let s = t.status
            let isDone = (s == .completed)
            let isMissedOrCancelled = (s == .cancelled || (s == .planned && t.trainingDate < referenceDate))
            return isDone || isMissedOrCancelled
        }

        var longestStreak = 0
        var tempStreak = 0

        for t in elapsedTrainings {
            if t.status == .completed {
                tempStreak += 1
                if tempStreak > longestStreak {
                    longestStreak = tempStreak
                }
            } else {
                tempStreak = 0
            }
        }

        var currentStreak = 0
        for t in elapsedTrainings.reversed() {
            if t.status == .completed {
                currentStreak += 1
            } else {
                break
            }
        }

        // Status determination
        let adherenceStatus: ProgramAdherenceStatus
        if completedCount >= totalScheduled {
            adherenceStatus = .completed
        } else if expectedElapsed == 0 && inProgressCount == 0 {
            adherenceStatus = .notStarted
        } else if currentAdherenceRate >= 80.0 {
            adherenceStatus = .onTrack
        } else if currentAdherenceRate >= 50.0 {
            adherenceStatus = .behindSchedule
        } else {
            adherenceStatus = .atRisk
        }

        let weeklyBreakdowns = calculateWeeklyBreakdowns(
            program: program,
            sortedTrainings: sortedTrainings,
            referenceDate: referenceDate
        )

        return ProgramAdherence(
            programId: program.id,
            programName: program.name,
            durationWeeks: program.durationWeeks,
            totalScheduledWorkouts: totalScheduled,
            completedWorkouts: completedCount,
            inProgressWorkouts: inProgressCount,
            plannedWorkouts: plannedCount,
            missedWorkouts: missedCount,
            cancelledWorkouts: cancelledCount,
            overallCompletionRate: overallCompletionRate,
            currentAdherenceRate: currentAdherenceRate,
            currentStreak: currentStreak,
            longestStreak: longestStreak,
            status: adherenceStatus,
            statusDescription: adherenceStatus.displayName,
            weeklyBreakdowns: weeklyBreakdowns,
            workoutDetails: details
        )
    }

    private static func calculateWeeklyBreakdowns(
        program: TrainingProgram,
        sortedTrainings: [Training],
        referenceDate: Date
    ) -> [WeeklyAdherence] {
        guard program.durationWeeks > 0 else { return [] }

        let calendar = Calendar.current
        let baseDate = sortedTrainings[0].trainingDate

        var weeks: [WeeklyAdherence] = []

        for w in 1...program.durationWeeks {
            let weekStart = calendar.date(byAdding: .day, value: (w - 1) * 7, to: baseDate)!
            let weekEnd = calendar.date(byAdding: .day, value: 6, to: weekStart)!

            let weekTrainings = sortedTrainings.filter { t in
                t.trainingDate >= weekStart && t.trainingDate <= calendar.date(byAdding: .day, value: 1, to: weekEnd)!
            }

            let scheduled = weekTrainings.count
            var completed = 0
            var missed = 0

            for t in weekTrainings {
                if t.status == .completed {
                    completed += 1
                } else if t.status == .cancelled || (t.status == .planned && t.trainingDate < referenceDate) {
                    missed += 1
                }
            }

            let rate = scheduled > 0 ? roundToOneDecimal((Double(completed) * 100.0) / Double(scheduled)) : 0.0
            let weekDone = scheduled > 0 && completed == scheduled

            weeks.append(
                WeeklyAdherence(
                    weekNumber: w,
                    weekStartDate: weekStart,
                    weekEndDate: weekEnd,
                    scheduledWorkouts: scheduled,
                    completedWorkouts: completed,
                    missedWorkouts: missed,
                    adherenceRate: rate,
                    completed: weekDone
                )
            )
        }

        return weeks
    }

    private static func roundToOneDecimal(_ value: Double) -> Double {
        (value * 10.0).rounded() / 10.0
    }
}
