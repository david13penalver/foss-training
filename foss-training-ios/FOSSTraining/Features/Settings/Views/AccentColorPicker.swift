import SwiftUI

public struct AccentColorPicker: View {
    @Environment(\.theme) private var theme

    public init() {}

    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Gym Accent Color")
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(.secondary)

            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 12), count: 6), spacing: 12) {
                ForEach(AppAccentColor.allCases) { accent in
                    Button {
                        theme.setAccent(accent)
                        HapticManager.shared.selectionChanged()
                    } label: {
                        ZStack {
                            Circle()
                                .fill(accent.color)
                                .frame(width: 44, height: 44)
                                .shadow(color: theme.selectedAccent == accent ? accent.glowColor : .clear, radius: 8)

                            if theme.selectedAccent == accent {
                                Circle()
                                    .strokeBorder(Color.white, lineWidth: 3)
                                    .frame(width: 48, height: 48)
                                Image(systemName: "checkmark")
                                    .font(.headline.weight(.bold))
                                    .foregroundStyle(accent.badgeTextColor)
                            }
                        }
                    }
                    .buttonStyle(.plain)
                    .accessibilityLabel(accent.rawValue)
                }
            }
            .padding(.vertical, 4)

            HStack {
                Circle()
                    .fill(theme.selectedAccent.color)
                    .frame(width: 10, height: 10)
                Text("Selected: \(theme.selectedAccent.rawValue)")
                    .font(.caption.weight(.medium))
                    .foregroundStyle(theme.selectedAccent.color)
            }
        }
    }
}
