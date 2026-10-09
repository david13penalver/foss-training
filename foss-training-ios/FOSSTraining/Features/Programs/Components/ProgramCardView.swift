import SwiftUI

public struct ProgramCardView: View {
    @Environment(\.theme) private var theme
    public let program: TrainingProgram
    public let adherence: ProgramAdherence?
    public let onClone: () -> Void
    public let onDelete: () -> Void

    public init(
        program: TrainingProgram,
        adherence: ProgramAdherence? = nil,
        onClone: @escaping () -> Void,
        onDelete: @escaping () -> Void
    ) {
        self.program = program
        self.adherence = adherence
        self.onClone = onClone
        self.onDelete = onDelete
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(program.name)
                        .font(.headline)
                        .foregroundStyle(.primary)

                    if let desc = program.description, !desc.isEmpty {
                        Text(desc)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .lineLimit(2)
                    }
                }

                Spacer()

                Menu {
                    Button(action: onClone) {
                        Label("Clone Program", systemImage: "doc.on.doc")
                    }
                    Button(role: .destructive, action: onDelete) {
                        Label("Delete Program", systemImage: "trash")
                    }
                } label: {
                    Image(systemName: "ellipsis.circle")
                        .font(.title3)
                        .foregroundStyle(.secondary)
                        .frame(width: 32, height: 32)
                }
            }

            // Chips row: Level, Periodization, Duration, Frequency
            HStack(spacing: 8) {
                Text(program.level.displayName)
                    .themedBadge()

                Text(program.periodizationType.displayName)
                    .font(.caption.weight(.medium))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.white.opacity(0.08))
                    .clipShape(Capsule())

                Text("\(program.durationWeeks) W")
                    .font(.caption.weight(.semibold))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.white.opacity(0.08))
                    .clipShape(Capsule())

                Text("\(program.workouts.count) d/wk")
                    .font(.caption.weight(.semibold))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.white.opacity(0.08))
                    .clipShape(Capsule())
            }

            // Adherence status chip if available
            if let adh = adherence {
                HStack(spacing: 8) {
                    Image(systemName: adherenceIcon(adh.status))
                        .foregroundStyle(adherenceColor(adh.status))
                    Text("\(adh.status.displayName) • \(String(format: "%.0f%%", adh.overallCompletionRate)) completed")
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    if adh.currentStreak > 0 {
                        Spacer()
                        HStack(spacing: 2) {
                            Image(systemName: "flame.fill")
                                .foregroundStyle(.orange)
                            Text("\(adh.currentStreak)")
                                .font(.caption.weight(.bold))
                                .foregroundStyle(.orange)
                        }
                    }
                }
                .padding(.top, 4)
            }
        }
        .themedCard()
    }

    private func adherenceIcon(_ status: ProgramAdherenceStatus) -> String {
        switch status {
        case .completed: return "checkmark.seal.fill"
        case .onTrack: return "chart.line.uptrend.xyaxis"
        case .behindSchedule: return "clock.badge.exclamationmark"
        case .atRisk: return "exclamationmark.triangle.fill"
        case .notStarted: return "circle.dashed"
        }
    }

    private func adherenceColor(_ status: ProgramAdherenceStatus) -> Color {
        switch status {
        case .completed: return .green
        case .onTrack: return .blue
        case .behindSchedule: return .yellow
        case .atRisk: return .red
        case .notStarted: return .secondary
        }
    }
}
