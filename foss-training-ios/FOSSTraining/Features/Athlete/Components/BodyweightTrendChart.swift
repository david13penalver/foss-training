import SwiftUI
import Charts

public struct BodyweightTrendChart: View {
    @Environment(\.theme) private var theme
    public let trendPoints: [BodyweightTrendPoint]
    @State private var selectedPoint: BodyweightTrendPoint?
    @State private var rawSelectedDate: Date?

    public init(trendPoints: [BodyweightTrendPoint]) {
        self.trendPoints = trendPoints
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Bodyweight Trend (7-Day EMA)")
                        .font(.headline)
                    Text("Exponential Moving Average filters daily water weight noise")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                Spacer()
                if let latest = trendPoints.last {
                    VStack(alignment: .trailing, spacing: 2) {
                        Text("\(String(format: "%.1f", latest.movingAverageKg)) kg")
                            .font(.title3.monospacedDigit().weight(.bold))
                            .foregroundStyle(theme.selectedAccent.color)
                        Text("7d EMA")
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                    }
                }
            }

            // Scrubber Readout
            if let selected = selectedPoint {
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(selected.date, format: .dateTime.month(.abbreviated).day().year())
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                        if let notes = selected.notes, !notes.isEmpty {
                            Text(notes)
                                .font(.caption2.italic())
                                .foregroundStyle(.tertiary)
                        }
                    }
                    Spacer()
                    VStack(alignment: .trailing, spacing: 2) {
                        Text("Raw: \(String(format: "%.1f", selected.weightKg)) kg")
                            .font(.subheadline.monospacedDigit().weight(.semibold))
                        Text("EMA: \(String(format: "%.1f", selected.movingAverageKg)) kg")
                            .font(.caption2.monospacedDigit())
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                }
                .padding(10)
                .background(theme.surfaceStyle.backgroundColor.opacity(0.8))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }

            // Swift Chart
            if trendPoints.isEmpty {
                VStack(spacing: 8) {
                    Image(systemName: "scalemass")
                        .font(.largeTitle)
                        .foregroundStyle(.secondary)
                    Text("No bodyweight entries recorded yet")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                    Text("Log daily morning weights to see your smoothed trend curve.")
                        .font(.caption)
                        .foregroundStyle(.tertiary)
                }
                .frame(maxWidth: .infinity, minHeight: 180)
            } else {
                Chart {
                    ForEach(trendPoints) { point in
                        // Raw weigh-in scatter dot
                        PointMark(
                            x: .value("Date", point.date),
                            y: .value("Weight", point.weightKg)
                        )
                        .foregroundStyle(Color.secondary.opacity(0.45))
                        .symbolSize(selectedPoint?.id == point.id ? 50 : 25)

                        // 7-day EMA smooth curve
                        LineMark(
                            x: .value("Date", point.date),
                            y: .value("EMA", point.movingAverageKg)
                        )
                        .interpolationMethod(.catmullRom)
                        .foregroundStyle(theme.selectedAccent.color)
                        .lineStyle(StrokeStyle(lineWidth: 2.5))

                        if let selected = selectedPoint, selected.id == point.id {
                            RuleMark(x: .value("Selected Date", point.date))
                                .foregroundStyle(theme.selectedAccent.color.opacity(0.5))
                                .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 4]))
                        }
                    }
                }
                .chartXSelection(value: $rawSelectedDate)
                .onChange(of: rawSelectedDate) { _, newDate in
                    guard let newDate else {
                        selectedPoint = nil
                        return
                    }
                    selectedPoint = trendPoints.min(by: {
                        abs($0.date.timeIntervalSince(newDate)) < abs($1.date.timeIntervalSince(newDate))
                    })
                }
                .chartXAxis {
                    AxisMarks(values: .automatic(desiredCount: 5)) { _ in
                        AxisGridLine()
                        AxisTick()
                        AxisValueLabel(format: .dateTime.month(.abbreviated).day())
                    }
                }
                .chartYAxis {
                    AxisMarks(position: .leading)
                }
                .frame(height: 200)
            }

            // Legend
            HStack(spacing: 16) {
                HStack(spacing: 6) {
                    Circle()
                        .fill(Color.secondary.opacity(0.6))
                        .frame(width: 8, height: 8)
                    Text("Daily Weigh-ins")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }

                HStack(spacing: 6) {
                    RoundedRectangle(cornerRadius: 1)
                        .fill(theme.selectedAccent.color)
                        .frame(width: 14, height: 3)
                    Text("7-Day Moving Avg")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }
            }
        }
        .themedCard()
    }
}
