import Foundation
import SwiftUI

@Observable
@MainActor
public final class OneRepMaxCalculatorViewModel {
    private let analyticsRepository: AnalyticsRepository

    public var weightKg: Double = 100.0
    public var repetitions: Int = 5
    public var selectedFormula: OneRepMaxFormula = .epley
    public var estimates: [OneRepMaxEstimate] = []
    public var percentages: [TrainingPercentage] = []
    public var currentEstimated1Rm: Double = 0.0
    public var isLoading: Bool = false
    public var errorMessage: String?

    public init(analyticsRepository: AnalyticsRepository) {
        self.analyticsRepository = analyticsRepository
        calculate()
    }

    public func setWeight(_ weight: Double) {
        self.weightKg = max(0.0, weight)
        calculate()
    }

    public func setReps(_ reps: Int) {
        self.repetitions = max(1, min(36, reps))
        calculate()
    }

    public func setFormula(_ formula: OneRepMaxFormula) {
        self.selectedFormula = formula
        updateActiveEstimate()
    }

    public func calculate() {
        let allEstimates = OneRepMaxCalculator.calculateAll(weightKg: weightKg, reps: repetitions)
        self.estimates = allEstimates
        updateActiveEstimate()
    }

    public func updateActiveEstimate() {
        if let match = estimates.first(where: { $0.formula == selectedFormula }) {
            self.currentEstimated1Rm = match.estimatedOneRepMax
            self.percentages = match.percentages
        } else {
            let estimate = OneRepMaxCalculator.calculate(weightKg: weightKg, reps: repetitions, formula: selectedFormula)
            self.currentEstimated1Rm = estimate.estimatedOneRepMax
            self.percentages = estimate.percentages
        }
    }

    public func calculateAsync() async {
        isLoading = true
        errorMessage = nil
        do {
            self.estimates = try await analyticsRepository.calculateOneRepMax(weightKg: weightKg, reps: repetitions)
            updateActiveEstimate()
        } catch {
            self.errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}
