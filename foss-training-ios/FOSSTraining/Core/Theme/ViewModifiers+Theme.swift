import SwiftUI

extension EnvironmentValues {
    @Entry public var theme: ThemeManager = .shared
}

public struct ThemedCardModifier: ViewModifier {
    @Environment(\.theme) private var theme

    public init() {}

    public func body(content: Content) -> some View {
        content
            .padding(16)
            .background(theme.surfaceStyle.cardBackgroundColor)
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .stroke(Color.white.opacity(0.06), lineWidth: 1)
            )
    }
}

public struct ThemedAccentBadgeModifier: ViewModifier {
    @Environment(\.theme) private var theme

    public init() {}

    public func body(content: Content) -> some View {
        content
            .font(.caption.weight(.bold))
            .padding(.horizontal, 10)
            .padding(.vertical, 4)
            .background(theme.selectedAccent.color.opacity(0.18))
            .foregroundStyle(theme.selectedAccent.color)
            .clipShape(Capsule())
    }
}

extension View {
    public func themedCard() -> some View {
        modifier(ThemedCardModifier())
    }

    public func themedBadge() -> some View {
        modifier(ThemedAccentBadgeModifier())
    }
}
