import Foundation
import Testing
@testable import FOSSTraining

@Suite("SPEC-05: Personal Records, Progression & Heart Rate Zone Tests")
struct PersonalRecordAndProgressionTests {

    private func createExercise(id: Int, name: String) -> Exercise {
        Exercise(id: id, name: name, primaryCategory: .resistance)
    }

    private func createTraining(
        id: Int,
        exerciseId: Int,
        exerciseName: String,
        weightKg: Double,
        reps: Int,
        daysAgo: Int,
        status: TrainingStatus = .completed,
        isCompleted: Bool = true
    ) -> Training {
        let calendar = Calendar.current
        let date = calendar.date(byAdding: .day, value: -daysAgo, to: calendar.startOfDay(for: Date()))!
        let set = ResistanceSet(setNumber: 1, setType: .normal, weightKg: weightKg, repetitions: reps, rpe: 8.0, isCompleted: isCompleted)
        let item = SessionExerciseItem(orderIndex: 1, exerciseId: exerciseId, exerciseName: exerciseName, sets: [set])
        return Training(
            id: id,
            name: "Workout \(id)",
            trainingDate: date,
            status: status,
            loggedExercises: [item]
        )
    }

    // MARK: - Personal Record Tracker Tests

    @Test("PersonalRecordTracker: Detects max weight, volume, estimated 1RM, and reps")
    func testPersonalRecordDetection() {
        let ex = createExercise(id: 1, name: "Bench Press")

        // Session 1: 80kg x 8 reps (vol = 640kg, est 1RM = 101.33)
        let t1 = createTraining(id: 1, exerciseId: 1, exerciseName: "Bench Press", weightKg: 80.0, reps: 8, daysAgo: 10)
        // Session 2: 100kg x 3 reps (vol = 300kg, est 1RM = 110.0) -> New max weight & best 1RM
        let t2 = createTraining(id: 2, exerciseId: 1, exerciseName: "Bench Press", weightKg: 100.0, reps: 3, daysAgo: 5)
        // Session 3: 60kg x 15 reps (vol = 900kg, est 1RM = 90.0) -> New max reps & max volume
        let t3 = createTraining(id: 3, exerciseId: 1, exerciseName: "Bench Press", weightKg: 60.0, reps: 15, daysAgo: 1)

        let records = PersonalRecordTracker.computeForExercise(exerciseId: 1, exerciseName: "Bench Press", trainings: [t1, t2, t3])
        #expect(records.count == 4)

        let maxWeight = records.first { $0.recordType == .maxWeight }
        #expect(maxWeight?.value == 100.0)
        #expect(maxWeight?.trainingId == 2)
        #expect(maxWeight?.id == "1-MAX_WEIGHT")

        let best1Rm = records.first { $0.recordType == .maxEstimated1RM }
        #expect(best1Rm?.value == 110.0)
        #expect(best1Rm?.trainingId == 2)

        let maxVol = records.first { $0.recordType == .maxVolume }
        #expect(maxVol?.value == 900.0)
        #expect(maxVol?.trainingId == 3)

        let maxReps = records.first { $0.recordType == .maxReps }
        #expect(maxReps?.value == 15.0)
        #expect(maxReps?.trainingId == 3)

        for prType in PRRecordType.allCases {
            #expect(!prType.displayName.isEmpty)
            #expect(prType.id == prType.rawValue)
        }
    }

    @Test("PersonalRecordTracker: computeAll over exercise array and empty trainings")
    func testPersonalRecordComputeAll() {
        let ex1 = createExercise(id: 1, name: "Squat")
        let ex2 = createExercise(id: 2, name: "Deadlift")

        let t1 = createTraining(id: 1, exerciseId: 1, exerciseName: "Squat", weightKg: 140, reps: 5, daysAgo: 2)
        let t2 = createTraining(id: 2, exerciseId: 2, exerciseName: "Deadlift", weightKg: 180, reps: 3, daysAgo: 1)

        let allRecords = PersonalRecordTracker.computeAll(exercises: [ex1, ex2], trainings: [t1, t2])
        #expect(allRecords.count == 8) // 4 for Squat, 4 for Deadlift

        let emptyRecords = PersonalRecordTracker.computeAll(exercises: [ex1], trainings: [])
        #expect(emptyRecords.isEmpty)
    }

