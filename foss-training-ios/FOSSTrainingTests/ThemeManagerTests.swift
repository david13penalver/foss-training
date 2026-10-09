import Testing
import Foundation
@testable import FOSSTraining

@Suite("Theme & Color Personalization Tests")
struct ThemeManagerTests {

    @Test("Synchronous accent color persistence")
    func testAccentColorPersistence() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_Accent")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_Accent")
        let theme = ThemeManager(userDefaults: defaults)

        theme.setAccent(.crimson)
        #expect(theme.selectedAccent == .crimson)

        let saved = defaults.string(forKey: "app_accent_color")
        #expect(saved == AppAccentColor.crimson.rawValue)

        theme.setAccent(.volt)
        #expect(theme.selectedAccent == .volt)
    }

    @Test("Surface style persistence (OLED vs Charcoal)")
    func testSurfaceStylePersistence() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_Surface")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_Surface")
        let theme = ThemeManager(userDefaults: defaults)

        theme.setSurface(.oledBlack)
        #expect(theme.surfaceStyle == .oledBlack)

        let saved = defaults.string(forKey: "app_surface_style")
        #expect(saved == SurfaceStyle.oledBlack.rawValue)

        theme.setSurface(.charcoal)
        #expect(theme.surfaceStyle == .charcoal)

        theme.setSurface(.oledBlack)
    }

    @Test("Tactile haptics toggle and intensity persistence")
    func testHapticsToggleAndIntensity() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_Haptics")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_Haptics")
        let theme = ThemeManager(userDefaults: defaults)

        theme.hapticsEnabled = false
        #expect(theme.hapticsEnabled == false)

        theme.hapticsEnabled = true
        #expect(theme.hapticsEnabled == true)

        theme.setHapticIntensity(.subtle)
        #expect(theme.hapticIntensity == .subtle)
        #expect(defaults.string(forKey: "app_haptic_intensity") == HapticIntensity.subtle.rawValue)

        theme.setHapticIntensity(.heavy)
        #expect(theme.hapticIntensity == .heavy)

        theme.setHapticIntensity(.crisp)
    }

    @Test("Sound effects and keep screen awake toggles")
    func testSoundAndScreenAwakeToggles() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_SoundAwake")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_SoundAwake")
        let theme = ThemeManager(userDefaults: defaults)

        theme.soundEffectsEnabled = false
        #expect(theme.soundEffectsEnabled == false)
        #expect(defaults.bool(forKey: "app_sound_effects_enabled") == false)

        theme.soundEffectsEnabled = true
        #expect(theme.soundEffectsEnabled == true)

        theme.keepScreenAwake = true
        #expect(theme.keepScreenAwake == true)
        #expect(defaults.bool(forKey: "app_keep_screen_awake") == true)

        theme.keepScreenAwake = false
        #expect(theme.keepScreenAwake == false)
    }

    @Test("Weight unit preference persistence")
    func testWeightUnitPersistence() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_WeightUnit")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_WeightUnit")
        let theme = ThemeManager(userDefaults: defaults)

        theme.setWeightUnit(.lbs)
        #expect(theme.weightUnit == .lbs)
        #expect(defaults.string(forKey: "app_weight_unit") == WeightUnit.lbs.rawValue)

        theme.setWeightUnit(.kg)
        #expect(theme.weightUnit == .kg)
        #expect(defaults.string(forKey: "app_weight_unit") == WeightUnit.kg.rawValue)
    }

    @Test("ThemeManager initialization with full coverage of default fallbacks")
    func testThemeManagerInitFallbacks() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_Init")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_Init")

        // 1. Set all custom values
        defaults.set(AppAccentColor.amber.rawValue, forKey: "app_accent_color")
        defaults.set(SurfaceStyle.charcoal.rawValue, forKey: "app_surface_style")
        defaults.set(false, forKey: "app_haptics_enabled")
        defaults.set(HapticIntensity.heavy.rawValue, forKey: "app_haptic_intensity")
        defaults.set(false, forKey: "app_sound_effects_enabled")
        defaults.set(true, forKey: "app_keep_screen_awake")
        defaults.set(WeightUnit.lbs.rawValue, forKey: "app_weight_unit")

        let tmCustom = ThemeManager(userDefaults: defaults)
        #expect(tmCustom.selectedAccent == .amber)
        #expect(tmCustom.surfaceStyle == .charcoal)
        #expect(tmCustom.hapticsEnabled == false)
        #expect(tmCustom.hapticIntensity == .heavy)
        #expect(tmCustom.soundEffectsEnabled == false)
        #expect(tmCustom.keepScreenAwake == true)
        #expect(tmCustom.weightUnit == .lbs)

        // 2. Clear all keys to test defaults
        defaults.removePersistentDomain(forName: "ThemeManagerTests_Init")

        let tmDefault = ThemeManager(userDefaults: defaults)
        #expect(tmDefault.selectedAccent == .volt)
        #expect(tmDefault.surfaceStyle == .oledBlack)
        #expect(tmDefault.hapticsEnabled == true)
        #expect(tmDefault.hapticIntensity == .crisp)
        #expect(tmDefault.soundEffectsEnabled == true)
        #expect(tmDefault.keepScreenAwake == false)
        #expect(tmDefault.weightUnit == .kg)

        // 3. Set corrupted/unrecognized raw values to test Enum fallbacks
        defaults.set("INVALID_ACCENT", forKey: "app_accent_color")
        defaults.set("INVALID_SURFACE", forKey: "app_surface_style")
        defaults.set("INVALID_INTENSITY", forKey: "app_haptic_intensity")
        defaults.set("INVALID_UNIT", forKey: "app_weight_unit")

        let tmInvalid = ThemeManager(userDefaults: defaults)
        #expect(tmInvalid.selectedAccent == .volt)
        #expect(tmInvalid.surfaceStyle == .oledBlack)
        #expect(tmInvalid.hapticIntensity == .crisp)
        #expect(tmInvalid.weightUnit == .kg)
    }

    @Test("ThemeManager shared instance smoke test")
    func testThemeManagerSharedInstance() {
        let shared = ThemeManager.shared
        _ = shared.selectedAccent
        _ = shared.surfaceStyle
        _ = shared.hapticsEnabled
        _ = shared.hapticIntensity
        _ = shared.soundEffectsEnabled
        _ = shared.keepScreenAwake
        _ = shared.weightUnit
    }

    final class IdleTimerCollector: @unchecked Sendable {
        var recordedState: Bool?
    }

    @Test("Keep screen awake idleTimerSetter handler")
    func testKeepScreenAwakeIdleTimerSetter() {
        let defaults = UserDefaults(suiteName: "ThemeManagerTests_Idle")!
        defaults.removePersistentDomain(forName: "ThemeManagerTests_Idle")
        let theme = ThemeManager(userDefaults: defaults)
        let collector = IdleTimerCollector()
        theme.idleTimerSetter = { disabled in
            collector.recordedState = disabled
        }
        theme.keepScreenAwake = true
        #expect(collector.recordedState == true)
        theme.keepScreenAwake = false
        #expect(collector.recordedState == false)
    }
}
