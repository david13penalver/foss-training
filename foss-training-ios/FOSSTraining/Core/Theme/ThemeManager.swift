import SwiftUI

@Observable
public final class ThemeManager: @unchecked Sendable {
    public static let shared = ThemeManager()

    public let userDefaults: UserDefaults

    private enum Keys {
        static let accentColor = "app_accent_color"
        static let surfaceStyle = "app_surface_style"
        static let hapticsEnabled = "app_haptics_enabled"
        static let hapticIntensity = "app_haptic_intensity"
        static let soundEffectsEnabled = "app_sound_effects_enabled"
        static let keepScreenAwake = "app_keep_screen_awake"
        static let weightUnit = "app_weight_unit"
    }

    public var selectedAccent: AppAccentColor {
        didSet {
            userDefaults.set(selectedAccent.rawValue, forKey: Keys.accentColor)
        }
    }

    public var surfaceStyle: SurfaceStyle {
        didSet {
            userDefaults.set(surfaceStyle.rawValue, forKey: Keys.surfaceStyle)
        }
    }

    public var hapticsEnabled: Bool {
        didSet {
            userDefaults.set(hapticsEnabled, forKey: Keys.hapticsEnabled)
        }
    }

    public var hapticIntensity: HapticIntensity {
        didSet {
            userDefaults.set(hapticIntensity.rawValue, forKey: Keys.hapticIntensity)
        }
    }

    public var soundEffectsEnabled: Bool {
        didSet {
            userDefaults.set(soundEffectsEnabled, forKey: Keys.soundEffectsEnabled)
        }
    }

    public var keepScreenAwake: Bool {
        didSet {
            userDefaults.set(keepScreenAwake, forKey: Keys.keepScreenAwake)
            applyKeepScreenAwake()
        }
    }

    public var weightUnit: WeightUnit {
        didSet {
            userDefaults.set(weightUnit.rawValue, forKey: Keys.weightUnit)
        }
    }

    public init(userDefaults: UserDefaults = .standard) {
        self.userDefaults = userDefaults

        let savedAccent = userDefaults.string(forKey: Keys.accentColor) ?? AppAccentColor.volt.rawValue
        self.selectedAccent = AppAccentColor(rawValue: savedAccent) ?? .volt

        let savedSurface = userDefaults.string(forKey: Keys.surfaceStyle) ?? SurfaceStyle.oledBlack.rawValue
        self.surfaceStyle = SurfaceStyle(rawValue: savedSurface) ?? .oledBlack

        if userDefaults.object(forKey: Keys.hapticsEnabled) == nil {
            self.hapticsEnabled = true
        } else {
            self.hapticsEnabled = userDefaults.bool(forKey: Keys.hapticsEnabled)
        }

        let savedIntensity = userDefaults.string(forKey: Keys.hapticIntensity) ?? HapticIntensity.crisp.rawValue
        self.hapticIntensity = HapticIntensity(rawValue: savedIntensity) ?? .crisp

        if userDefaults.object(forKey: Keys.soundEffectsEnabled) == nil {
            self.soundEffectsEnabled = true
        } else {
            self.soundEffectsEnabled = userDefaults.bool(forKey: Keys.soundEffectsEnabled)
        }

        self.keepScreenAwake = userDefaults.bool(forKey: Keys.keepScreenAwake)

        let savedUnit = userDefaults.string(forKey: Keys.weightUnit) ?? WeightUnit.kg.rawValue
        self.weightUnit = WeightUnit(rawValue: savedUnit) ?? .kg

        applyKeepScreenAwake()
    }

    public var idleTimerSetter: @Sendable (Bool) -> Void = { _ in }

    private func applyKeepScreenAwake() {
        idleTimerSetter(keepScreenAwake)
    }

    public func setAccent(_ accent: AppAccentColor) {
        self.selectedAccent = accent
    }

    public func setSurface(_ surface: SurfaceStyle) {
        self.surfaceStyle = surface
    }

    public func setHapticIntensity(_ intensity: HapticIntensity) {
        self.hapticIntensity = intensity
    }

    public func setWeightUnit(_ unit: WeightUnit) {
        self.weightUnit = unit
    }
}
