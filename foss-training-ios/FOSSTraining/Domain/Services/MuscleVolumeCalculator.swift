import Foundation

public enum MuscleVolumeCalculator {
    public static let targetMuscles = [
        "Chest", "Lats", "Upper Back", "Shoulders", "Biceps", "Triceps",
        "Quadriceps", "Hamstrings", "Glutes", "Calves", "Abs"
    ]

    private static let pushMuscles: Set<String> = ["Chest", "Shoulders", "Triceps"]
    private static let pullMuscles: Set<String> = ["Lats", "Upper Back", "Biceps"]
    private static let upperMuscles: Set<String> = ["Chest", "Lats", "Upper Back", "Shoulders", "Biceps", "Triceps", "Abs"]
    private static let lowerMuscles: Set<String> = ["Quadriceps", "Hamstrings", "Glutes", "Calves"]

    public static func compute(
        exercises: [Exercise],
        trainings: [Training],
        startDate: Date? = nil,
        endDate: Date? = nil
    ) -> WeeklyMuscleVolume {
        let calendar = Calendar.current
        let effectiveEnd = calendar.startOfDay(for: endDate ?? Date())
        let effectiveStart = calendar.startOfDay(for: startDate ?? calendar.date(byAdding: .day, value: -6, to: effectiveEnd)!)

        let exerciseMap = Dictionary(uniqueKeysWithValues: exercises.map { ($0.id, $0) })

        var directMap: [String: Int] = [:]
        var indirectMap: [String: Int] = [:]
        var volumeMap: [String: Double] = [:]

        for m in targetMuscles {
            directMap[m] = 0
            indirectMap[m] = 0
            volumeMap[m] = 0.0
        }

        var totalWorkingSets = 0
        var totalVolumeKg = 0.0

        for tr in trainings where tr.status == .completed {
            let trDate = calendar.startOfDay(for: tr.trainingDate)
            if trDate >= effectiveStart && trDate <= effectiveEnd {
                for item in tr.loggedExercises {
                    let ex = exerciseMap[item.exerciseId]
                    let primaryMuscle = resolvePrimaryMuscle(item: item, exercise: ex)
                    let secondaryMuscles = ex?.secondaryMuscleGroups ?? []

                    for set in item.sets where set.isCompleted {
                        totalWorkingSets += 1
                        totalVolumeKg += set.volumeKg

                        if let p = primaryMuscle {
                            let curDirect = directMap[p]
                            directMap[p] = (curDirect != nil ? curDirect! + 1 : 1)
                            let curVol = volumeMap[p]
                            volumeMap[p] = (curVol != nil ? curVol! + set.volumeKg : set.volumeKg)
                        }

                        for s in secondaryMuscles {
                            let curInd = indirectMap[s]
                            indirectMap[s] = (curInd != nil ? curInd! + 1 : 1)
                            let curVol = volumeMap[s]
                            volumeMap[s] = (curVol != nil ? curVol! + (set.volumeKg * 0.5) : set.volumeKg * 0.5)
                        }
                    }
                }
            }
        }

        // Build muscle group volumes
        var muscleVolumes: [MuscleGroupVolume] = []
        for muscle in targetMuscles {
            let direct = directMap[muscle] != nil ? directMap[muscle]! : 0
            let indirect = indirectMap[muscle] != nil ? indirectMap[muscle]! : 0
            let rawVol = volumeMap[muscle] != nil ? volumeMap[muscle]! : 0.0
            let effective = Double(direct) + 0.5 * Double(indirect)
            let vol = (rawVol * 10.0).rounded() / 10.0
            let status = HypertrophyVolumeStatus.from(sets: effective)

            muscleVolumes.append(MuscleGroupVolume(
                muscleGroup: muscle,
                muscleGroupName: muscle,
                directSets: direct,
                indirectSets: indirect,
                effectiveSets: (effective * 10.0).rounded() / 10.0,
                totalVolumeKg: vol,
                status: status
            ))
        }

        muscleVolumes.sort {
            if $0.effectiveSets != $1.effectiveSets {
                return $0.effectiveSets > $1.effectiveSets
            }
            return $0.muscleGroupName < $1.muscleGroupName
        }

        // Calculate push/pull and upper/lower metrics
        var pushSets = 0.0
        var pullSets = 0.0
        var upperSets = 0.0
        var lowerSets = 0.0

        for mv in muscleVolumes {
            if pushMuscles.contains(mv.muscleGroup) { pushSets += mv.effectiveSets }
            if pullMuscles.contains(mv.muscleGroup) { pullSets += mv.effectiveSets }
            if upperMuscles.contains(mv.muscleGroup) { upperSets += mv.effectiveSets }
            if lowerMuscles.contains(mv.muscleGroup) { lowerSets += mv.effectiveSets }
        }

        let pushPullRatio: Double
        if pullSets > 0.0 {
            pushPullRatio = ((pushSets / pullSets) * 100.0).rounded() / 100.0
        } else {
            pushPullRatio = (pushSets > 0.0) ? 2.0 : 1.0
        }

        let upperLowerRatio: Double
        if lowerSets > 0.0 {
            upperLowerRatio = ((upperSets / lowerSets) * 100.0).rounded() / 100.0
        } else {
            upperLowerRatio = (upperSets > 0.0) ? 3.0 : 1.0
        }

        let neglected = muscleVolumes.filter { $0.effectiveSets < 6.0 }.map(\.muscleGroup)
        let optimal = muscleVolumes.filter { $0.effectiveSets >= 10.0 && $0.effectiveSets <= 20.0 }.map(\.muscleGroup)
        let overtrained = muscleVolumes.filter { $0.effectiveSets > 20.0 }.map(\.muscleGroup)

        let recommendations = generateRecommendations(
            totalSets: totalWorkingSets,
            pushPullRatio: pushPullRatio,
            upperLowerRatio: upperLowerRatio,
            upperSets: upperSets,
            lowerSets: lowerSets,
            pushSets: pushSets,
            pullSets: pullSets,
            neglected: neglected,
            overtrained: overtrained
        )

        return WeeklyMuscleVolume(
            startDate: effectiveStart,
            endDate: effectiveEnd,
            totalWorkingSets: totalWorkingSets,
            totalVolumeKg: (totalVolumeKg * 10.0).rounded() / 10.0,
            muscleVolumes: muscleVolumes,
            pushPullRatio: pushPullRatio,
            upperLowerRatio: upperLowerRatio,
            neglectedMuscleGroups: neglected,
            optimalMuscleGroups: optimal,
            overtrainedMuscleGroups: overtrained,
            recommendations: recommendations
        )
    }

