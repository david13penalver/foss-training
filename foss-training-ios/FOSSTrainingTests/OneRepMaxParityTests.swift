import Foundation
import Testing
@testable import FOSSTraining

@Suite("SPEC-05: One Rep Max Scientific Parity Tests")
struct OneRepMaxParityTests {

    @Test("1RM Parity: 1 Repetition returns lifted weight exactly without inflation")
    func testSingleRepetitionIdentity() {
        let weight = 100.0
        for formula in OneRepMaxFormula.allCases {
            let result = formula.calculate(weightKg: weight, repetitions: 1)
            #expect(result == 100.0, "Formula \(formula.displayName) should return 100.0 for 1 rep")
        }
    }

    @Test("1RM Parity: Zero or negative weight and reps return 0.0")
    func testInvalidInputsReturnZero() {
        for formula in OneRepMaxFormula.allCases {
            #expect(formula.calculate(weightKg: 0, repetitions: 5) == 0.0)
            #expect(formula.calculate(weightKg: -50, repetitions: 5) == 0.0)
            #expect(formula.calculate(weightKg: 100, repetitions: 0) == 0.0)
            #expect(formula.calculate(weightKg: 100, repetitions: -2) == 0.0)
        }
    }

    @Test("1RM Parity: 100kg at 5 reps matches canonical benchmarks")
    func test100kgAt5Reps() {
        let weight = 100.0
        let reps = 5

        // Epley: 100 * (1 + 5/30) = 116.67
        #expect(OneRepMaxFormula.epley.calculate(weightKg: weight, repetitions: reps) == 116.67)

        // Brzycki: 100 * (36 / (37 - 5)) = 112.50
        #expect(OneRepMaxFormula.brzycki.calculate(weightKg: weight, repetitions: reps) == 112.50)

        // Lombardi: 100 * 5^0.10 = 117.46
        #expect(OneRepMaxFormula.lombardi.calculate(weightKg: weight, repetitions: reps) == 117.46)

        // Mayhew: (100 * 100) / (52.2 + 41.9 * e^(-0.055 * 5)) = 119.01
        #expect(OneRepMaxFormula.mayhew.calculate(weightKg: weight, repetitions: reps) == 119.01)

        // O'Conner: 100 * (1 + 0.025 * 5) = 112.50
        #expect(OneRepMaxFormula.oconner.calculate(weightKg: weight, repetitions: reps) == 112.50)

        // Wathen: (100 * 100) / (48.8 + 53.8 * e^(-0.075 * 5)) = 116.58
        #expect(OneRepMaxFormula.wathen.calculate(weightKg: weight, repetitions: reps) == 116.58)
    }

    @Test("1RM Parity: 100kg at 10 reps matches canonical benchmarks")
    func test100kgAt10Reps() {
        let weight = 100.0
        let reps = 10

        // Epley: 100 * (1 + 10/30) = 133.33
        #expect(OneRepMaxFormula.epley.calculate(weightKg: weight, repetitions: reps) == 133.33)

        // Brzycki: 100 * (36 / (37 - 10)) = 133.33
        #expect(OneRepMaxFormula.brzycki.calculate(weightKg: weight, repetitions: reps) == 133.33)

        // Lombardi: 100 * 10^0.10 = 125.89
        #expect(OneRepMaxFormula.lombardi.calculate(weightKg: weight, repetitions: reps) == 125.89)

        // Mayhew: 130.93
        #expect(OneRepMaxFormula.mayhew.calculate(weightKg: weight, repetitions: reps) == 130.93)

        // O'Conner: 100 * (1 + 0.025 * 10) = 125.00
        #expect(OneRepMaxFormula.oconner.calculate(weightKg: weight, repetitions: reps) == 125.00)

        // Wathen: 134.75
        #expect(OneRepMaxFormula.wathen.calculate(weightKg: weight, repetitions: reps) == 134.75)
    }

