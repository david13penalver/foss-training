import Foundation

public enum ExerciseProgressionCalculator {
    public static func compute(
        exercise: Exercise,
        trainings: [Training],
        startDate: Date? = nil,
        endDate: Date? = nil,
        formula: OneRepMaxFormula = .epley
    ) -> ExerciseProgression {
        let calendar = Calendar.current
        var dataPoints: [ProgressionDataPoint] = []

        for tr in trainings where tr.status == .completed {
            let trDate = calendar.startOfDay(for: tr.trainingDate)
            if let start = startDate, trDate < calendar.startOfDay(for: start) { continue }
            if let end = endDate, trDate > calendar.startOfDay(for: end) { continue }

            for item in tr.loggedExercises where item.exerciseId == exercise.id {
                var totalSets = 0
                var workingSets = 0
                var totalWorkingReps = 0
                var totalAllReps = 0
                var workingVolumeKg = 0.0
                var allVolumeKg = 0.0
                var topWeightKg = 0.0
                var topWeightReps = 0
                var topWeightRpe: Double?
                var maxEst1Rm = 0.0
                var hasValidSet = false

                for set in item.sets where set.isCompleted {
                    let reps = set.repetitions
                    if reps > 0 {
                        totalSets += 1
                        totalAllReps += reps
                        allVolumeKg += set.volumeKg

                        if set.setType.countsAsWorkingVolume {
                            workingSets += 1
                            totalWorkingReps += reps
                            workingVolumeKg += set.volumeKg
                        }

                        if set.weightKg > 0.0 {
                            hasValidSet = true
                            if set.weightKg > topWeightKg {
                                topWeightKg = set.weightKg
                                topWeightReps = reps
                                topWeightRpe = set.rpe
                            } else if set.weightKg == topWeightKg && reps > topWeightReps {
                                topWeightReps = reps
                                topWeightRpe = set.rpe
                            }

                            let est = formula.calculate(weightKg: set.weightKg, repetitions: reps)
                            if est > maxEst1Rm {
                                maxEst1Rm = est
                            }
                        }
                    }
                }

                if hasValidSet && totalSets > 0 {
                    let repsToUse = workingSets > 0 ? totalWorkingReps : totalAllReps
                    let volToUse = workingSets > 0 ? workingVolumeKg : allVolumeKg
                    let avgIntensity = ((volToUse / Double(repsToUse)) * 100.0).rounded() / 100.0

                    dataPoints.append(ProgressionDataPoint(
                        trainingId: tr.id,
                        date: trDate,
                        totalSets: totalSets,
                        workingSets: workingSets,
                        totalReps: repsToUse,
                        totalVolumeKg: (volToUse * 10.0).rounded() / 10.0,
                        topWeightKg: topWeightKg,
                        topWeightReps: topWeightReps,
                        topWeightRpe: topWeightRpe,
                        estimatedOneRepMax: maxEst1Rm,
                        averageIntensityKg: avgIntensity
                    ))
                }
            }
        }

        // Sort data points chronologically
        dataPoints.sort {
            if $0.date != $1.date {
                return $0.date < $1.date
            }
            return $0.trainingId < $1.trainingId
        }

        let totalSessions = dataPoints.sizeOrCount
        var initial1Rm = 0.0
        var latest1Rm = 0.0
        var absoluteGain = 0.0
        var relativeGain = 0.0
        var best1Rm = 0.0
        var bestWeight = 0.0
        var maxVol = 0.0
        let effectiveStart = startDate ?? (dataPoints.first?.date ?? Date())
        let effectiveEnd = endDate ?? (dataPoints.last?.date ?? Date())

        if totalSessions > 0 {
            initial1Rm = dataPoints.first!.estimatedOneRepMax
            latest1Rm = dataPoints.last!.estimatedOneRepMax

            for dp in dataPoints {
                if dp.estimatedOneRepMax > best1Rm { best1Rm = dp.estimatedOneRepMax }
                if dp.topWeightKg > bestWeight { bestWeight = dp.topWeightKg }
                if dp.totalVolumeKg > maxVol { maxVol = dp.totalVolumeKg }
            }

            if totalSessions >= 2 {
                absoluteGain = ((latest1Rm - initial1Rm) * 100.0).rounded() / 100.0
                relativeGain = (((latest1Rm - initial1Rm) / initial1Rm * 100.0) * 100.0).rounded() / 100.0
            }
        }

        let trend = ProgressionTrend.evaluate(relativeGainPercentage: relativeGain, sessionCount: totalSessions)

        return ExerciseProgression(
            exerciseId: exercise.id,
            exerciseName: exercise.name,
            formula: formula,
            startDate: effectiveStart,
            endDate: effectiveEnd,
            totalSessions: totalSessions,
            initial1RmKg: initial1Rm,
            latest1RmKg: latest1Rm,
            absolute1RmGainKg: absoluteGain,
            relative1RmGainPercentage: relativeGain,
            allTimeBest1RmKg: best1Rm,
            allTimeBestTopWeightKg: bestWeight,
            allTimeMaxVolumeKg: maxVol,
            trend: trend,
            dataPoints: dataPoints
        )
    }
}

private extension Array {
    var sizeOrCount: Int { count }
}
