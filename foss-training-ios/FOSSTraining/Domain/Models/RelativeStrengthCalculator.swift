import Foundation

public enum AthleteGender: String, Codable, CaseIterable, Identifiable, Sendable {
    case male = "MALE"
    case female = "FEMALE"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .male: return "Male"
        case .female: return "Female"
        }
    }
}

public struct RelativeStrengthScore: Codable, Sendable {
    public let totalWeightKg: Double
    public let bodyweightKg: Double
    public let gender: AthleteGender
    public let relativeStrengthRatio: Double
    public let dotsScore: Double
    public let wilksScore: Double
    public let classification: String

    public init(
        totalWeightKg: Double,
        bodyweightKg: Double,
        gender: AthleteGender,
        relativeStrengthRatio: Double,
        dotsScore: Double,
        wilksScore: Double,
        classification: String
    ) {
        self.totalWeightKg = totalWeightKg
        self.bodyweightKg = bodyweightKg
        self.gender = gender
        self.relativeStrengthRatio = relativeStrengthRatio
        self.dotsScore = dotsScore
        self.wilksScore = wilksScore
        self.classification = classification
    }
}

public enum RelativeStrengthCalculator {
    public static func calculate(
        totalWeightKg: Double,
        bodyweightKg: Double,
        gender: AthleteGender = .male
    ) -> RelativeStrengthScore {
        guard totalWeightKg > 0, bodyweightKg > 0 else {
            return RelativeStrengthScore(
                totalWeightKg: totalWeightKg,
                bodyweightKg: bodyweightKg,
                gender: gender,
                relativeStrengthRatio: 0,
                dotsScore: 0,
                wilksScore: 0,
                classification: "Untrained"
            )
        }

        let ratio = ((totalWeightKg / bodyweightKg) * 100.0).rounded() / 100.0
        let dots = calculateDots(totalKg: totalWeightKg, bwKg: bodyweightKg, gender: gender)
        let wilks = calculateWilks(totalKg: totalWeightKg, bwKg: bodyweightKg, gender: gender)
        let classification = classify(dotsScore: dots)

        return RelativeStrengthScore(
            totalWeightKg: totalWeightKg,
            bodyweightKg: bodyweightKg,
            gender: gender,
            relativeStrengthRatio: ratio,
            dotsScore: dots,
            wilksScore: wilks,
            classification: classification
        )
    }

    private static func calculateDots(totalKg: Double, bwKg: Double, gender: AthleteGender) -> Double {
        let x = bwKg
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

    private static func calculateWilks(totalKg: Double, bwKg: Double, gender: AthleteGender) -> Double {
        let x = bwKg
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

    private static func classify(dotsScore: Double) -> String {
        switch dotsScore {
        case ..<250: return "Novice"
        case 250..<325: return "Intermediate"
        case 325..<400: return "Advanced"
        case 400..<475: return "Elite"
        default: return "International Elite"
        }
    }
}
