import SwiftUI

public struct AcwrGaugeCard: View {
    @Environment(\.theme) private var theme
    public let workloadRatio: WorkloadRatio

    public init(workloadRatio: WorkloadRatio) {
        self.workloadRatio = workloadRatio
    }

    private var zoneColor: Color {
        switch workloadRatio.riskZone {
        case .low:
            return .blue
        case .optimal:
            return .green
        case .caution:
            return .orange
        case .high:
            return .red
        }
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Workload Ratio (ACWR)")
                        .font(.headline)
                    Text("Acute (7d) vs Chronic (28d) training balance")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                Spacer()
                Text(workloadRatio.riskZone.displayName)
                    .font(.caption.weight(.bold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(zoneColor.opacity(0.18))
                    .foregroundStyle(zoneColor)
                    .clipShape(Capsule())
            }

            // Gauge & Value
            VStack(spacing: 8) {
                HStack(alignment: .firstTextBaseline) {
                    Text(String(format: "%.2f", workloadRatio.acwr))
                        .font(.system(size: 40, weight: .bold, design: .rounded))
                        .foregroundStyle(zoneColor)
                    Text("ratio")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                    Spacer()
                }

                Gauge(value: min(2.0, max(0.0, workloadRatio.acwr)), in: 0.0...2.0) {
                    Text("ACWR")
                } currentValueLabel: {
                    Text(String(format: "%.2f", workloadRatio.acwr))
                }
                .gaugeStyle(.accessoryLinearCapacity)
                .tint(Gradient(colors: [.blue, .green, .orange, .red]))
            }

            // Load Metrics Breakdown
            HStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Acute (7d)")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                    Text("\(Int(workloadRatio.acuteWorkload)) AU")
                        .font(.subheadline.weight(.semibold))
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                Divider()

                VStack(alignment: .leading, spacing: 2) {
                    Text("Chronic (28d Avg)")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                    Text("\(Int(workloadRatio.chronicWeeklyAverage)) AU")
                        .font(.subheadline.weight(.semibold))
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                Divider()

                VStack(alignment: .leading, spacing: 2) {
                    Text("Deload")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                    Text(workloadRatio.deloadRecommended ? "Recommended" : "Not Needed")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(workloadRatio.deloadRecommended ? .red : .primary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(.vertical, 8)

            // Recommendation Text
            HStack(alignment: .top, spacing: 8) {
                Image(systemName: workloadRatio.deloadRecommended ? "exclamationmark.triangle.fill" : "info.circle.fill")
                    .foregroundStyle(zoneColor)
                    .font(.caption)
                    .padding(.top, 2)
                Text(workloadRatio.recommendation)
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
        .themedCard()
    }
}
