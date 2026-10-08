import Foundation

public enum PersonalRecordTracker {
    public static func computeForExercise(exerciseId: Int, exerciseName: String, trainings: [Training]) -> [PersonalRecord] {
        var bestWeight: Double?
        var bestWeightDate: Date?
        var bestWeightTrainingId: Int?

        var bestVolume: Double?
        var bestVolumeDate: Date?
        var bestVolumeTrainingId: Int?

        var best1Rm: Double?
        var best1RmDate: Date?
        var best1RmTrainingId: Int?

        var bestReps: Int?
        var bestRepsDate: Date?
        var bestRepsTrainingId: Int?

        for tr in trainings where tr.status == .completed {
            for item in tr.loggedExercises where item.exerciseId == exerciseId {
                // Session Volume
                var sessionVol = 0.0
                for set in item.sets where set.isCompleted {
                    sessionVol += set.volumeKg
                }
                if sessionVol > 0.0 {
                    if bestVolume == nil || sessionVol > bestVolume! {
                        bestVolume = (sessionVol * 10.0).rounded() / 10.0
                        bestVolumeDate = tr.trainingDate
                        bestVolumeTrainingId = tr.id
                    }
                }

                // Sets Inspection
                for set in item.sets where set.isCompleted {
                    if set.repetitions > 0 {
                        if bestReps == nil || set.repetitions > bestReps! {
                            bestReps = set.repetitions
                            bestRepsDate = tr.trainingDate
                            bestRepsTrainingId = tr.id
                        }
                    }

                    if set.weightKg > 0.0 && set.repetitions > 0 {
                        if bestWeight == nil || set.weightKg > bestWeight! {
                            bestWeight = set.weightKg
                            bestWeightDate = tr.trainingDate
                            bestWeightTrainingId = tr.id
                        }

                        let est1Rm = OneRepMaxFormula.epley.calculate(weightKg: set.weightKg, repetitions: set.repetitions)
                        if best1Rm == nil || est1Rm > best1Rm! {
                            best1Rm = est1Rm
                            best1RmDate = tr.trainingDate
                            best1RmTrainingId = tr.id
                        }
                    }
                }
            }
        }

        var records: [PersonalRecord] = []

        if let bw = bestWeight, let bwd = bestWeightDate, let bwt = bestWeightTrainingId {
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxWeight,
                value: bw,
                unit: "kg",
                achievedDate: bwd,
                trainingId: bwt
            ))
        }

        if let bv = bestVolume, let bvd = bestVolumeDate, let bvt = bestVolumeTrainingId {
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxVolume,
                value: bv,
                unit: "kg",
                achievedDate: bvd,
                trainingId: bvt
            ))
        }

        if let b1 = best1Rm, let b1d = best1RmDate, let b1t = best1RmTrainingId {
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxEstimated1RM,
                value: b1,
                unit: "kg",
                achievedDate: b1d,
                trainingId: b1t
            ))
        }

        if let br = bestReps, let brd = bestRepsDate, let brt = bestRepsTrainingId {
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxReps,
                value: Double(br),
                unit: "reps",
                achievedDate: brd,
                trainingId: brt
            ))
        }

        return records
    }

    public static func computeAll(exercises: [Exercise], trainings: [Training]) -> [PersonalRecord] {
        var results: [PersonalRecord] = []
        for ex in exercises {
            let prs = computeForExercise(exerciseId: ex.id, exerciseName: ex.name, trainings: trainings)
            results.append(contentsOf: prs)
        }
        return results
    }
}
