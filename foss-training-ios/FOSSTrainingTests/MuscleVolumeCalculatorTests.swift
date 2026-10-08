import Foundation
import Testing
@testable import FOSSTraining

@Suite("SPEC-05: Weekly Muscle Volume & Hypertrophy Landmark Tests")
struct MuscleVolumeCalculatorTests {

    private func createExercise(id: Int, name: String, primary: String, secondaries: [String] = []) -> Exercise {
        Exercise(
            id: id,
            name: name,
            primaryCategory: .resistance,
            primaryMuscleGroup: primary,
            secondaryMuscleGroups: secondaries
        )
    }

    private func createTraining(id: Int, daysAgo: Int, exercises: [SessionExerciseItem], referenceDate: Date = Date()) -> Training {
        let calendar = Calendar.current
        let date = calendar.date(byAdding: .day, value: -daysAgo, to: calendar.startOfDay(for: referenceDate))!
        return Training(
            id: id,
            name: "Session \(id)",
            trainingDate: date,
            status: .completed,
            loggedExercises: exercises
        )
    }

    @Test("HypertrophyVolumeStatus: Israetel threshold mapping and descriptions")
    func testHypertrophyVolumeStatusLandmarks() {
        #expect(HypertrophyVolumeStatus.from(sets: 0.0) == .belowMev)
        #expect(HypertrophyVolumeStatus.from(sets: 5.9) == .belowMev)
        #expect(HypertrophyVolumeStatus.from(sets: 6.0) == .maintenance)
        #expect(HypertrophyVolumeStatus.from(sets: 9.9) == .maintenance)
        #expect(HypertrophyVolumeStatus.from(sets: 10.0) == .adaptive)
        #expect(HypertrophyVolumeStatus.from(sets: 20.0) == .adaptive)
        #expect(HypertrophyVolumeStatus.from(sets: 20.1) == .approachingMrv)
        #expect(HypertrophyVolumeStatus.from(sets: 25.0) == .approachingMrv)
        #expect(HypertrophyVolumeStatus.from(sets: 25.1) == .exceededMrv)

        for status in HypertrophyVolumeStatus.allCases {
            #expect(!status.displayName.isEmpty)
            #expect(!status.description.isEmpty)
            #expect(status.id == status.rawValue)
        }
    }

