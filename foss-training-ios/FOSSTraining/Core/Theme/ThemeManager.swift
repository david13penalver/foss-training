import SwiftUI

@Observable
public final class ThemeManager: @unchecked Sendable {
    public static let shared = ThemeManager()

    private enum Keys {
        static let accentColor = "app_accent_color"
        static let surfaceStyle = "app_surface_style"
        static let hapticsEnabled = "app_haptics_enabled"
    }

    public var selectedAccent: AppAccentColor {
        didSet {
            UserDefaults.standard.set(selectedAccent.rawValue, forKey: Keys.accentColor)
        }
    }

    public var surfaceStyle: SurfaceStyle {
        didSet {
            UserDefaults.standard.set(surfaceStyle.rawValue, forKey: Keys.surfaceStyle)
        }
    }

    public var hapticsEnabled: Bool {
        didSet {
            UserDefaults.standard.set(hapticsEnabled, forKey: Keys.hapticsEnabled)
        }
    }

    init() {
        let savedAccent = UserDefaults.standard.string(forKey: Keys.accentColor) ?? AppAccentColor.volt.rawValue
        self.selectedAccent = AppAccentColor(rawValue: savedAccent) ?? .volt

        let savedSurface = UserDefaults.standard.string(forKey: Keys.surfaceStyle) ?? SurfaceStyle.oledBlack.rawValue
        self.surfaceStyle = SurfaceStyle(rawValue: savedSurface) ?? .oledBlack

        if UserDefaults.standard.object(forKey: Keys.hapticsEnabled) == nil {
            self.hapticsEnabled = true
        } else {
            self.hapticsEnabled = UserDefaults.standard.bool(forKey: Keys.hapticsEnabled)
        }
    }

    public func setAccent(_ accent: AppAccentColor) {
        self.selectedAccent = accent
    }

    public func setSurface(_ surface: SurfaceStyle) {
        self.surfaceStyle = surface
    }
}
