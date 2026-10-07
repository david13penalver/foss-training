import SwiftUI

public enum AppAccentColor: String, CaseIterable, Identifiable, Sendable {
    case volt = "Volt Lime"
    case amber = "Amber Blaze"
    case electricBlue = "Electric Blue"
    case crimson = "Crimson Pulse"
    case violet = "Cyber Violet"
    case monochrome = "Titanium"

    public var id: String { rawValue }

    public var color: Color {
        switch self {
        case .volt:
            return Color(red: 0.80, green: 0.98, blue: 0.0) // #CCFA00
        case .amber:
            return Color(red: 1.00, green: 0.58, blue: 0.0) // #FF9400
        case .electricBlue:
            return Color(red: 0.04, green: 0.52, blue: 1.0) // #0A84FF
        case .crimson:
            return Color(red: 1.00, green: 0.18, blue: 0.33) // #FF2D55
        case .violet:
            return Color(red: 0.69, green: 0.32, blue: 0.87) // #AF52DE
        case .monochrome:
            return Color(red: 0.92, green: 0.92, blue: 0.94) // Titanium light
        }
    }

    public var glowColor: Color {
        color.opacity(0.35)
    }

    public var badgeTextColor: Color {
        switch self {
        case .volt, .monochrome:
            return .black
        default:
            return .white
        }
    }
}