    // MARK: - Heart Rate Zone Tests

    @Test("HeartRateZoneCalculator: Karvonen method with resting heart rate")
    func testHeartRateKarvonenMethod() throws {
        let zones = try HeartRateZoneCalculator.compute(maxHr: 190, restingHr: 60, age: nil)
        #expect(zones.method == .karvonen)
        #expect(zones.maxHr == 190)
        #expect(zones.restingHr == 60)
        #expect(zones.heartRateReserve == 130) // 190 - 60 = 130
        #expect(zones.zones.count == 5)

        // Zone 1: 50% - 60% of HRR + HRrest -> 60 + 0.50*130 = 125, 60 + 0.60*130 = 138
        let z1 = zones.zones[0]
        #expect(z1.zoneNumber == 1)
        #expect(z1.id == 1)
        #expect(z1.displayName == "Active Recovery")
        #expect(z1.minBpm == 125)
        #expect(z1.maxBpm == 138)
        #expect(!z1.trainingBenefit.isEmpty)
        #expect(!z1.description.isEmpty)

        // Zone 5: 90% - 100% of HRR + HRrest -> 60 + 0.90*130 = 177, 60 + 1.00*130 = 190
        let z5 = zones.zones[4]
        #expect(z5.minBpm == 177)
        #expect(z5.maxBpm == 190)
    }

    @Test("HeartRateZoneCalculator: Percent of Max HR method without resting heart rate")
    func testHeartRatePercentMaxHr() throws {
        let zones = try HeartRateZoneCalculator.compute(maxHr: 200, restingHr: nil, age: nil)
        #expect(zones.method == .percentMaxHr)
        #expect(zones.heartRateReserve == nil)

        // Zone 1: 50% - 60% of 200 = 100 - 120
        #expect(zones.zones[0].minBpm == 100)
        #expect(zones.zones[0].maxBpm == 120)

        // Zone 5: 90% - 100% of 200 = 180 - 200
        #expect(zones.zones[4].minBpm == 180)
        #expect(zones.zones[4].maxBpm == 200)

        for m in HeartRateZoneMethod.allCases {
            #expect(!m.displayName.isEmpty)
            #expect(m.id == m.rawValue)
        }
    }

    @Test("HeartRateZoneCalculator: Tanaka formula estimation from age")
    func testHeartRateTanakaFormula() throws {
        // Tanaka: 208 - (0.7 * 30) = 208 - 21 = 187
        let zones = try HeartRateZoneCalculator.compute(maxHr: nil, restingHr: nil, age: 30)
        #expect(zones.maxHr == 187)
    }

    @Test("HeartRateZoneCalculator: Validation errors")
    func testHeartRateValidationErrors() {
        // Missing both maxHr and age
        #expect(throws: HeartRateZoneError.missingParameters) {
            try HeartRateZoneCalculator.compute(maxHr: nil, restingHr: nil, age: nil)
        }

