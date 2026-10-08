import Foundation

public struct RelativeStrengthScore: Codable, Hashable, Sendable {
    public let formula: ScoringFormula
    public let score: Double
    public let totalKg: Double
    public let bodyweightKg: Double
    public let gender: Gender
    public let strengthToWeightRatio: Double
    public let dotsScore: Double
    public let wilksScore: Double
    public let tier: RelativeStrengthTier
    public let tierDescription: String

    public var totalWeightKg: Double { totalKg }
    public var relativeStrengthRatio: Double { strengthToWeightRatio }
    public var ratio: Double { strengthToWeightRatio }
    public var dots: Double { dotsScore }
    public var wilks: Double { wilksScore }
    public var classification: String {
        score == 0.0 ? "Untrained" : tier.displayName
    }

    public init(
        formula: ScoringFormula = .dots,
        score: Double? = nil,
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender,
        strengthToWeightRatio: Double,
        dotsScore: Double,
        wilksScore: Double,
        tier: RelativeStrengthTier,
        tierDescription: String? = nil
    ) {
        self.formula = formula
        let effectiveScore = score ?? (formula == .dots ? dotsScore : wilksScore)
        self.score = effectiveScore
        self.totalKg = totalKg
        self.bodyweightKg = bodyweightKg
        self.gender = gender
        self.strengthToWeightRatio = strengthToWeightRatio
        self.dotsScore = dotsScore
        self.wilksScore = wilksScore
        self.tier = tier
        self.tierDescription = tierDescription ?? tier.description
    }

    public init(
        totalWeightKg: Double,
        bodyweightKg: Double,
        gender: Gender,
        relativeStrengthRatio: Double,
        dotsScore: Double,
        wilksScore: Double,
        classification: String
    ) {
        let evaluatedTier = RelativeStrengthTier.evaluate(dotsScore: dotsScore)
        self.init(
            formula: .dots,
            score: dotsScore,
            totalKg: totalWeightKg,
            bodyweightKg: bodyweightKg,
            gender: gender,
            strengthToWeightRatio: relativeStrengthRatio,
            dotsScore: dotsScore,
            wilksScore: wilksScore,
            tier: evaluatedTier,
            tierDescription: evaluatedTier.description
        )
    }
}
