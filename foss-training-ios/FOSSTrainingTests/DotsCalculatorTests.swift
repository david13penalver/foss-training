import Foundation
import Testing
@testable import FOSSTraining

@Suite("DOTS Powerlifting Scoring Parity Tests")
struct DotsCalculatorTests {

    @Test("DOTS Male: 83kg bodyweight with 650kg total matches canonical score")
    func testDotsMale_83kgBodyweight_650kgTotal() {
        // x = 83.0 kg
        // denom = -1.0930e-6 * 83^4 + 7.391293e-4 * 83^3 - 0.1918759221 * 83^2 + 24.0900786 * 83 - 307.754178
        // 500 / denom * 650 = ~438.81 DOTS
        let score = RelativeStrengthCalculator.calculateDots(totalKg: 650.0, bwKg: 83.0, gender: .male)
        #expect(score > 435.0 && score < 442.0)

        let fullScore = RelativeStrengthCalculator.calculate(totalKg: 650.0, bodyweightKg: 83.0, gender: .male, formula: .dots)
        #expect(fullScore.formula == .dots)
        #expect(fullScore.score == score)
        #expect(fullScore.tier == .elite)
        #expect(fullScore.classification == "Elite")
        #expect(!fullScore.tierDescription.isEmpty)
    }

    @Test("DOTS Female: 63kg bodyweight with 420kg total matches canonical score")
    func testDotsFemale_63kgBodyweight_420kgTotal() {
        let score = RelativeStrengthCalculator.calculateDots(totalKg: 420.0, bwKg: 63.0, gender: .female)
        #expect(score > 450.0 && score < 455.0)

        let fullScore = RelativeStrengthCalculator.calculate(totalKg: 420.0, bodyweightKg: 63.0, gender: .female, formula: .dots)
        #expect(fullScore.tier == .elite)
        #expect(fullScore.gender == .female)
    }

    @Test("DOTS Tier Classification: covers all 5 competitive tiers")
    func testDotsTierClassifications() {
        // Novice (< 250)
        let novice = RelativeStrengthCalculator.calculate(totalKg: 200.0, bodyweightKg: 80.0, gender: .male)
        #expect(novice.tier == .novice)
        #expect(novice.classification == "Novice")
        #expect(RelativeStrengthTier.evaluate(dotsScore: 249.9) == .novice)

        // Intermediate (250..<325)
        let intermediate = RelativeStrengthCalculator.calculate(totalKg: 400.0, bodyweightKg: 80.0, gender: .male)
        #expect(intermediate.tier == .intermediate)
        #expect(intermediate.classification == "Intermediate")
        #expect(RelativeStrengthTier.evaluate(dotsScore: 250.0) == .intermediate)
        #expect(RelativeStrengthTier.evaluate(dotsScore: 324.9) == .intermediate)

        // Advanced (325..<400)
        let advanced = RelativeStrengthCalculator.calculate(totalKg: 530.0, bodyweightKg: 80.0, gender: .male)
        #expect(advanced.tier == .advanced)
        #expect(advanced.classification == "Advanced")
        #expect(RelativeStrengthTier.evaluate(dotsScore: 325.0) == .advanced)
        #expect(RelativeStrengthTier.evaluate(dotsScore: 399.9) == .advanced)

        // Elite (400..<475)
        let elite = RelativeStrengthCalculator.calculate(totalKg: 650.0, bodyweightKg: 80.0, gender: .male)
        #expect(elite.tier == .elite)
        #expect(elite.classification == "Elite")
        #expect(RelativeStrengthTier.evaluate(dotsScore: 400.0) == .elite)
        #expect(RelativeStrengthTier.evaluate(dotsScore: 474.9) == .elite)

        // International Elite (>= 475)
        let intElite = RelativeStrengthCalculator.calculate(totalKg: 800.0, bodyweightKg: 80.0, gender: .male)
        #expect(intElite.tier == .internationalElite)
        #expect(intElite.classification == "International Elite")
        #expect(RelativeStrengthTier.evaluate(dotsScore: 475.0) == .internationalElite)
        #expect(RelativeStrengthTier.evaluate(dotsScore: 550.0) == .internationalElite)
    }

    @Test("DOTS Boundary Clamping: handles extreme and zero inputs gracefully")
    func testDotsClampingAndEdgeCases() {
        // Clamping below 40kg uses 40kg polynomial
        let below40 = RelativeStrengthCalculator.calculateDots(totalKg: 300.0, bwKg: 30.0, gender: .male)
        let exactly40 = RelativeStrengthCalculator.calculateDots(totalKg: 300.0, bwKg: 40.0, gender: .male)
        #expect(below40 == exactly40)

        // Clamping above 210kg uses 210kg polynomial
        let above210 = RelativeStrengthCalculator.calculateDots(totalKg: 600.0, bwKg: 250.0, gender: .male)
        let exactly210 = RelativeStrengthCalculator.calculateDots(totalKg: 600.0, bwKg: 210.0, gender: .male)
        #expect(above210 == exactly210)

        // Zero or negative inputs
        let zeroTotal = RelativeStrengthCalculator.calculate(totalKg: 0.0, bodyweightKg: 80.0, gender: .male)
        #expect(zeroTotal.score == 0.0)
        #expect(zeroTotal.tier == .novice)

        let negativeBw = RelativeStrengthCalculator.calculate(totalKg: 500.0, bodyweightKg: -80.0, gender: .male)
        #expect(negativeBw.score == 0.0)

        let zeroDots = RelativeStrengthCalculator.calculateDots(totalKg: 0.0, bwKg: 80.0, gender: .male)
        #expect(zeroDots == 0.0)

        let zeroBwDots = RelativeStrengthCalculator.calculateDots(totalKg: 500.0, bwKg: 0.0, gender: .male)
        #expect(zeroBwDots == 0.0)

        // Gender other defaults to male polynomial
        let otherGender = RelativeStrengthCalculator.calculateDots(totalKg: 500.0, bwKg: 80.0, gender: .other)
        let maleGender = RelativeStrengthCalculator.calculateDots(totalKg: 500.0, bwKg: 80.0, gender: .male)
        #expect(otherGender == maleGender)
    }
}
