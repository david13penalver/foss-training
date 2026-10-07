import Testing
import Foundation
@testable import FOSSTraining

@Suite("Sports Science Domain Tests (1RM, ACWR & Powerlifting Scoring)")
struct SportsScienceTests {

    @Test("1RM estimation parity: Epley, Brzycki, Lombardi, Mayhew, O'Conner, Wathen")
    func testOneRepMaxFormulas() {
        // Test 100kg for 1 rep -> exactly 100kg across all formulas
        for formula in OneRepMaxFormula.allCases {
            let oneRep = formula.calculate(weightKg: 100.0, repetitions: 1)
            #expect(oneRep == 100.0)
        }

        // Test 100kg for 5 reps
        // Epley: 100 * (1 + 5/30) = 116.67
        let epley = OneRepMaxFormula.epley.calculate(weightKg: 100.0, repetitions: 5)
        #expect(epley == 116.67)

        // Brzycki: 100 * (36 / (37 - 5)) = 100 * 36 / 32 = 112.50
        let brzycki = OneRepMaxFormula.brzycki.calculate(weightKg: 100.0, repetitions: 5)
        #expect(brzycki == 112.50)

        // O'Conner: 100 * (1 + 0.025 * 5) = 112.50
        let oconner = OneRepMaxFormula.oconner.calculate(weightKg: 100.0, repetitions: 5)
        #expect(oconner == 112.50)
    }

    @Test("ACWR Workload Ratio and Risk Zone classification")
    func testWorkloadRatioRiskZones() {
        #expect(AcwrRiskZone.from(ratio: 0.6) == .low)
        #expect(AcwrRiskZone.from(ratio: 1.1) == .optimal)
        #expect(AcwrRiskZone.from(ratio: 1.4) == .caution)
        #expect(AcwrRiskZone.from(ratio: 1.8) == .high)
    }

    @Test("Relative Strength DOTS & Wilks calculation parity")
    func testRelativeStrengthFormulas() {
        // 80kg male lifting 500kg total
        let scoreMale = RelativeStrengthCalculator.calculate(
            totalWeightKg: 500.0,
            bodyweightKg: 80.0,
            gender: .male
        )

        #expect(scoreMale.relativeStrengthRatio == 6.25)
        #expect(scoreMale.dotsScore > 300.0)
        #expect(scoreMale.wilksScore > 300.0)
        #expect(!scoreMale.classification.isEmpty)
    }
}