        // Invalid maxHr (< 60 or > 240)
        #expect(throws: HeartRateZoneError.invalidMaxHr(50)) {
            try HeartRateZoneCalculator.compute(maxHr: 50, restingHr: nil, age: nil)
        }
        #expect(throws: HeartRateZoneError.invalidMaxHr(250)) {
            try HeartRateZoneCalculator.compute(maxHr: 250, restingHr: nil, age: nil)
        }

        // Invalid age (< 10 or > 110)
        #expect(throws: HeartRateZoneError.invalidAge(8)) {
            try HeartRateZoneCalculator.compute(maxHr: nil, restingHr: nil, age: 8)
        }
        #expect(throws: HeartRateZoneError.invalidAge(120)) {
            try HeartRateZoneCalculator.compute(maxHr: nil, restingHr: nil, age: 120)
        }

        // Invalid restingHr (< 30 or >= maxHr)
        #expect(throws: HeartRateZoneError.invalidRestingHr(20, maxHr: 180)) {
            try HeartRateZoneCalculator.compute(maxHr: 180, restingHr: 20, age: nil)
        }
        #expect(throws: HeartRateZoneError.invalidRestingHr(185, maxHr: 180)) {
            try HeartRateZoneCalculator.compute(maxHr: 180, restingHr: 185, age: nil)
        }

        // Error descriptions
        #expect(HeartRateZoneError.missingParameters.errorDescription != nil)
        #expect(HeartRateZoneError.invalidMaxHr(40).errorDescription != nil)
        #expect(HeartRateZoneError.invalidAge(5).errorDescription != nil)
        #expect(HeartRateZoneError.invalidRestingHr(20, maxHr: 180).errorDescription != nil)
    }

    // MARK: - Exercise Progression Tests

    @Test("ExerciseProgressionCalculator: Progression tracking and trend evaluation")
    func testExerciseProgression() throws {
        let ex = createExercise(id: 1, name: "Squat")

        // 1. Single session -> insufficient data
        let t1 = createTraining(id: 1, exerciseId: 1, exerciseName: "Squat", weightKg: 100, reps: 5, daysAgo: 14)
        let prog1 = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [t1])
        #expect(prog1.totalSessions == 1)
        #expect(prog1.trend == .insufficientData)

        // 2. Improving trend: 100kg x 5 (116.67) to 110kg x 5 (128.33) -> +10% gain
        let t2 = createTraining(id: 2, exerciseId: 1, exerciseName: "Squat", weightKg: 110, reps: 5, daysAgo: 7)
        let progImproving = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [t1, t2])
        #expect(progImproving.totalSessions == 2)
        #expect(progImproving.trend == .improving)
        #expect(progImproving.trend == ProgressionTrend.increasing)
        #expect(progImproving.relative1RmGainPercentage > 2.0)
        #expect(progImproving.percentageChange == progImproving.relative1RmGainPercentage)
        #expect(progImproving.allTimeBest1RmKg == 128.33)
        #expect(progImproving.allTimeBestTopWeightKg == 110.0)

        // 3. Declining trend: 110kg x 5 (7 days ago) to 90kg x 5 (1 day ago)
        let tDeclining = createTraining(id: 4, exerciseId: 1, exerciseName: "Squat", weightKg: 90, reps: 5, daysAgo: 1)
        let progDeclining = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [t2, tDeclining])
        #expect(progDeclining.trend == .declining)
        #expect(progDeclining.trend == ProgressionTrend.decreasing)

        // 4. Stagnant trend: within +/- 2%
        let tStagnant = createTraining(id: 3, exerciseId: 1, exerciseName: "Squat", weightKg: 100.5, reps: 5, daysAgo: 3)
        let progStagnant = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [t1, tStagnant])
        #expect(progStagnant.trend == .stagnant)
        #expect(progStagnant.trend == ProgressionTrend.stable)

        // Trend descriptions and cases
        for trend in ProgressionTrend.allCases {
            #expect(!trend.displayName.isEmpty)
            #expect(!trend.description.isEmpty)
            #expect(trend.id == trend.rawValue)
        }

        // Trend decoders
        let decoder = JSONDecoder()
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"IMPROVING\"".data(using: .utf8)!) == .improving)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"INCREASING\"".data(using: .utf8)!) == .improving)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"STAGNANT\"".data(using: .utf8)!) == .stagnant)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"STABLE\"".data(using: .utf8)!) == .stagnant)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"DECLINING\"".data(using: .utf8)!) == .declining)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"DECREASING\"".data(using: .utf8)!) == .declining)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"INSUFFICIENT_DATA\"".data(using: .utf8)!) == .insufficientData)
        #expect(try decoder.decode(ProgressionTrend.self, from: "\"UNKNOWN_STRING\"".data(using: .utf8)!) == .insufficientData)

        // ProgressionDataPoint Codable test
        let dp = progImproving.dataPoints[0]
        #expect(dp.id == dp.date)
        let enc = JSONEncoder()
        let data = try enc.encode(dp)
        let decDp = try decoder.decode(ProgressionDataPoint.self, from: data)
        #expect(decDp.trainingId == dp.trainingId)
        #expect(decDp.estimatedOneRepMax == dp.estimatedOneRepMax)

        // Backend DTO format with estimated1RmKg
        let backendDpJson = """
        {
            "trainingId": 5,
            "date": \(Int(Date().timeIntervalSince1970)),
            "totalSets": 3,
            "workingSets": 3,
            "totalReps": 15,
            "totalVolumeKg": 1500.0,
            "topWeightKg": 100.0,
            "topWeightReps": 5,
            "estimated1RmKg": 116.67,
            "averageIntensityKg": 100.0
        }
        """.data(using: .utf8)!
        let decBackendDp = try decoder.decode(ProgressionDataPoint.self, from: backendDpJson)
        #expect(decBackendDp.estimatedOneRepMax == 116.67)

        // Backend DTO format without any estimated 1RM field (defaults to 0.0)
        let noEstDpJson = """
        {
            "trainingId": 6,
            "date": \(Int(Date().timeIntervalSince1970)),
            "totalSets": 3,
            "workingSets": 3,
            "totalReps": 15,
            "totalVolumeKg": 1500.0,
            "topWeightKg": 100.0,
            "topWeightReps": 5,
            "averageIntensityKg": 100.0
        }
        """.data(using: .utf8)!
        let decNoEstDp = try decoder.decode(ProgressionDataPoint.self, from: noEstDpJson)
        #expect(decNoEstDp.estimatedOneRepMax == 0.0)

        // Same date sorting by trainingId
        let sameDay1 = createTraining(id: 10, exerciseId: 1, exerciseName: "Squat", weightKg: 100, reps: 5, daysAgo: 5)
        let sameDay2 = createTraining(id: 20, exerciseId: 1, exerciseName: "Squat", weightKg: 105, reps: 5, daysAgo: 5)
        let sameDayProg = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [sameDay2, sameDay1])
        #expect(sameDayProg.dataPoints[0].trainingId == 10)
        #expect(sameDayProg.dataPoints[1].trainingId == 20)

        // Date range filtering (startDate and endDate filtering out sessions)
        let earlyDate = Calendar.current.date(byAdding: .day, value: -10, to: Date())!
        let lateDate = Calendar.current.date(byAdding: .day, value: -3, to: Date())!
        let rangeProg = ExerciseProgressionCalculator.compute(
            exercise: ex,
            trainings: [t1, t2, tDeclining],
            startDate: earlyDate,
            endDate: lateDate
        )
        #expect(rangeProg.totalSessions == 1)

        // Multiple sets in single session (same weight more reps, and same weight fewer reps)
        let s1 = ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100.0, repetitions: 3, rpe: 7.0, restSeconds: 60, isCompleted: true)
        let s2 = ResistanceSet(setNumber: 2, setType: .normal, weightKg: 100.0, repetitions: 5, rpe: 8.0, restSeconds: 60, isCompleted: true)
        let s3 = ResistanceSet(setNumber: 3, setType: .normal, weightKg: 100.0, repetitions: 2, rpe: 8.0, restSeconds: 60, isCompleted: true)
        let multiItem = SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Squat", part: .main, restSeconds: 60, sets: [s1, s2, s3])
        let multiTraining = Training(id: 30, name: "MultiSet", trainingDate: Date(), startTime: nil, endTime: nil, status: .completed, notes: nil, overallRpe: 7.0, programId: nil, loggedExercises: [multiItem])
        let multiProg = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [multiTraining])
        #expect(multiProg.dataPoints[0].topWeightReps == 5)

        // Session with warm-up sets only
        let wuSet = ResistanceSet(setNumber: 1, setType: .warmUp, weightKg: 50.0, repetitions: 10, rpe: 5.0, restSeconds: 60, isCompleted: true)
        let wuItem = SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Squat", part: .main, restSeconds: 60, sets: [wuSet])
        let wuTraining = Training(id: 31, name: "WarmUpOnly", trainingDate: Date(), startTime: nil, endTime: nil, status: .completed, notes: nil, overallRpe: 5.0, programId: nil, loggedExercises: [wuItem])
        let wuProg = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [wuTraining])
        #expect(wuProg.dataPoints[0].workingSets == 0)
        #expect(wuProg.dataPoints[0].totalReps == 10)

        // Empty points progression
        let emptyProg = ExerciseProgressionCalculator.compute(exercise: ex, trainings: [])
        #expect(emptyProg.totalSessions == 0)
        #expect(emptyProg.initial1RmKg == 0.0)
    }
}
