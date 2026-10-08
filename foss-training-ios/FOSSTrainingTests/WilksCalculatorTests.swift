import Foundation
import Testing
@testable import FOSSTraining

@Suite("Wilks Powerlifting Scoring & Value Objects Tests")
struct WilksCalculatorTests {

    @Test("Wilks Male: 80kg bodyweight with 500kg total matches canonical score")
    func testWilksMale_80kgBodyweight_500kgTotal() {
        let score = RelativeStrengthCalculator.calculateWilks(totalKg: 500.0, bwKg: 80.0, gender: .male)
        #expect(score > 340.0 && score < 350.0)

        let fullScore = RelativeStrengthCalculator.calculate(
            totalKg: 500.0,
            bodyweightKg: 80.0,
            gender: .male,
            formula: .wilks
        )
        #expect(fullScore.formula == .wilks)
        #expect(fullScore.score == score)
        #expect(fullScore.dotsScore > 340.0 && fullScore.dotsScore < 350.0)
        #expect(fullScore.wilksScore == score)
        #expect(fullScore.strengthToWeightRatio == 6.25)
    }

    @Test("Wilks Female: 60kg bodyweight with 350kg total matches canonical score")
    func testWilksFemale_60kgBodyweight_350kgTotal() {
        let score = RelativeStrengthCalculator.calculateWilks(totalKg: 350.0, bwKg: 60.0, gender: .female)
        #expect(score > 385.0 && score < 395.0)

        let fullScore = RelativeStrengthCalculator.calculate(
            totalKg: 350.0,
            bodyweightKg: 60.0,
            gender: .female,
            formula: .wilks
        )
        #expect(fullScore.score == score)
        #expect(fullScore.gender == .female)
    }

    @Test("Wilks Boundary Clamping & Edge Cases")
    func testWilksClampingAndEdgeCases() {
        // Clamping below 40kg uses 40kg polynomial
        let below40 = RelativeStrengthCalculator.calculateWilks(totalKg: 300.0, bwKg: 25.0, gender: .male)
        let exactly40 = RelativeStrengthCalculator.calculateWilks(totalKg: 300.0, bwKg: 40.0, gender: .male)
        #expect(below40 == exactly40)

        // Clamping above 210kg uses 210kg polynomial
        let above210 = RelativeStrengthCalculator.calculateWilks(totalKg: 500.0, bwKg: 240.0, gender: .male)
        let exactly210 = RelativeStrengthCalculator.calculateWilks(totalKg: 500.0, bwKg: 210.0, gender: .male)
        #expect(above210 == exactly210)

        // Zero or negative inputs
        let zeroTotal = RelativeStrengthCalculator.calculateWilks(totalKg: 0.0, bwKg: 80.0, gender: .male)
        #expect(zeroTotal == 0.0)

        let zeroBw = RelativeStrengthCalculator.calculateWilks(totalKg: 500.0, bwKg: 0.0, gender: .male)
        #expect(zeroBw == 0.0)

        // Gender other defaults to male polynomial
        let otherScore = RelativeStrengthCalculator.calculateWilks(totalKg: 500.0, bwKg: 80.0, gender: .other)
        let maleScore = RelativeStrengthCalculator.calculateWilks(totalKg: 500.0, bwKg: 80.0, gender: .male)
        #expect(otherScore == maleScore)
    }

    @Test("RelativeStrengthScore backwards compatibility and properties")
    func testRelativeStrengthScoreProperties() throws {
        let score = RelativeStrengthScore(
            totalWeightKg: 450.0,
            bodyweightKg: 75.0,
            gender: .male,
            relativeStrengthRatio: 6.0,
            dotsScore: 320.0,
            wilksScore: 310.0,
            classification: "Intermediate"
        )

        #expect(score.totalWeightKg == 450.0)
        #expect(score.relativeStrengthRatio == 6.0)
        #expect(score.ratio == 6.0)
        #expect(score.dots == 320.0)
        #expect(score.wilks == 310.0)
        #expect(score.classification == "Intermediate")
        #expect(score.tier == .intermediate)

        // Explicit formula initialization with wilks
        let wilksScoreObj = RelativeStrengthScore(
            formula: .wilks,
            score: nil,
            totalKg: 450.0,
            bodyweightKg: 75.0,
            gender: .male,
            strengthToWeightRatio: 6.0,
            dotsScore: 320.0,
            wilksScore: 310.0,
            tier: .intermediate,
            tierDescription: nil
        )
        #expect(wilksScoreObj.score == 310.0)

        // Codable serialization
        let data = try JSONEncoder().encode(score)
        let decoded = try JSONDecoder().decode(RelativeStrengthScore.self, from: data)
        #expect(decoded.totalKg == score.totalKg)
        #expect(decoded.score == score.score)
    }

    @Test("SPEC-06 Enums full coverage")
    func testSpec06Enums() {
        // Gender
        for g in Gender.allCases {
            #expect(!g.displayName.isEmpty)
            #expect(g.id == g.rawValue)
        }

        // ScoringFormula
        for f in ScoringFormula.allCases {
            #expect(!f.displayName.isEmpty)
            #expect(f.id == f.rawValue)
        }

        // WeightUnit
        for u in WeightUnit.allCases {
            #expect(!u.displayName.isEmpty)
            #expect(u.id == u.rawValue)
        }

        // RelativeStrengthTier
        for t in RelativeStrengthTier.allCases {
            #expect(!t.displayName.isEmpty)
            #expect(!t.description.isEmpty)
            #expect(t.id == t.rawValue)
        }
    }
}
