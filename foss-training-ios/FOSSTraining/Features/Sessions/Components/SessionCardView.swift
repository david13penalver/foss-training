import SwiftUI

public struct SessionCardView: View {
    @Environment(\.theme) private var theme
    public let session: Session
    public let onStart: () -> Void
    public let onEdit: () -> Void
    public let onClone: () -> Void
    public let onDelete: () -> Void

    public init(
        session: Session,
        onStart: @escaping () -> Void,
        onEdit: @escaping () -> Void,
        onClone: @escaping () -> Void,
        onDelete: @escaping () -> Void
    ) {
        self.session = session
        self.onStart = onStart
        self.onEdit = onEdit
        self.onClone = onClone
        self.onDelete = onDelete
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header Row: Title & Duration Badge
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(session.name)
                        .font(.headline)
                        .foregroundStyle(Color.primary)

                    if let desc = session.description, !desc.isEmpty {
                        Text(desc)
                            .font(.subheadline)
                            .foregroundStyle(Color.secondary)
                            .lineLimit(2)
                    }
                }

                Spacer()

                HStack(spacing: 4) {
                    Image(systemName: "clock")
                        .font(.caption2)
                    Text("\(session.estimatedDurationMinutes ?? 60) min")
                        .font(.caption)
                        .fontWeight(.medium)
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(theme.selectedAccent.color.opacity(0.15))
                .foregroundStyle(theme.selectedAccent.color)
                .clipShape(Capsule())
            }

            // Exercises Summary
            if !session.exercises.isEmpty {
                let exerciseNames = session.exercises.map(\.exerciseName).prefix(4).joined(separator: " • ")
                let suffix = session.exercises.count > 4 ? " +\(session.exercises.count - 4) more" : ""
                Text("\(exerciseNames)\(suffix)")
                    .font(.caption)
                    .foregroundStyle(Color.secondary)
                    .lineLimit(1)

                HStack(spacing: 12) {
                    Label("\(session.exercises.count) exercises", systemImage: "dumbbell")
                    Label("\(session.totalSetsCount) sets", systemImage: "repeat")
                }
                .font(.caption2)
                .foregroundStyle(Color.secondary)
            } else {
                Text("No exercises added yet")
                    .font(.caption)
                    .foregroundStyle(Color.secondary)
            }

            Divider()
                .overlay(Color.white.opacity(0.1))

            // Action Row: "Start This Workout" CTA & More Menu
            HStack {
                Button(action: onStart) {
                    HStack(spacing: 6) {
                        Image(systemName: "play.fill")
                        Text("Start Workout")
                            .fontWeight(.semibold)
                    }
                    .font(.subheadline)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 8)
                    .background(theme.selectedAccent.color)
                    .foregroundStyle(theme.selectedAccent.badgeTextColor)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                }
                .buttonStyle(.plain)

                Spacer()

                Menu {
                    Button(action: onEdit) {
                        Label("Edit Template", systemImage: "pencil")
                    }
                    Button(action: onClone) {
                        Label("Duplicate (Copy)", systemImage: "doc.on.doc")
                    }
                    Divider()
                    Button(role: .destructive, action: onDelete) {
                        Label("Delete Template", systemImage: "trash")
                    }
                } label: {
                    Image(systemName: "ellipsis.circle")
                        .font(.title3)
                        .foregroundStyle(Color.secondary)
                        .padding(6)
                }
            }
        }
        .padding(14)
        .background(theme.surfaceStyle.cardBackgroundColor)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.white.opacity(0.08), lineWidth: 1)
        )
    }
}
