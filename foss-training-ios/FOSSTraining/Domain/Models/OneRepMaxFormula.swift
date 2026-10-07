import Foundation

public enum OneRepMaxFormula: String, CaseIterable, Identifiable, Codable, Sendable {
    case epley = "EPLEY"
    case brzycki = "BRZYCKI"
    case lombardi = "LOMBARDI"
    case mayhew = "MAYHEW"
    case oconner = "OCONNER"
    case wathen = "WATHEN"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .epley: return "Epley"
        case .brzycki: return "Brzycki"
        case .lombardi: return "Lombardi"
        case .mayhew: return "Mayhew et al."
        case .oconner: return "O'Conner"
        case .wathen: return "Wathen"
        }
    }

    public func calculate(weightKg: Double, repetitions: Int) -> Double {
        guard weightKg > 0 else { return 0.0 }
        guard repetitions >= 1 else { return 0.0 }
        if repetitions == 1 {
            return (weightKg * 100.0).rounded() / 100.0
        }

        let raw: Double
        switch self {
        case .epley:
            raw = weightKg * (1.0 + Double(repetitions) / 30.0)
        case .brzycki:
            if repetitions >= 37 {
                raw = weightKg * 36.0 // Fallback if reps exceeding asymptote
            } else {
                raw = weightKg * (36.0 / (37.0 - Double(repetitions)))
            }
        case .lombardi:
            raw = weightKg * pow(Double(repetitions), 0.10)
        case .mayhew:
            raw = (100.0 * weightKg) / (52.2 + 41.9 * exp(-0.055 * Double(repetitions)))
        case .oconner:
            raw = weightKg * (1.0 + 0.025 * Double(repetitions))
        case .wathen:
            raw = (100.0 * weightKg) / (48.8 + 53.8 * exp(-0.075 * Double(repetitions)))
        }

        return (raw * 100.0).rounded() / 100.0
    }
}
