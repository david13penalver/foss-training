import SwiftUI

public struct WorkoutHistoryCardView: View {
    @Environment(\.theme) private var theme
    let training: Training
    let onOpen: () -> Void

    public init(training: Training, onOpen: @escaping () -> Void) {
        self.training = training
        self.onOpen = onOpen
    }

    public var body: some View {
        Button(action: onOpen) {
            VStack(alignment: .leading, spacing: 10) {
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(training.name)
                            .font(.headline)
                            .foregroundStyle(.primary)

                        Text(training.trainingDate.formatted(date: .abbreviated, time: .omitted))
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }

                    Spacer()

                    TrainingStatusBadge(status: training.status)
                }

                HStack {
                    Label("\(training.totalCompletedSets)/\(training.totalSets) sets", systemImage: "checklist")

                    Spacer()

                    if training.durationSeconds > 0 {
                        Label(training.formattedDuration, systemImage: "clock")
                    }

                    Spacer()

                    if training.totalVolumeKg > 0 {
                        Text("\(Int(training.totalVolumeKg)) kg")
                            .font(.caption.weight(.bold))
                            .foregroundStyle(theme.selectedAccent.color)
                    }
                }
                .font(.caption)
                .foregroundStyle(.secondary)
            }
            .padding(.vertical, 6)
        }
        .buttonStyle(.plain)
    }
}

public struct TrainingStatusBadge: View {
    @Environment(\.theme) private var theme
    let status: TrainingStatus

    public init(status: TrainingStatus) {
        self.status = status
    }

    public var body: some View {
        Text(status.displayName)
            .font(.caption2.weight(.bold))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(bgColor)
            .foregroundStyle(fgColor)
            .clipShape(Capsule())
    }

    private var bgColor: Color {
        switch status {
        case .inProgress: return theme.selectedAccent.color.opacity(0.2)
        case .completed: return Color.green.opacity(0.2)
        case .paused: return Color.orange.opacity(0.2)
        case .cancelled: return Color.red.opacity(0.2)
        default: return Color.secondary.opacity(0.2)
        }
    }

    private var fgColor: Color {
        switch status {
        case .inProgress: return theme.selectedAccent.color
        case .completed: return Color.green
        case .paused: return Color.orange
        case .cancelled: return Color.red
        default: return Color.secondary
        }
    }
}
