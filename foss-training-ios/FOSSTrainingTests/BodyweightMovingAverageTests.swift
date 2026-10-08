import Foundation
import Testing
@testable import FOSSTraining

@Suite("Bodyweight Exponential Moving Average Tests")
struct BodyweightMovingAverageTests {

    @Test("Moving Average: empty entries returns empty array")
    func testEmptyEntries() {
        let result = BodyweightMovingAverageCalculator.calculateTrend(entries: [])
        #expect(result.isEmpty)
    }

    @Test("Moving Average: single entry equals the raw weight")
    func testSingleEntry() {
        let entry = BodyweightEntry(id: 1, weightKg: 82.5, measuredDate: Date(), notes: "Morning")
        let result = BodyweightMovingAverageCalculator.calculateTrend(entries: [entry])

        #expect(result.count == 1)
        #expect(result[0].weightKg == 82.5)
        #expect(result[0].movingAverageKg == 82.5)
        #expect(result[0].entryId == 1)
        #expect(result[0].notes == "Morning")
        #expect(result[0].id == entry.measuredDate)
    }

    @Test("Moving Average: constant weight sequence maintains identical EMA")
    func testConstantWeightSequence() {
        let now = Date()
        let entries = (0..<7).map { i in
            BodyweightEntry(
                id: i + 1,
                weightKg: 80.0,
                measuredDate: now.addingTimeInterval(Double(i) * 86400)
            )
        }

        let trend = BodyweightMovingAverageCalculator.calculateTrend(entries: entries, windowSize: 7)
        #expect(trend.count == 7)
        for point in trend {
            #expect(point.movingAverageKg == 80.0)
        }
    }

    @Test("Moving Average: outlier spike is smoothed by 7-day EMA alpha=0.25")
    func testOutlierSpikeSmoothing() {
        let now = Date()
        // Day 0: 80.0, Day 1: 80.0, Day 2: 84.0 (spike), Day 3: 80.0
        let entries = [
            BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: now),
            BodyweightEntry(id: 2, weightKg: 80.0, measuredDate: now.addingTimeInterval(86400)),
            BodyweightEntry(id: 3, weightKg: 84.0, measuredDate: now.addingTimeInterval(86400 * 2)),
            BodyweightEntry(id: 4, weightKg: 80.0, measuredDate: now.addingTimeInterval(86400 * 3))
        ]

        let trend = BodyweightMovingAverageCalculator.calculateTrend(entries: entries, windowSize: 7)
        #expect(trend.count == 4)

        // Day 2 EMA: alpha * 84 + (1 - alpha) * 80 = 0.25 * 84 + 0.75 * 80 = 21 + 60 = 81.0
        #expect(trend[2].movingAverageKg == 81.0)
        // Day 3 EMA: 0.25 * 80 + 0.75 * 81 = 20 + 60.75 = 80.75
        #expect(trend[3].movingAverageKg == 80.75)
    }

    @Test("Moving Average: sorts out-of-order entries chronologically and handles custom window size")
    func testSortingAndCustomWindow() throws {
        let now = Date()
        let entries = [
            BodyweightEntry(id: 2, weightKg: 82.0, measuredDate: now.addingTimeInterval(86400)),
            BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: now)
        ]

        let trend = BodyweightMovingAverageCalculator.calculateTrend(entries: entries, windowSize: 0) // windowSize <= 0 clamps to 1
        #expect(trend.count == 2)
        #expect(trend[0].entryId == 1)
        #expect(trend[1].entryId == 2)

        // BodyweightTrendPoint Codable
        let point = trend[0]
        let data = try JSONEncoder().encode(point)
        let decoded = try JSONDecoder().decode(BodyweightTrendPoint.self, from: data)
        #expect(decoded.weightKg == point.weightKg)
        #expect(decoded.movingAverageKg == point.movingAverageKg)
    }
}
