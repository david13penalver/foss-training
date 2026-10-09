import Foundation

public enum HapticIntensity: String, CaseIterable, Identifiable, Sendable {
    case disabled = "Disabled"
    case subtle = "Subtle"
    case crisp = "Crisp"
    case heavy = "Heavy"

    public var id: String { rawValue }
}
