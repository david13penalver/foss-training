import Foundation

public enum OneRepMaxCalculator {
    public static func calculate(weightKg: Double, reps: Int, formula: OneRepMaxFormula) -> OneRepMaxEstimate {
        let est = formula.calculate(weightKg: weightKg, repetitions: reps)
        let percentages = [95, 90, 85, 80, 75, 70, 65, 60, 55, 50].map { p in
            let pctWeight = (est * Double(p) / 100.0 * 100.0).rounded() / 100.0
            return TrainingPercentage(percentage: p, weightKg: pctWeight)
        }
        return OneRepMaxEstimate(
            formula: formula,
            estimatedOneRepMax: est,
            percentages: percentages
        )
    }

    public static func calculateAll(weightKg: Double, reps: Int) -> [OneRepMaxEstimate] {
        OneRepMaxFormula.allCases.map { formula in
            calculate(weightKg: weightKg, reps: reps, formula: formula)
        }
    }
}
