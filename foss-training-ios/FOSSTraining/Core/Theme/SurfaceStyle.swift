import SwiftUI

public enum SurfaceStyle: String, CaseIterable, Identifiable, Sendable {
    case oledBlack = "OLED Pure Black"
    case charcoal = "Charcoal Slate"
    case systemAdaptive = "System Adaptive"

    public var id: String { rawValue }

    public var backgroundColor: Color {
        switch self {
        case .oledBlack:
            return Color.black
        case .charcoal:
            return Color(red: 0.09, green: 0.09, blue: 0.11)
        case .systemAdaptive:
            return Color(uiColor: .systemGroupedBackground)
        }
    }

    public var cardBackgroundColor: Color {
        switch self {
        case .oledBlack:
            return Color(red: 0.08, green: 0.08, blue: 0.09)
        case .charcoal:
            return Color(red: 0.14, green: 0.14, blue: 0.16)
        case .systemAdaptive:
            return Color(uiColor: .secondarySystemGroupedBackground)
        }
    }

    public var tertiaryBackgroundColor: Color {
        switch self {
        case .oledBlack:
            return Color(red: 0.12, green: 0.12, blue: 0.14)
        case .charcoal:
            return Color(red: 0.18, green: 0.18, blue: 0.20)
        case .systemAdaptive:
            return Color(uiColor: .tertiarySystemGroupedBackground)
        }
    }
}
