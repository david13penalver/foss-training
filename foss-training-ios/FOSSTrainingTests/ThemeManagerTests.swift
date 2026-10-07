import Testing
import Foundation
@testable import FOSSTraining

@Suite("Theme & Color Personalization Tests")
struct ThemeManagerTests {

    @Test("Synchronous accent color persistence")
    func testAccentColorPersistence() {
        let theme = ThemeManager.shared

        theme.setAccent(.crimson)
        #expect(theme.selectedAccent == .crimson)

        let saved = UserDefaults.standard.string(forKey: "app_accent_color")
        #expect(saved == AppAccentColor.crimson.rawValue)

        theme.setAccent(.volt)
        #expect(theme.selectedAccent == .volt)
    }

    @Test("Surface style persistence (OLED vs Charcoal)")
    func testSurfaceStylePersistence() {
        let theme = ThemeManager.shared

        theme.setSurface(.oledBlack)
        #expect(theme.surfaceStyle == .oledBlack)

        let saved = UserDefaults.standard.string(forKey: "app_surface_style")
        #expect(saved == SurfaceStyle.oledBlack.rawValue)

        theme.setSurface(.charcoal)
        #expect(theme.surfaceStyle == .charcoal)
    }

    @Test("Tactile haptics toggle")
    func testHapticsToggle() {
        let theme = ThemeManager.shared
        theme.hapticsEnabled = false
        #expect(theme.hapticsEnabled == false)

        theme.hapticsEnabled = true
        #expect(theme.hapticsEnabled == true)
    }
}
