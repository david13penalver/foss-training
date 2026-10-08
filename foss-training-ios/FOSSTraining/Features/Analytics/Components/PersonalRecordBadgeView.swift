import SwiftUI

public struct PersonalRecordBadgeView: View {
    @Environment(\.theme) private var theme
    public let record: PersonalRecord

    public init(record: PersonalRecord) {
        self.record = record
    }

    private var badgeColor: Color {
        switch record.recordType {
        case .maxWeight:
            return Color(red: 1.0, green: 0.84, blue: 0.0) // Gold
        case .maxEstimated1RM:
            return Color(red: 0.95, green: 0.65, blue: 0.15) // Amber Gold
        case .maxVolume:
            return Color(red: 0.75, green: 0.75, blue: 0.75) // Silver
        case .maxReps:
            return Color(red: 0.80, green: 0.50, blue: 0.20) // Bronze
        }
    }

    private var iconName: String {
        switch record.recordType {
        case .maxWeight:
            return "trophy.fill"
        case .maxEstimated1RM:
            return "flame.fill"
        case .maxVolume:
            return "chart.bar.fill"
        case .maxReps:
            return "repeat"
        }
    }

    private var formattedValue: String {
        switch record.recordType {
        case .maxReps:
            return "\(Int(record.value)) reps"
        case .maxVolume:
            return "\(Int(record.value)) \(record.unit)"
        case .maxWeight, .maxEstimated1RM:
            return "\(String(format: "%.1f", record.value)) \(record.unit)"
        }
    }

    public var body: some View {
        HStack(spacing: 12) {
            // Trophy / Medal Icon
            ZStack {
                Circle()
                    .fill(badgeColor.opacity(0.18))
                    .frame(width: 44, height: 44)

                Image(systemName: iconName)
                    .font(.title3)
                    .foregroundStyle(badgeColor)
            }

            // Record Info
            VStack(alignment: .leading, spacing: 3) {
                Text(record.exerciseName)
                    .font(.subheadline.weight(.semibold))
                    .lineLimit(1)

                Text(record.recordType.displayName)
                    .font(.caption2)
                    .foregroundStyle(.secondary)

                Text(record.achievedDate, format: .dateTime.month(.abbreviated).day().year())
                    .font(.caption2)
                    .foregroundStyle(.tertiary)
            }

            Spacer()

            // Value Display
            Text(formattedValue)
                .font(.callout.monospacedDigit().weight(.bold))
                .foregroundStyle(badgeColor)
        }
        .padding(12)
        .background(theme.surfaceStyle.backgroundColor)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(badgeColor.opacity(0.3), lineWidth: 1)
        )
    }
}

public struct PersonalRecordsCard: View {
    @Environment(\.theme) private var theme
    public let records: [PersonalRecord]

    public init(records: [PersonalRecord]) {
        self.records = records
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                Text("Personal Records")
                    .font(.headline)
                Spacer()
                Text("\(records.count) Records")
                    .themedBadge()
            }

            if records.isEmpty {
                VStack(spacing: 8) {
                    Image(systemName: "trophy")
                        .font(.largeTitle)
                        .foregroundStyle(.secondary)
                    Text("No personal records recorded yet")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                    Text("Complete workouts to automatically track milestones.")
                        .font(.caption)
                        .foregroundStyle(.tertiary)
                }
                .frame(maxWidth: .infinity, minHeight: 120)
            } else {
                VStack(spacing: 8) {
                    ForEach(records) { record in
                        PersonalRecordBadgeView(record: record)
                    }
                }
            }
        }
        .themedCard()
    }
}
