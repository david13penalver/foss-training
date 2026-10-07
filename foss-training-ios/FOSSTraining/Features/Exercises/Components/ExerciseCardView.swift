import SwiftUI

public struct ExerciseCardView: View {
    @Environment(\.theme) private var theme
    public let exercise: Exercise

    public init(exercise: Exercise) {
        self.exercise = exercise
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .center) {
                Label(exercise.primaryCategory.displayName, systemImage: exercise.primaryCategory.systemIcon)
                    .themedBadge()

                Spacer()

                Text(exercise.difficultyLevel.displayName)
                    .font(.caption2.weight(.bold))
                    .foregroundStyle(.secondary)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 3)
                    .background(Color.secondary.opacity(0.12))
                    .clipShape(Capsule())
            }

            Text(exercise.name)
                .font(.headline.weight(.semibold))
                .foregroundStyle(.primary)
                .lineLimit(1)

            HStack(spacing: 8) {
                if let muscle = exercise.primaryMuscleGroup {
                    Label(muscle, systemImage: "figure.walk")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }

                if let pattern = exercise.movementPattern {
                    Label(pattern.displayName, systemImage: "arrow.triangle.swap")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }

                Spacer()

                if !exercise.equipmentRequired.isEmpty {
                    Text(exercise.equipmentRequired.first?.displayName ?? "")
                        .font(.caption2)
                        .foregroundStyle(theme.selectedAccent.color)
                }
            }
        }
        .themedCard()
    }
}
