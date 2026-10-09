import SwiftUI

public struct ProgramAdherenceCard: View {
    @Environment(\.theme) private var theme
    public let adherence: ProgramAdherence

    public init(adherence: ProgramAdherence) {
        self.adherence = adherence
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .center, spacing: 16) {
                // Circular Progress Ring
                ZStack {
                    Circle()
                        .stroke(Color.white.opacity(0.1), lineWidth: 8)
                        .frame(width: 72, height: 72)

                    Circle()
                        .trim(from: 0, to: CGFloat(min(max(adherence.overallCompletionRate / 100.0, 0.0), 1.0)))
                        .stroke(
                            theme.selectedAccent.color,
                            style: StrokeStyle(lineWidth: 8, lineCap: .round)
                        )
                        .rotationEffect(.degrees(-90))
                        .frame(width: 72, height: 72)
                        .animation(.easeInOut, value: adherence.overallCompletionRate)

                    VStack(spacing: 2) {
                        Text("\(Int(adherence.overallCompletionRate))%")
                            .font(.system(.subheadline, design: .rounded, weight: .bold))
                        Text("done")
                            .font(.system(size: 10))
                            .foregroundStyle(.secondary)
                    }
                }

                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text(adherence.status.displayName)
                            .themedBadge()

                        if adherence.currentStreak > 0 {
                            HStack(spacing: 4) {
                                Image(systemName: "flame.fill")
                                    .foregroundStyle(.orange)
                                Text("\(adherence.currentStreak) streak")
                                    .font(.caption.weight(.bold))
                                    .foregroundStyle(.orange)
                            }
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.orange.opacity(0.15))
                            .clipShape(Capsule())
                        }
                    }

                    Text(adherence.statusDescription)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                        .fixedSize(horizontal: false, vertical: true)
                }
            }

            Divider()
                .overlay(Color.white.opacity(0.08))

            // Metric Counters Row
            HStack {
                metricItem(
                    label: "Scheduled",
                    value: "\(adherence.totalScheduledWorkouts)",
                    systemImage: "calendar"
                )
                Spacer()
                metricItem(
                    label: "Completed",
                    value: "\(adherence.completedWorkouts)",
                    systemImage: "checkmark.circle.fill",
                    color: .green
                )
                Spacer()
                metricItem(
                    label: "Missed",
                    value: "\(adherence.missedWorkouts)",
                    systemImage: "xmark.circle.fill",
                    color: adherence.missedWorkouts > 0 ? .red : .secondary
                )
                Spacer()
                metricItem(
                    label: "Longest",
                    value: "\(adherence.longestStreak)",
                    systemImage: "trophy.fill",
                    color: .yellow
                )
            }
        }
        .themedCard()
    }

    private func metricItem(label: String, value: String, systemImage: String, color: Color = .primary) -> some View {
        VStack(spacing: 4) {
            Image(systemName: systemImage)
                .font(.caption)
                .foregroundStyle(color)

            Text(value)
                .font(.system(.headline, design: .rounded, weight: .bold))
                .foregroundStyle(.primary)

            Text(label)
                .font(.caption2)
                .foregroundStyle(.secondary)
        }
    }
}