    @Test("1RM Edge Case: Brzycki formula with repetitions >= 37 uses fallback")
    func testBrzyckiAsymptoteFallback() {
        let weight = 80.0
        let result37 = OneRepMaxFormula.brzycki.calculate(weightKg: weight, repetitions: 37)
        let result40 = OneRepMaxFormula.brzycki.calculate(weightKg: weight, repetitions: 40)
        #expect(result37 == 80.0 * 36.0)
        #expect(result40 == 80.0 * 36.0)
    }

    @Test("OneRepMaxCalculator: calculate single formula produces percentages")
    func testOneRepMaxCalculatorSingle() {
        let estimate = OneRepMaxCalculator.calculate(weightKg: 100.0, reps: 5, formula: .epley)
        #expect(estimate.formula == .epley)
        #expect(estimate.estimatedOneRepMax == 116.67)
        #expect(estimate.id == "EPLEY")
        #expect(estimate.percentages.count == 10)

        // Test 95% = 116.67 * 0.95 = 110.84
        let p95 = estimate.percentages.first { $0.percentage == 95 }
        #expect(p95 != nil)
        #expect(p95?.id == 95)
        #expect(p95?.weightKg == 110.84)

        // Test 50% = 116.67 * 0.50 = 58.34
        let p50 = estimate.percentages.first { $0.percentage == 50 }
        #expect(p50 != nil)
        #expect(p50?.weightKg == 58.34)
    }

    @Test("OneRepMaxCalculator: calculateAll returns estimates for all 6 formulas")
    func testOneRepMaxCalculatorAll() {
        let allEstimates = OneRepMaxCalculator.calculateAll(weightKg: 100.0, reps: 5)
        #expect(allEstimates.count == 6)
        let formulas = allEstimates.map(\.formula)
        #expect(formulas.contains(.epley))
        #expect(formulas.contains(.brzycki))
        #expect(formulas.contains(.lombardi))
        #expect(formulas.contains(.mayhew))
        #expect(formulas.contains(.oconner))
        #expect(formulas.contains(.wathen))
    }

    @Test("OneRepMaxEstimate: JSON encoding and decoding parity")
    func testEstimateJsonCodable() throws {
        let original = OneRepMaxCalculator.calculate(weightKg: 120.0, reps: 3, formula: .brzycki)
        let encoder = JSONEncoder()
        let data = try encoder.encode(original)

        let decoder = JSONDecoder()
        let decoded = try decoder.decode(OneRepMaxEstimate.self, from: data)
        #expect(decoded.formula == original.formula)
        #expect(decoded.estimatedOneRepMax == original.estimatedOneRepMax)
        #expect(decoded.percentages.count == original.percentages.count)

        // Test decoding Spring Boot format with dictionary percentages and estimated1Rm key
        let springBootJson = """
        {
            "formula": "EPLEY",
            "estimated1Rm": 105.5,
            "percentages": {
                "95": 100.2,
                "50": 52.8,
                "invalidKey": 10.0
            }
        }
        """.data(using: .utf8)!
        let decodedBackend = try decoder.decode(OneRepMaxEstimate.self, from: springBootJson)
        #expect(decodedBackend.formula == .epley)
        #expect(decodedBackend.estimatedOneRepMax == 105.5)
        #expect(decodedBackend.percentages.count == 2)
        #expect(decodedBackend.percentages[0].percentage == 95)
        #expect(decodedBackend.percentages[1].percentage == 50)

        // Test decoding with missing percentages and missing estimated1Rm fallback
        let minimalJson = """
        {
            "formula": "WATHO"
        }
        """
        #expect(throws: Error.self) {
            _ = try decoder.decode(OneRepMaxEstimate.self, from: minimalJson.data(using: .utf8)!)
        }

        let defaultJson = """
        {
            "formula": "LOMBARDI"
        }
        """.data(using: .utf8)!
        let decodedDefault = try decoder.decode(OneRepMaxEstimate.self, from: defaultJson)
        #expect(decodedDefault.formula == .lombardi)
        #expect(decodedDefault.estimatedOneRepMax == 0.0)
        #expect(decodedDefault.percentages.isEmpty)
    }
}
