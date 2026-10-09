import UIKit

@MainActor
public protocol HapticFeedbackProviding: AnyObject, Sendable {
    func impact(style: UIImpactFeedbackGenerator.FeedbackStyle)
    func notification(type: UINotificationFeedbackGenerator.FeedbackType)
    func selection()
}

@MainActor
public final class DefaultHapticFeedbackProvider: HapticFeedbackProviding {
    public init() {}

    public func impact(style: UIImpactFeedbackGenerator.FeedbackStyle) {
        let generator = UIImpactFeedbackGenerator(style: style)
        generator.prepare()
        generator.impactOccurred()
    }

    public func notification(type: UINotificationFeedbackGenerator.FeedbackType) {
        let generator = UINotificationFeedbackGenerator()
        generator.prepare()
        generator.notificationOccurred(type)
    }

    public func selection() {
        let generator = UISelectionFeedbackGenerator()
        generator.prepare()
        generator.selectionChanged()
    }
}

@MainActor
public final class HapticManager {
    public static let shared = HapticManager()

    public var provider: HapticFeedbackProviding

    public init(provider: HapticFeedbackProviding = DefaultHapticFeedbackProvider()) {
        self.provider = provider
    }

    public func triggerImpact(intensity: HapticIntensity) {
        guard ThemeManager.shared.hapticsEnabled else { return }
        switch intensity {
        case .disabled:
            return
        case .subtle:
            provider.impact(style: .light)
        case .crisp:
            provider.impact(style: .medium)
        case .heavy:
            provider.impact(style: .heavy)
        }
    }

    public func setCompleted() {
        guard ThemeManager.shared.hapticsEnabled else { return }
        triggerImpact(intensity: ThemeManager.shared.hapticIntensity)
    }

    public func restTimerExpired() {
        guard ThemeManager.shared.hapticsEnabled else { return }
        provider.notification(type: .success)
    }

    public func dangerAlert() {
        guard ThemeManager.shared.hapticsEnabled else { return }
        provider.notification(type: .error)
    }

    public func selectionChanged() {
        guard ThemeManager.shared.hapticsEnabled else { return }
        provider.selection()
    }
}
