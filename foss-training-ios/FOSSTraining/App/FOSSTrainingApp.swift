import SwiftUI
import SwiftData

@main
struct FOSSTrainingApp: App {
    static var isRunningTests: Bool {
        NSClassFromString("XCTestCase") != nil ||
        ProcessInfo.processInfo.environment["XCTestConfigurationFilePath"] != nil
    }

    @State private var themeManager = ThemeManager.shared
    @State private var appEnvironment = AppEnvironment(inMemory: isRunningTests)

    var body: some Scene {
        WindowGroup {
            if Self.isRunningTests {
                Text("Running Tests...")
            } else {
                ContentView(appEnvironment: appEnvironment)
                    .environment(\.theme, themeManager)
                    .modelContainer(appEnvironment.modelContainer)
                    .preferredColorScheme(colorScheme)
            }
        }
    }

    static func colorScheme(for style: SurfaceStyle) -> ColorScheme? {
        switch style {
        case .oledBlack, .charcoal:
            return .dark
        case .systemAdaptive:
            return nil
        }
    }

    private var colorScheme: ColorScheme? {
        Self.colorScheme(for: themeManager.surfaceStyle)
    }
}
