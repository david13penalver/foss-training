import Foundation

public enum RelativeStrengthCalculator {
    public static func calculate(
        totalWeightKg: Double,
        bodyweightKg: Double,
        gender: Gender = .male
    ) -> RelativeStrengthScore {
        calculate(totalKg: totalWeightKg, bodyweightKg: bodyweightKg, gender: gender, formula: .dots)
    }

    public static func calculate(
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender = .male,
        formula: ScoringFormula = .dots
    ) -> RelativeStrengthScore {
        guard totalKg > 0, bodyweightKg > 0 else {
            return RelativeStrengthScore(
                formula: formula,
                score: 0.0,
                totalKg: max(0.0, totalKg),
                bodyweightKg: max(0.0, bodyweightKg),
                gender: gender,
                strengthToWeightRatio: 0.0,
                dotsScore: 0.0,
                wilksScore: 0.0,
                tier: .novice,
                tierDescription: RelativeStrengthTier.novice.description
            )
        }

        let ratio = ((totalKg / bodyweightKg) * 100.0).rounded() / 100.0
        let dots = calculateDots(totalKg: totalKg, bwKg: bodyweightKg, gender: gender)
        let wilks = calculateWilks(totalKg: totalKg, bwKg: bodyweightKg, gender: gender)
        let tier = RelativeStrengthTier.evaluate(dotsScore: dots)
        let selectedScore = (formula == .dots) ? dots : wilks

        return RelativeStrengthScore(
            formula: formula,
            score: selectedScore,
            totalKg: totalKg,
            bodyweightKg: bodyweightKg,
            gender: gender,
            strengthToWeightRatio: ratio,
            dotsScore: dots,
            wilksScore: wilks,
            tier: tier,
            tierDescription: tier.description
        )
    }

    public static func calculateDots(totalKg: Double, bwKg: Double, gender: Gender) -> Double {
        guard totalKg > 0, bwKg > 0 else { return 0.0 }
        let x = min(210.0, max(40.0, bwKg))
        let denom: Double
        if gender == .female {
            denom = -0.0000010706 * pow(x, 4)
                    + 0.0005158568 * pow(x, 3)
                    - 0.1126655495 * pow(x, 2)
                    + 13.6175032 * x
                    - 57.96288
        } else {
            denom = -0.0000010930 * pow(x, 4)
                    + 0.0007391293 * pow(x, 3)
                    - 0.1918759221 * pow(x, 2)
                    + 24.0900786 * x
                    - 307.754178
        }

        guard denom > 0 else { return 0.0 }
        let score = totalKg * (500.0 / denom)
        return (score * 100.0).rounded() / 100.0
    }

    public static func calculateWilks(totalKg: Double, bwKg: Double, gender: Gender) -> Double {
        guard totalKg > 0, bwKg > 0 else { return 0.0 }
        let x = min(210.0, max(40.0, bwKg))
        let denom: Double
        if gender == .female {
            denom = 594.31747775582
                    - 27.23842536447 * x
                    + 0.82112226871 * pow(x, 2)
                    - 0.00930733913 * pow(x, 3)
                    + 0.00004731582 * pow(x, 4)
                    - 0.00000009054 * pow(x, 5)
        } else {
            denom = -216.0475144
                    + 16.2606339 * x
                    - 0.002388645 * pow(x, 2)
                    - 0.00113732 * pow(x, 3)
                    + 0.00000701863 * pow(x, 4)
                    - 0.00000001291 * pow(x, 5)
        }

        guard denom > 0 else { return 0.0 }
        let score = totalKg * (500.0 / denom)
        return (score * 100.0).rounded() / 100.0
    }
}
