import Foundation

public enum AcwrCalculator {
    public static let acuteDays = 7
    public static let chronicDays = 28
    public static let defaultModerateRpe = 6.0
    public static let defaultSessionMinutes = 45.0

    public static func calculate(trainings: [Training], targetDate: Date = Date()) -> WorkloadRatio {
        let calendar = Calendar.current
        let effectiveTarget = calendar.startOfDay(for: targetDate)

        // 1. Initialize 28-day timeline
        var dailyMap: [Date: DailyWorkload] = [:]
        var timelineDates: [Date] = []

        for i in (0..<chronicDays).reversed() {
            if let day = calendar.date(byAdding: .day, value: -i, to: effectiveTarget) {
                let startDay = calendar.startOfDay(for: day)
                timelineDates.append(startDay)
                dailyMap[startDay] = DailyWorkload(date: startDay, workloadAu: 0.0, totalVolumeKg: 0.0, completedSessions: 0)
            }
        }

        // 2. Aggregate completed trainings
        for tr in trainings where tr.status == .completed {
            let trDate = calendar.startOfDay(for: tr.trainingDate)
            if var daily = dailyMap[trDate] {
                let sessionLoad = calculateSessionLoad(training: tr)
                let sessionVolume = calculateTrainingVolume(training: tr)

                daily.workloadAu = ((daily.workloadAu + sessionLoad) * 10.0).rounded() / 10.0
                daily.totalVolumeKg = ((daily.totalVolumeKg + sessionVolume) * 10.0).rounded() / 10.0
                daily.completedSessions += 1
                dailyMap[trDate] = daily
            }
        }

        // 3. Compute Acute (7-day) and Chronic (28-day) metrics
        let acuteStartDate = calendar.date(byAdding: .day, value: -(acuteDays - 1), to: effectiveTarget)!
        var acuteSum = 0.0
        var chronicSum = 0.0

        let orderedWorkloads = timelineDates.compactMap { dailyMap[$0] }
        for daily in orderedWorkloads {
            chronicSum += daily.workloadAu
            if daily.date >= acuteStartDate {
                acuteSum += daily.workloadAu
            }
        }

        let acuteWorkload = (acuteSum * 10.0).rounded() / 10.0
        let acuteDailyAverage = ((acuteWorkload / Double(acuteDays)) * 100.0).rounded() / 100.0
        let chronicWorkload = (chronicSum * 10.0).rounded() / 10.0
        let chronicWeeklyAverage = ((chronicWorkload / 4.0) * 100.0).rounded() / 100.0
        let chronicDailyAverage = ((chronicWorkload / Double(chronicDays)) * 100.0).rounded() / 100.0

        // 4. Calculate ACWR ratio
        let acwr: Double
        if chronicWeeklyAverage <= 0.0 {
            acwr = (acuteWorkload <= 0.0) ? 0.0 : 2.0
        } else {
            acwr = ((acuteWorkload / chronicWeeklyAverage) * 100.0).rounded() / 100.0
        }

        let riskZone = AcwrRiskZone.from(ratio: acwr)
        let deloadRecommended = (riskZone == .high)
        let recommendation = generateRecommendation(
            riskZone: riskZone,
            acwr: acwr,
            chronicWorkload: chronicWorkload,
            acuteWorkload: acuteWorkload
        )

        return WorkloadRatio(
            targetDate: effectiveTarget,
            acuteWorkload: acuteWorkload,
            acuteDailyAverage: acuteDailyAverage,
            chronicWorkload: chronicWorkload,
            chronicWeeklyAverage: chronicWeeklyAverage,
            chronicDailyAverage: chronicDailyAverage,
            acwr: acwr,
            riskZone: riskZone,
            deloadRecommended: deloadRecommended,
            recommendation: recommendation,
            dailyWorkloads: orderedWorkloads
        )
    }

    public static func calculateSessionLoad(training: Training) -> Double {
        guard training.status == .completed else { return 0.0 }
        let rpe = training.overallRpe ?? defaultModerateRpe
        let durationMinutes: Double

        if let start = training.startTime, let end = training.endTime, end.timeIntervalSince(start) > 0 {
            durationMinutes = max(1.0, end.timeIntervalSince(start) / 60.0)
        } else {
            let totalSets = training.loggedExercises.reduce(0) { $0 + $1.sets.count }
            if totalSets > 0 {
                durationMinutes = max(20.0, Double(totalSets) * 2.5)
            } else {
                durationMinutes = defaultSessionMinutes
            }
        }

        return ((rpe * durationMinutes) * 10.0).rounded() / 10.0
    }

    private static func calculateTrainingVolume(training: Training) -> Double {
        var total = 0.0
        for ex in training.loggedExercises {
            for set in ex.sets where set.isCompleted {
                total += set.volumeKg
            }
        }
        return ((total) * 10.0).rounded() / 10.0
    }

    private static func generateRecommendation(
        riskZone: AcwrRiskZone,
        acwr: Double,
        chronicWorkload: Double,
        acuteWorkload: Double
    ) -> String {
        if chronicWorkload <= 0.0 && acuteWorkload <= 0.0 {
            return "No completed workouts recorded in the past 28 days. Start with light-to-moderate sessions to build your chronic fitness base."
        }
        switch riskZone {
        case .low:
            return "Acute workload is significantly below your chronic training baseline (ACWR \(acwr) < 0.80). Progressive overload is recommended to maintain fitness and avoid deconditioning injury."
        case .optimal:
            return "Acute workload is in the optimal 'sweet spot' (ACWR \(acwr)). Fitness gains are maximized while keeping injury risk minimal. Maintain your current progression."
        case .caution:
            return "Acute workload is moderately elevated above your chronic baseline (ACWR \(acwr)). Fatigue is accumulating; prioritize post-workout recovery, nutrition, and sleep."
        case .high:
            return "Acute workload spike detected (ACWR \(acwr) > 1.50). Training in the danger zone significantly elevates injury risk. A deload week (40-50% volume reduction) or active recovery is strongly recommended."
        }
    }
}
