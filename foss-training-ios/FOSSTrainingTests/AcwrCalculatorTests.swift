import Foundation
import Testing
@testable import FOSSTraining

@Suite("SPEC-05: ACWR Workload Ratio & Injury Risk Tests")
struct AcwrCalculatorTests {

    private func createTraining(
        id: Int,
        daysAgo: Int,
        status: TrainingStatus = .completed,
        rpe: Double = 6.0,
        durationMinutes: Double = 60.0,
        volumeKg: Double = 1000.0,
        referenceDate: Date = Date()
    ) -> Training {
        let calendar = Calendar.current
        let trainingDate = calendar.date(byAdding: .day, value: -daysAgo, to: calendar.startOfDay(for: referenceDate))!
        let startTime = trainingDate
        let endTime = startTime.addingTimeInterval(durationMinutes * 60.0)

        let set = ResistanceSet(
            setNumber: 1,
            setType: .normal,
            weightKg: volumeKg / 10.0,
            repetitions: 10,
            rpe: rpe,
            restSeconds: 90,
            isCompleted: true
        )
        let loggedEx = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 1,
            exerciseName: "Squat",
            part: .main,
            restSeconds: 90,
            sets: [set]
        )

        return Training(
            id: id,
            name: "Leg Day",
            description: "Squat workout",
            trainingDate: trainingDate,
            startTime: startTime,
            endTime: endTime,
            status: status,
            notes: nil,
            overallRpe: rpe,
            programId: nil,
            loggedExercises: [loggedEx]
        )
    }

    @Test("ACWR: Session load calculation with duration and RPE")
    func testSessionLoadCalculation() {
        let training = createTraining(id: 1, daysAgo: 0, rpe: 8.0, durationMinutes: 45.0)
        let load = AcwrCalculator.calculateSessionLoad(training: training)
        #expect(load == 360.0) // 8.0 * 45.0 = 360.0

        let plannedTraining = createTraining(id: 2, daysAgo: 0, status: .inProgress, rpe: 8.0)
        #expect(AcwrCalculator.calculateSessionLoad(training: plannedTraining) == 0.0)
    }

    @Test("ACWR: Session load fallback when startTime or endTime are missing")
    func testSessionLoadDurationFallbacks() {
        // Training with sets but no start/end time
        let set = ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5, rpe: 7.0, isCompleted: true)
        let logged = SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Bench", sets: [set, set]) // 2 sets * 2.5 = 5.0 -> clamped to 20.0
        let trainingWithSets = Training(
            id: 3,
            name: "Chest",
            trainingDate: Date(),
            startTime: nil,
            endTime: nil,
            status: .completed,
            overallRpe: 7.0,
            loggedExercises: [logged]
        )
        #expect(AcwrCalculator.calculateSessionLoad(training: trainingWithSets) == 140.0) // 7.0 * 20.0 = 140.0

        // Training with no sets and no times -> default 45.0 mins, default RPE 6.0
        let emptyTraining = Training(
            id: 4,
            name: "General",
            trainingDate: Date(),
            startTime: nil,
            endTime: nil,
            status: .completed,
            overallRpe: nil,
            loggedExercises: []
        )
        #expect(AcwrCalculator.calculateSessionLoad(training: emptyTraining) == 270.0) // 6.0 * 45.0 = 270.0
    }

    @Test("ACWR: Empty workout history returns zero acute and chronic workload with guidance")
    func testEmptyWorkoutHistory() {
        let result = AcwrCalculator.calculate(trainings: [], targetDate: Date())
        #expect(result.acuteWorkload == 0.0)
        #expect(result.chronicWorkload == 0.0)
        #expect(result.acuteDailyAverage == 0.0)
        #expect(result.chronicWeeklyAverage == 0.0)
        #expect(result.chronicDailyAverage == 0.0)
        #expect(result.acwr == 0.0)
        #expect(result.acwrRatio == 0.0)
        #expect(result.riskZone == .low)
        #expect(result.deloadRecommended == false)
        #expect(result.recommendation.contains("No completed workouts recorded"))
        #expect(result.dailyWorkloads.count == 28)
    }

    @Test("ACWR: Initial workout baseline (acute session without chronic baseline is classified as spike)")
    func testInitialWorkoutBaseline() {
        let today = Date()
        let training = createTraining(id: 1, daysAgo: 1, rpe: 6.0, durationMinutes: 30.0, referenceDate: today)
        let result = AcwrCalculator.calculate(trainings: [training], targetDate: today)

        // 1 session of 180 AU in acute window -> acute = 180, chronic = 180, chronic weekly avg = 45 -> ACWR = 4.0
        #expect(result.acuteWorkload == 180.0)
        #expect(result.chronicWorkload == 180.0)
        #expect(result.acwr == 4.0)
        #expect(result.riskZone == .high)
        #expect(result.recommendation.contains("danger zone"))
    }

    @Test("ACWR: Balanced 4-week training classifies as optimal sweet spot (0.80 - 1.30)")
    func testBalancedOptimalWorkload() {
        let refDate = Date()
        var trainings: [Training] = []
        // 3 consistent workouts per week across 4 weeks
        for week in 0..<4 {
            for dayOffset in [1, 3, 5] {
                let daysAgo = week * 7 + dayOffset
                trainings.append(createTraining(id: week * 10 + dayOffset, daysAgo: daysAgo, rpe: 7.0, durationMinutes: 60.0, referenceDate: refDate))
            }
        }

        let result = AcwrCalculator.calculate(trainings: trainings, targetDate: refDate)
        #expect(result.acwr >= 0.80 && result.acwr <= 1.30)
        #expect(result.riskZone == .optimal)
        #expect(result.riskZone == AcwrRiskZone.sweetSpot)
        #expect(result.deloadRecommended == false)
        #expect(result.recommendation.contains("sweet spot"))
    }

    @Test("ACWR: Sudden load spike triggers danger zone (>= 1.50) and recommends deload")
    func testSuddenLoadSpikeDangerZone() {
        let refDate = Date()
        var trainings: [Training] = []
        // Light baseline for weeks 2-4 (1 workout per week)
        for week in 1..<4 {
            let daysAgo = week * 7 + 2
            trainings.append(createTraining(id: week * 10, daysAgo: daysAgo, rpe: 5.0, durationMinutes: 30.0, referenceDate: refDate))
        }
        // Heavy spike in acute week (5 intense workouts)
        for day in 0..<5 {
            trainings.append(createTraining(id: 100 + day, daysAgo: day, rpe: 9.0, durationMinutes: 90.0, referenceDate: refDate))
        }

        let result = AcwrCalculator.calculate(trainings: trainings, targetDate: refDate)
        #expect(result.acwr >= 1.50)
        #expect(result.riskZone == .high)
        #expect(result.riskZone == AcwrRiskZone.dangerZone)
        #expect(result.deloadRecommended == true)
        #expect(result.recommendation.contains("danger zone"))
    }

    @Test("ACWR: Under-training zone (< 0.80) detected when acute week drops significantly")
    func testUnderTrainingDeconditioningZone() {
        let refDate = Date()
        var trainings: [Training] = []
        // Heavy baseline for weeks 2-4 (4 intense workouts per week)
        for week in 1..<4 {
            for day in [1, 2, 4, 5] {
                let daysAgo = week * 7 + day
                trainings.append(createTraining(id: week * 10 + day, daysAgo: daysAgo, rpe: 8.0, durationMinutes: 60.0, referenceDate: refDate))
            }
        }
        // Minimal load in current week (0 workouts)
        let result = AcwrCalculator.calculate(trainings: trainings, targetDate: refDate)
        #expect(result.acwr < 0.80)
        #expect(result.riskZone == .low)
        #expect(result.riskZone == AcwrRiskZone.underTraining)
        #expect(result.deloadRecommended == false)
        #expect(result.recommendation.contains("significantly below"))
    }

    @Test("ACWR: Elevated risk zone (1.30 - 1.50) fatigue warning")
    func testElevatedRiskCautionZone() {
        let refDate = Date()
        var trainings: [Training] = []
        // Weeks 1, 2, 3 prior to acute (1000 AU each = 3000 AU total)
        for week in 1..<4 {
            for day in [2, 5] {
                // 500 AU each -> 1000 AU/week
                trainings.append(createTraining(id: week * 10 + day, daysAgo: week * 7 + day, rpe: 10.0, durationMinutes: 50.0, referenceDate: refDate))
            }
        }
        // Acute week: 3 sessions of 500 AU = 1500 AU -> ACWR = 1500 / 1125 = 1.33
        for day in [1, 3, 5] {
            trainings.append(createTraining(id: 200 + day, daysAgo: day, rpe: 10.0, durationMinutes: 50.0, referenceDate: refDate))
        }

        let result = AcwrCalculator.calculate(trainings: trainings, targetDate: refDate)
        #expect(result.acwr == 1.33)
        #expect(result.riskZone == .caution)
        #expect(result.riskZone == AcwrRiskZone.elevatedRisk)
        #expect(result.recommendation.contains("moderately elevated"))
    }

    @Test("AcwrRiskZone: Descriptions, aliases and JSON decoder variants")
    func testAcwrRiskZoneDetails() throws {
        let decoder = JSONDecoder()

        for zone in AcwrRiskZone.allCases {
            #expect(!zone.description.isEmpty)
        }

        // Test decoding backend enum string names
        let jsonUnder = "\"UNDERTRAINING\"".data(using: .utf8)!
        #expect(try decoder.decode(AcwrRiskZone.self, from: jsonUnder) == .low)

        let jsonOptimal = "\"OPTIMAL\"".data(using: .utf8)!
        #expect(try decoder.decode(AcwrRiskZone.self, from: jsonOptimal) == .optimal)

        let jsonOver = "\"OVERREACHING\"".data(using: .utf8)!
        #expect(try decoder.decode(AcwrRiskZone.self, from: jsonOver) == .caution)

        let jsonHigh = "\"HIGH_RISK\"".data(using: .utf8)!
        #expect(try decoder.decode(AcwrRiskZone.self, from: jsonHigh) == .high)

        let jsonDanger = "\"DANGER_ZONE\"".data(using: .utf8)!
        #expect(try decoder.decode(AcwrRiskZone.self, from: jsonDanger) == .high)

        let jsonUnknown = "\"SOMETHING_NEW\"".data(using: .utf8)!
        #expect(try decoder.decode(AcwrRiskZone.self, from: jsonUnknown) == .optimal)
    }

    @Test("DailyWorkload and WorkloadRatio: IDs, accessors and delegating computeACWR")
    func testAccessorsAndDelegates() {
        let now = Date()
        let daily = DailyWorkload(date: now, workloadAu: 100.0, totalVolumeKg: 500.0, completedSessions: 1)
        #expect(daily.id == now)

        let training = createTraining(id: 1, daysAgo: 1, referenceDate: now)
        let ratioVal = WorkloadRatioCalculator.computeACWR(trainings: [training], targetDate: now)
        #expect(ratioVal > 0)
    }
}
