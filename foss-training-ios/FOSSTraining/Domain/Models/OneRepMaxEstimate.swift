import Foundation

public struct TrainingPercentage: Identifiable, Codable, Hashable, Sendable {
    public var id: Int { percentage }
    public let percentage: Int
    public let weightKg: Double

    public init(percentage: Int, weightKg: Double) {
        self.percentage = percentage
        self.weightKg = weightKg
    }
}

public struct OneRepMaxEstimate: Identifiable, Codable, Hashable, Sendable {
    public var id: String { formula.rawValue }
    public let formula: OneRepMaxFormula
    public let estimatedOneRepMax: Double
    public let percentages: [TrainingPercentage]

    public init(formula: OneRepMaxFormula, estimatedOneRepMax: Double, percentages: [TrainingPercentage]) {
        self.formula = formula
        self.estimatedOneRepMax = estimatedOneRepMax
        self.percentages = percentages
    }

    private enum CodingKeys: String, CodingKey {
        case formula
        case estimatedOneRepMax
        case estimated1Rm
        case percentages
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let formula = try container.decode(OneRepMaxFormula.self, forKey: .formula)
        self.formula = formula

        if let est = try container.decodeIfPresent(Double.self, forKey: .estimatedOneRepMax) {
            self.estimatedOneRepMax = est
        } else if let est1Rm = try container.decodeIfPresent(Double.self, forKey: .estimated1Rm) {
            self.estimatedOneRepMax = est1Rm
        } else {
            self.estimatedOneRepMax = 0.0
        }

        if let list = try? container.decode([TrainingPercentage].self, forKey: .percentages) {
            self.percentages = list
        } else if let dict = try? container.decode([String: Double].self, forKey: .percentages) {
            let mapped = dict.compactMap { key, value -> TrainingPercentage? in
                guard let pct = Int(key) else { return nil }
                return TrainingPercentage(percentage: pct, weightKg: value)
            }.sorted { $0.percentage > $1.percentage }
            self.percentages = mapped
        } else {
            self.percentages = []
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(formula, forKey: .formula)
        try container.encode(estimatedOneRepMax, forKey: .estimatedOneRepMax)
        try container.encode(percentages, forKey: .percentages)
    }
}
