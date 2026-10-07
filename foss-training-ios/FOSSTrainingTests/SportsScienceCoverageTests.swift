import Testing
import Foundation
@testable import FOSSTraining

@Suite("Sports Science Edge Cases & Mathematical Coverage Tests")
struct SportsScienceCoverageTests {

    @Test("OneRepMaxFormula displayName, id, and edge cases")
    func testOneRepMaxEdgeCases() {
        for formula in OneRepMaxFormula.allCases {
            #expect(!formula.id.isEmpty)
            #expect(!formula.displayName.isEmpty)

            // Weight <= 0
            #expect(formula.calculate(weightKg: 0, repetitions: 5) == 0.0)
            #expect(formula.calculate(weightKg: -10, repetitions: 5) == 0.0)

            // Reps < 1
            #expect(formula.calculate(weightKg: 100, repetitions: 0) == 0.0)
            #expect(formula.calculate(weightKg: 100, repetitions: -2) == 0.0)

            // Exactly 1 rep
            #expect(formula.calculate(weightKg: 100.5, repetitions: 1) == 100.5)
        }

        // Brzycki asymptote clamp (reps >= 37)
        let clamped = OneRepMaxFormula.brzycki.calculate(weightKg: 100, repetitions: 40)
        #expect(clamped == 3600.0)

        // Standard rep calculation on all formulas
        #expect(OneRepMaxFormula.epley.calculate(weightKg: 100, repetitions: 5) > 100)
        #expect(OneRepMaxFormula.brzycki.calculate(weightKg: 100, repetitions: 5) > 100)
        #expect(OneRepMaxFormula.lombardi.calculate(weightKg: 100, repetitions: 5) > 100)
        #expect(OneRepMaxFormula.mayhew.calculate(weightKg: 100, repetitions: 5) > 100)
        #expect(OneRepMaxFormula.oconner.calculate(weightKg: 100, repetitions: 5) > 100)
        #expect(OneRepMaxFormula.wathen.calculate(weightKg: 100, repetitions: 5) > 100)
    }

    @Test("AcwrRiskZone from ratio thresholds, id, and displayNames")
    func testAcwrRiskZoneAllBranches() {
        for zone in AcwrRiskZone.allCases {
            #expect(!zone.id.isEmpty)
            #expect(!zone.displayName.isEmpty)
        }

        #expect(AcwrRiskZone.from(ratio: 0.5) == .low)
        #expect(AcwrRiskZone.from(ratio: 0.79) == .low)
        #expect(AcwrRiskZone.from(ratio: 0.80) == .optimal)
        #expect(AcwrRiskZone.from(ratio: 1.30) == .optimal)
        #expect(AcwrRiskZone.from(ratio: 1.31) == .caution)
        #expect(AcwrRiskZone.from(ratio: 1.50) == .caution)
        #expect(AcwrRiskZone.from(ratio: 1.51) == .high)
        #expect(AcwrRiskZone.from(ratio: 2.50) == .high)
    }

    @Test("WorkloadRatioCalculator with empty trainings returns baseline 1.0")
    func testWorkloadRatioEmpty() {
        let acwr = WorkloadRatioCalculator.computeACWR(trainings: [])
        #expect(acwr == 1.0)
    }

    @Test("WorkloadRatioCalculator with completed and non-completed trainings, start/end dates, and default fallbacks")
    func testWorkloadRatioWithTrainings() {
        let now = Date()
        let twoDaysAgo = Calendar.current.date(byAdding: .day, value: -2, to: now)!
        let tenDaysAgo = Calendar.current.date(byAdding: .day, value: -10, to: now)!
        let thirtyDaysAgo = Calendar.current.date(byAdding: .day, value: -30, to: now)!

        let t1 = Training(
            id: 1,
            name: "Recent Session",
            trainingDate: twoDaysAgo,
            startTime: twoDaysAgo,
            endTime: twoDaysAgo.addingTimeInterval(3600), // 60 mins
            status: .completed,
            overallRpe: 8.0
        )

        let t2 = Training(
            id: 2,
            name: "Older Session without explicit start/end",
            trainingDate: tenDaysAgo,
            status: .completed,
            overallRpe: nil // defaults to 6.0 and 45 mins
        )

        let t3 = Training(
            id: 3,
            name: "Planned workout (not completed)",
            trainingDate: twoDaysAgo,
            status: .planned
        )

        let t4 = Training(
            id: 4,
            name: "Outside 28 day chronic window",
            trainingDate: thirtyDaysAgo,
            status: .completed,
            overallRpe: 8.0
        )

        let ratio = WorkloadRatioCalculator.computeACWR(trainings: [t1, t2, t3, t4], targetDate: now)
        #expect(ratio > 0.0)

        // Distant future / extreme date to exercise ?? targetDate fallback
        let extremeDate = Date(timeIntervalSinceReferenceDate: 1e16)
        let fallbackRatio = WorkloadRatioCalculator.computeACWR(trainings: [], targetDate: extremeDate)
        #expect(fallbackRatio == 1.0)
    }

    @Test("AthleteGender cases, id, and displayName")
    func testAthleteGender() {
        for gender in AthleteGender.allCases {
            #expect(!gender.id.isEmpty)
            #expect(!gender.displayName.isEmpty)
        }
    }

    @Test("RelativeStrengthCalculator edge cases and zero inputs")
    func testRelativeStrengthZeroOrNegativeInputs() {
        let zeroWeight = RelativeStrengthCalculator.calculate(totalWeightKg: 0, bodyweightKg: 80, gender: .male)
        #expect(zeroWeight.dotsScore == 0)
        #expect(zeroWeight.classification == "Untrained")

        let zeroBw = RelativeStrengthCalculator.calculate(totalWeightKg: 500, bodyweightKg: 0, gender: .female)
        #expect(zeroBw.dotsScore == 0)
        #expect(zeroBw.classification == "Untrained")
    }

    @Test("RelativeStrengthCalculator all classification tiers for male and female")
    func testRelativeStrengthTiers() {
        // Novice (< 250)
        let novice = RelativeStrengthCalculator.calculate(totalWeightKg: 200, bodyweightKg: 80, gender: .male)
        #expect(novice.classification == "Novice")

        // Intermediate (250 ..< 325)
        let intermediate = RelativeStrengthCalculator.calculate(totalWeightKg: 400, bodyweightKg: 80, gender: .male)
        #expect(intermediate.classification == "Intermediate")

        // Advanced (325 ..< 400)
        let advanced = RelativeStrengthCalculator.calculate(totalWeightKg: 530, bodyweightKg: 80, gender: .male)
        #expect(advanced.classification == "Advanced")

        // Elite (400 ..< 475)
        let elite = RelativeStrengthCalculator.calculate(totalWeightKg: 650, bodyweightKg: 80, gender: .male)
        #expect(elite.classification == "Elite")

        // International Elite (>= 475)
        let intElite = RelativeStrengthCalculator.calculate(totalWeightKg: 800, bodyweightKg: 80, gender: .male)
        #expect(intElite.classification == "International Elite")

        // Female calculation
        let femaleScore = RelativeStrengthCalculator.calculate(totalWeightKg: 400, bodyweightKg: 60, gender: .female)
        #expect(femaleScore.dotsScore > 0)
        #expect(femaleScore.wilksScore > 0)
        #expect(femaleScore.relativeStrengthRatio > 0)
    }
}