    @Test("HypertrophyVolumeStatus: JSON decoding across backend aliases")
    func testHypertrophyVolumeStatusDecoder() throws {
        let decoder = JSONDecoder()

        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"BELOW_MEV\"".data(using: .utf8)!) == .belowMev)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"UNDERTRAINED\"".data(using: .utf8)!) == .belowMev)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"MAINTENANCE\"".data(using: .utf8)!) == .maintenance)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"OPTIMAL\"".data(using: .utf8)!) == .adaptive)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"ADAPTIVE\"".data(using: .utf8)!) == .adaptive)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"APPROACHING_MRV\"".data(using: .utf8)!) == .approachingMrv)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"OVERTRAINED\"".data(using: .utf8)!) == .exceededMrv)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"EXCEEDED_MRV\"".data(using: .utf8)!) == .exceededMrv)
        #expect(try decoder.decode(HypertrophyVolumeStatus.self, from: "\"CUSTOM_UNKNOWN\"".data(using: .utf8)!) == .belowMev)
    }

    @Test("MuscleVolumeCalculator: Empty workout history returns zero sets and initial guide")
    func testEmptyHistory() {
        let result = MuscleVolumeCalculator.compute(exercises: [], trainings: [])
        #expect(result.totalWorkingSets == 0)
        #expect(result.totalSets == 0)
        #expect(result.totalVolumeKg == 0.0)
        #expect(result.recommendations.first?.contains("No completed resistance workouts") == true)
        #expect(result.muscleVolumes.count == MuscleVolumeCalculator.targetMuscles.count)
        #expect(result.muscleVolumes.first?.id != nil)
    }

    @Test("MuscleVolumeCalculator: Direct and indirect sets calculation with 0.5 weighting")
    func testDirectAndIndirectSets() {
        let bench = createExercise(id: 1, name: "Barbell Bench Press", primary: "Chest", secondaries: ["Triceps", "Shoulders"])
        let sets = (1...10).map {
            ResistanceSet(setNumber: $0, weightKg: 100.0, repetitions: 10, isCompleted: true)
        }
        let item = SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Barbell Bench Press", sets: sets)
        let training = createTraining(id: 1, daysAgo: 2, exercises: [item])

        let result = MuscleVolumeCalculator.compute(exercises: [bench], trainings: [training])

        #expect(result.totalWorkingSets == 10)
        #expect(result.totalVolumeKg == 10000.0)

        let chest = result.muscleVolumes.first { $0.muscleGroup == "Chest" }
        #expect(chest?.directSets == 10)
        #expect(chest?.indirectSets == 0)
        #expect(chest?.effectiveSets == 10.0)
        #expect(chest?.status == .adaptive)

        let triceps = result.muscleVolumes.first { $0.muscleGroup == "Triceps" }
        #expect(triceps?.directSets == 0)
        #expect(triceps?.indirectSets == 10)
        #expect(triceps?.effectiveSets == 5.0) // 10 * 0.5 = 5.0
        #expect(triceps?.status == .belowMev)
    }

    @Test("MuscleVolumeCalculator: Name keyword fallback when exercise is not in catalog")
    func testNameKeywordFallbacks() {
        let sets = [ResistanceSet(setNumber: 1, weightKg: 80, repetitions: 10, isCompleted: true)]
        let exercises = [
            SessionExerciseItem(orderIndex: 1, exerciseId: 101, exerciseName: "Dumbbell Incline Bench", sets: sets),
            SessionExerciseItem(orderIndex: 2, exerciseId: 102, exerciseName: "Barbell Back Squat", sets: sets),
            SessionExerciseItem(orderIndex: 3, exerciseId: 103, exerciseName: "Conventional Deadlift", sets: sets),
            SessionExerciseItem(orderIndex: 4, exerciseId: 104, exerciseName: "Lat Pulldown", sets: sets),
            SessionExerciseItem(orderIndex: 5, exerciseId: 105, exerciseName: "Overhead Shoulder Press", sets: sets),
            SessionExerciseItem(orderIndex: 6, exerciseId: 106, exerciseName: "Bicep Curl", sets: sets),
            SessionExerciseItem(orderIndex: 7, exerciseId: 107, exerciseName: "Tricep Dip", sets: sets),
            SessionExerciseItem(orderIndex: 8, exerciseId: 108, exerciseName: "Hamstring Curl", sets: sets),
            SessionExerciseItem(orderIndex: 9, exerciseId: 109, exerciseName: "Glute Bridge", sets: sets),
            SessionExerciseItem(orderIndex: 10, exerciseId: 110, exerciseName: "Standing Calf Raise", sets: sets),
            SessionExerciseItem(orderIndex: 11, exerciseId: 111, exerciseName: "Hanging Abs Leg Raise", sets: sets),
            SessionExerciseItem(orderIndex: 12, exerciseId: 112, exerciseName: "Unknown Motion", sets: sets)
        ]
        let training = createTraining(id: 1, daysAgo: 1, exercises: exercises)
        let result = MuscleVolumeCalculator.compute(exercises: [], trainings: [training])

        #expect(result.totalWorkingSets == 12)
        let chest = result.muscleVolumes.first { $0.muscleGroup == "Chest" }
        #expect(chest?.directSets == 1)
        let quads = result.muscleVolumes.first { $0.muscleGroup == "Quadriceps" }
        #expect(quads?.directSets == 1)
        let upperBack = result.muscleVolumes.first { $0.muscleGroup == "Upper Back" }
        #expect(upperBack?.directSets == 1)
        let lats = result.muscleVolumes.first { $0.muscleGroup == "Lats" }
        #expect(lats?.directSets == 1)
        let shoulders = result.muscleVolumes.first { $0.muscleGroup == "Shoulders" }
        #expect(shoulders?.directSets == 1)
        let biceps = result.muscleVolumes.first { $0.muscleGroup == "Biceps" }
        #expect(biceps?.directSets == 1)
        let triceps = result.muscleVolumes.first { $0.muscleGroup == "Triceps" }
        #expect(triceps?.directSets == 1)
        let hamstrings = result.muscleVolumes.first { $0.muscleGroup == "Hamstrings" }
        #expect(hamstrings?.directSets == 1)
        let glutes = result.muscleVolumes.first { $0.muscleGroup == "Glutes" }
        #expect(glutes?.directSets == 1)
        let calves = result.muscleVolumes.first { $0.muscleGroup == "Calves" }
        #expect(calves?.directSets == 1)
        let abs = result.muscleVolumes.first { $0.muscleGroup == "Abs" }
        #expect(abs?.directSets == 1)

        // Exercise with custom primary muscle not in target list (e.g. Forearms)
        let forearmEx = createExercise(id: 113, name: "Wrist Curl", primary: "Forearms")
        let forearmTraining = createTraining(id: 2, daysAgo: 1, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 113, exerciseName: "Wrist Curl", sets: sets)
        ])
        let forearmResult = MuscleVolumeCalculator.compute(exercises: [forearmEx], trainings: [forearmTraining])
        #expect(forearmResult.totalWorkingSets == 1)
    }

    @Test("MuscleVolumeCalculator: Recommendations branch coverage")
    func testRecommendationsBranches() {
        // 1. Upper outpaces Lower (Upper > 2.5 * Lower, lower < 15)
        let bench = createExercise(id: 1, name: "Bench", primary: "Chest")
        let benchSets = (1...16).map { ResistanceSet(setNumber: $0, weightKg: 80, repetitions: 10, isCompleted: true) }
        let tUpper = createTraining(id: 1, daysAgo: 1, exercises: [SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Bench", sets: benchSets)])
        let rUpper = MuscleVolumeCalculator.compute(exercises: [bench], trainings: [tUpper])
        #expect(rUpper.recommendations.contains { $0.contains("Upper body volume") })

        // 2. Lower outpaces Upper (Lower > 2 * Upper, upper < 15)
        let squat = createExercise(id: 2, name: "Squat", primary: "Quadriceps")
        let squatSets = (1...16).map { ResistanceSet(setNumber: $0, weightKg: 100, repetitions: 10, isCompleted: true) }
        let tLower = createTraining(id: 2, daysAgo: 1, exercises: [SessionExerciseItem(orderIndex: 1, exerciseId: 2, exerciseName: "Squat", sets: squatSets)])
        let rLower = MuscleVolumeCalculator.compute(exercises: [squat], trainings: [tLower])
        #expect(rLower.recommendations.contains { $0.contains("Lower body volume") })

        // 3. Push outpaces Pull
        let overhead = createExercise(id: 3, name: "Press", primary: "Shoulders")
        let pushSets = (1...12).map { ResistanceSet(setNumber: $0, weightKg: 50, repetitions: 10, isCompleted: true) }
        let pullSets2 = (1...3).map { ResistanceSet(setNumber: $0, weightKg: 50, repetitions: 10, isCompleted: true) }
        let row = createExercise(id: 4, name: "Row", primary: "Lats")
        let legSets15 = (1...15).map { ResistanceSet(setNumber: $0, weightKg: 50, repetitions: 10, isCompleted: true) }
        let tPush = createTraining(id: 3, daysAgo: 1, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 3, exerciseName: "Press", sets: pushSets),
            SessionExerciseItem(orderIndex: 2, exerciseId: 4, exerciseName: "Row", sets: pullSets2),
            SessionExerciseItem(orderIndex: 3, exerciseId: 2, exerciseName: "Squat", sets: legSets15)
        ])
        let rPush = MuscleVolumeCalculator.compute(exercises: [overhead, row, squat], trainings: [tPush])
        #expect(rPush.recommendations.contains { $0.contains("Push-to-pull ratio is elevated") })

        // 4. Pull outpaces Push
        let tPull = createTraining(id: 4, daysAgo: 1, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 3, exerciseName: "Press", sets: pullSets2),
            SessionExerciseItem(orderIndex: 2, exerciseId: 4, exerciseName: "Row", sets: pushSets),
            SessionExerciseItem(orderIndex: 3, exerciseId: 2, exerciseName: "Squat", sets: legSets15)
        ])
        let rPull = MuscleVolumeCalculator.compute(exercises: [overhead, row, squat], trainings: [tPull])
        #expect(rPull.recommendations.contains { $0.contains("Pull volume dominates push volume") })

        // 5. Overtrained detection (>20 sets)
        let massiveChestSets = (1...26).map { ResistanceSet(setNumber: $0, weightKg: 80, repetitions: 10, isCompleted: true) }
        let tOver = createTraining(id: 5, daysAgo: 1, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Bench", sets: massiveChestSets),
            SessionExerciseItem(orderIndex: 2, exerciseId: 4, exerciseName: "Row", sets: legSets15),
            SessionExerciseItem(orderIndex: 3, exerciseId: 2, exerciseName: "Squat", sets: legSets15)
        ])
        let rOver = MuscleVolumeCalculator.compute(exercises: [bench, row, squat], trainings: [tOver])
        #expect(!rOver.overtrainedMuscleGroups.isEmpty)
        #expect(rOver.recommendations.contains { $0.contains("Excessive weekly volume") })

        // 6. Balanced distribution
        let balSets12 = (1...12).map { ResistanceSet(setNumber: $0, weightKg: 80, repetitions: 10, isCompleted: true) }
        let tBal = createTraining(id: 6, daysAgo: 1, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Bench", sets: balSets12),
            SessionExerciseItem(orderIndex: 2, exerciseId: 4, exerciseName: "Row", sets: balSets12),
            SessionExerciseItem(orderIndex: 3, exerciseId: 2, exerciseName: "Squat", sets: balSets12)
        ])
        let rBal = MuscleVolumeCalculator.compute(exercises: [bench, row, squat], trainings: [tBal])
        #expect(!rBal.optimalMuscleGroups.isEmpty)
        #expect(rBal.recommendations.contains { $0.contains("Excellent weekly volume distribution") || $0.contains("Under-stimulated") })
    }

    @Test("MuscleVolumeCalculator: Dates filtering and uncompleted sets ignored")
    func testDateFilteringAndUncompletedSets() {
        let bench = createExercise(id: 1, name: "Bench", primary: "Chest")
        let uncompletedSet = ResistanceSet(setNumber: 1, weightKg: 100, repetitions: 10, isCompleted: false)
        let completedSet = ResistanceSet(setNumber: 2, weightKg: 100, repetitions: 10, isCompleted: true)

        let oldTraining = createTraining(id: 1, daysAgo: 20, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Bench", sets: [completedSet])
        ])
        let recentTraining = createTraining(id: 2, daysAgo: 2, exercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Bench", sets: [uncompletedSet, completedSet])
        ])

        let result = MuscleVolumeCalculator.compute(exercises: [bench], trainings: [oldTraining, recentTraining])
        #expect(result.totalWorkingSets == 1) // only 1 completed set within the past 7 days
    }
}
