import SwiftUI
import Charts

public struct HypertrophyVolumeChart: View {
    @Environment(\.theme) private var theme
    public let weeklyVolume: WeeklyMuscleVolume

    public init(weeklyVolume: WeeklyMuscleVolume) {
        self.weeklyVolume = weeklyVolume
    }

    private func colorForStatus(_ status: HypertrophyVolumeStatus) -> Color {
        switch status {
        case .belowMev:
            return .blue
        case .maintenance:
            return .yellow
        case .adaptive:
            return .green
        case .approachingMrv:
            return .orange
        case .exceededMrv:
            return .red
        }
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Header & Landmark Legend
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("Hypertrophy Volume Landmarks")
                        .font(.headline)
                    Spacer()
                    Text("\(weeklyVolume.totalWorkingSets) Total Sets")
                        .themedBadge()
                }

                Text("Direct + 0.5× indirect synergist sets per muscle group")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            // Swift Chart
            if weeklyVolume.muscleVolumes.isEmpty || weeklyVolume.muscleVolumes.allSatisfy({ $0.effectiveSets == 0 }) {
                VStack(spacing: 8) {
                    Image(systemName: "chart.bar.xaxis")
                        .font(.largeTitle)
                        .foregroundStyle(.secondary)
                    Text("No volume logged for this week")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .frame(maxWidth: .infinity, minHeight: 180)
            } else {
                Chart {
                    ForEach(weeklyVolume.muscleVolumes) { item in
                        BarMark(
                            x: .value("Effective Sets", item.effectiveSets),
                            y: .value("Muscle", item.muscleGroupName)
                        )
                        .foregroundStyle(colorForStatus(item.status))
                        .annotation(position: .trailing, alignment: .leading) {
                            Text(String(format: "%.1f", item.effectiveSets))
                                .font(.caption2.monospacedDigit())
                                .foregroundStyle(.secondary)
                        }
                    }

                    RuleMark(x: .value("MEV Threshold", 10.0))
                        .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 4]))
                        .foregroundStyle(Color.green.opacity(0.6))

                    RuleMark(x: .value("MAV Ceiling", 20.0))
                        .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 4]))
                        .foregroundStyle(Color.orange.opacity(0.6))
                }
                .chartXAxis {
                    AxisMarks(position: .bottom)
                }
                .chartYAxis {
                    AxisMarks(position: .leading)
                }
                .frame(height: max(220, CGFloat(weeklyVolume.muscleVolumes.count) * 28))
            }

            // Landmark Legend Pills
            HStack(spacing: 8) {
                legendItem(title: "Sub-MEV (<6)", color: .blue)
                legendItem(title: "Maint (6-9)", color: .yellow)
                legendItem(title: "Adaptive (10-20)", color: .green)
                legendItem(title: "MRV (>25)", color: .red)
            }
            .font(.caption2)

            // Balance Ratios
            HStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Push : Pull")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                    Text(String(format: "%.2f", weeklyVolume.pushPullRatio))
                        .font(.subheadline.weight(.semibold))
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                Divider()

                VStack(alignment: .leading, spacing: 2) {
                    Text("Upper : Lower")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                    Text(String(format: "%.2f", weeklyVolume.upperLowerRatio))
                        .font(.subheadline.weight(.semibold))
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                Divider()

                VStack(alignment: .leading, spacing: 2) {
                    Text("Total Tonnage")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                    Text("\(Int(weeklyVolume.totalVolumeKg)) kg")
                        .font(.subheadline.weight(.semibold))
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(.top, 4)
        }
        .themedCard()
    }

    private func legendItem(title: String, color: Color) -> some View {
        HStack(spacing: 4) {
            Circle()
                .fill(color)
                .frame(width: 6, height: 6)
            Text(title)
                .foregroundStyle(.secondary)
        }
    }
}
