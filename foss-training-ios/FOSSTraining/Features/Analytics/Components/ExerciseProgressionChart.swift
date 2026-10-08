import SwiftUI
import Charts

public struct ExerciseProgressionChart: View {
    @Environment(\.theme) private var theme
    public let progression: ExerciseProgression
    @State private var selectedPoint: ProgressionDataPoint?
    @State private var rawSelectedDate: Date?

    public init(progression: ExerciseProgression) {
        self.progression = progression
    }

    private var trendColor: Color {
        switch progression.trend {
        case .improving:
            return .green
        case .stagnant:
            return .yellow
        case .declining:
            return .red
        case .insufficientData:
            return .secondary
        }
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Header with stats
            VStack(alignment: .leading, spacing: 6) {
                HStack(alignment: .firstTextBaseline) {
                    Text(progression.exerciseName)
                        .font(.headline)
                    Spacer()
                    Text(progression.trend.displayName)
                        .font(.caption.weight(.bold))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 4)
                        .background(trendColor.opacity(0.18))
                        .foregroundStyle(trendColor)
                        .clipShape(Capsule())
                }

                HStack(spacing: 12) {
                    HStack(spacing: 4) {
                        Text("Gain:")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        Text(String(format: "%+.1f kg (%+.1f%%)", progression.absolute1RmGainKg, progression.relative1RmGainPercentage))
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(progression.relative1RmGainPercentage >= 0 ? .green : .red)
                    }

                    Spacer()

                    HStack(spacing: 4) {
                        Text("Best 1RM:")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        Text(String(format: "%.1f kg", progression.allTimeBest1RmKg))
                            .font(.caption.weight(.bold))
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                }
            }

            // Interactive scrubber readout if selected
            if let selected = selectedPoint {
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(selected.date, format: .dateTime.month().day().year())
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                        Text("Est 1RM: \(String(format: "%.1f", selected.estimatedOneRepMax)) kg")
                            .font(.subheadline.monospacedDigit().weight(.bold))
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                    Spacer()
                    VStack(alignment: .trailing, spacing: 2) {
                        Text("Top Set: \(String(format: "%.1f", selected.topWeightKg)) kg × \(selected.topWeightReps)")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        Text("Vol: \(Int(selected.totalVolumeKg)) kg")
                            .font(.caption2.monospacedDigit())
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(10)
                .background(theme.surfaceStyle.backgroundColor.opacity(0.8))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }

            // Swift Chart
            if progression.dataPoints.isEmpty {
                VStack(spacing: 8) {
                    Image(systemName: "chart.line.uptrend.xyaxis")
                        .font(.largeTitle)
                        .foregroundStyle(.secondary)
                    Text("No progression data points recorded yet")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .frame(maxWidth: .infinity, minHeight: 200)
            } else {
                Chart {
                    ForEach(progression.dataPoints) { point in
                        AreaMark(
                            x: .value("Date", point.date),
                            y: .value("Est 1RM", point.estimatedOneRepMax)
                        )
                        .interpolationMethod(.catmullRom)
                        .foregroundStyle(
                            LinearGradient(
                                colors: [theme.selectedAccent.color.opacity(0.35), theme.selectedAccent.color.opacity(0.02)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )

                        LineMark(
                            x: .value("Date", point.date),
                            y: .value("Est 1RM", point.estimatedOneRepMax)
                        )
                        .interpolationMethod(.catmullRom)
                        .foregroundStyle(theme.selectedAccent.color)
                        .lineStyle(StrokeStyle(lineWidth: 2.5))

                        PointMark(
                            x: .value("Date", point.date),
                            y: .value("Est 1RM", point.estimatedOneRepMax)
                        )
                        .foregroundStyle(theme.selectedAccent.color)
                        .symbolSize(selectedPoint?.id == point.id ? 80 : 36)

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
                    selectedPoint = progression.dataPoints.min(by: {
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
                .frame(height: 220)
            }

            // Footer note
            Text("Calculated via \(progression.formula.displayName) formula across \(progression.totalSessions) sessions.")
                .font(.caption2)
                .foregroundStyle(.secondary)
        }
        .themedCard()
    }
}