    private static func resolvePrimaryMuscle(item: SessionExerciseItem, exercise: Exercise?) -> String? {
        if let primary = exercise?.primaryMuscleGroup, !primary.isEmpty {
            return normalizeMuscleName(primary)
        }
        let lower = item.exerciseName.lowercased()
        if lower.contains("bench") || lower.contains("chest") { return "Chest" }
        if lower.contains("squat") || lower.contains("quad") { return "Quadriceps" }
        if lower.contains("deadlift") || lower.contains("back") { return "Upper Back" }
        if lower.contains("lat") || lower.contains("pull") { return "Lats" }
        if lower.contains("shoulder") || lower.contains("overhead") { return "Shoulders" }
        if lower.contains("hamstring") { return "Hamstrings" }
        if lower.contains("glute") || lower.contains("hip") { return "Glutes" }
        if lower.contains("calf") { return "Calves" }
        if lower.contains("abs") || lower.contains("core") { return "Abs" }
        if lower.contains("bicep") || lower.contains("curl") { return "Biceps" }
        if lower.contains("tricep") || lower.contains("dip") { return "Triceps" }
        return nil
    }

    private static func normalizeMuscleName(_ name: String) -> String {
        for target in targetMuscles {
            if target.caseInsensitiveCompare(name) == .orderedSame {
                return target
            }
        }
        return name
    }

    private static func generateRecommendations(
        totalSets: Int,
        pushPullRatio: Double,
        upperLowerRatio: Double,
        upperSets: Double,
        lowerSets: Double,
        pushSets: Double,
        pullSets: Double,
        neglected: [String],
        overtrained: [String]
    ) -> [String] {
        var recs: [String] = []

        if totalSets == 0 {
            recs.append("No completed resistance workouts recorded in this date range. Plan working sessions aiming for 10–20 weekly sets per muscle group.")
            return recs
        }

        if upperLowerRatio > 2.5 && lowerSets < 15.0 {
            recs.append(String(format: "Upper body volume (%.1f sets) significantly outpaces lower body (%.1f sets). Consider adding squats, hinges, or leg press sets.", upperSets, lowerSets))
        } else if upperLowerRatio < 0.5 && upperSets < 15.0 {
            recs.append(String(format: "Lower body volume (%.1f sets) significantly outpaces upper body (%.1f sets). Ensure sufficient pressing and pulling volume for complete athletic balance.", lowerSets, upperSets))
        }

        if pushPullRatio > 1.5 && pushSets >= 10.0 {
            recs.append(String(format: "Push-to-pull ratio is elevated (%.2f). Add rowing and vertical pulling volume to preserve scapular stability.", pushPullRatio))
        } else if pushPullRatio < 0.67 && pullSets >= 10.0 {
            recs.append(String(format: "Pull volume dominates push volume (Push/Pull ratio %.2f). Balance your split with horizontal and overhead pressing sets.", pushPullRatio))
        }

        if !overtrained.isEmpty {
            recs.append("Excessive weekly volume (>20 sets) detected for: \(overtrained.joined(separator: ", ")). Consider reducing sets to avoid junk volume.")
        }

        if !neglected.isEmpty && totalSets >= 15 {
            recs.append("Under-stimulated target muscle groups (<6 sets): \(neglected.prefix(4).joined(separator: ", ")). Add 2–4 working sets to meet maintenance or hypertrophy thresholds.")
        }

        if recs.isEmpty {
            recs.append("Excellent weekly volume distribution. Working sets align well with evidence-based hypertrophy landmarks (10–20 sets/muscle group).")
        }

        return recs
    }
}
