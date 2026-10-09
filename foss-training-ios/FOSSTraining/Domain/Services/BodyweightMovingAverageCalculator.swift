import Foundation

public struct BodyweightTrendPoint: Identifiable, Codable, Hashable, Sendable {
    public var id: Date { date }
    public let entryId: Int?
    public let date: Date
    public let weightKg: Double
    public let movingAverageKg: Double
    public let notes: String?

    public init(
        entryId: Int? = nil,
        date: Date,
        weightKg: Double,
        movingAverageKg: Double,
        notes: String? = nil
    ) {
        self.entryId = entryId
        self.date = date
        self.weightKg = weightKg
        self.movingAverageKg = movingAverageKg
        self.notes = notes
    }
}

public enum BodyweightMovingAverageCalculator {
    /// Computes 7-day Exponential Moving Average (alpha = 2 / (7 + 1) = 0.25) across chronological entries
    public static func calculateTrend(
        entries: [BodyweightEntry],
        windowSize: Int = 7
    ) -> [BodyweightTrendPoint] {
        guard !entries.isEmpty else { return [] }

        let sorted = entries.sorted { $0.measuredDate < $1.measuredDate }
        let effectiveWindow = max(1, windowSize)
        let alpha = 2.0 / Double(effectiveWindow + 1)

        var points: [BodyweightTrendPoint] = []
        var currentEMA: Double = 0.0

        for (index, entry) in sorted.enumerated() {
            if index == 0 {
                currentEMA = entry.weightKg
            } else {
                currentEMA = (alpha * entry.weightKg) + ((1.0 - alpha) * currentEMA)
            }

            let roundedEMA = (currentEMA * 100.0).rounded() / 100.0
            points.append(
                BodyweightTrendPoint(
                    entryId: entry.id,
                    date: entry.measuredDate,
                    weightKg: entry.weightKg,
                    movingAverageKg: roundedEMA,
                    notes: entry.notes
                )
            )
        }

        return points
    }
}
