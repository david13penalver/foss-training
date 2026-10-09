import Testing
import UIKit
import AudioToolbox
@testable import FOSSTraining

@MainActor
final class MockHapticFeedbackProvider: HapticFeedbackProviding, @unchecked Sendable {
    var impactStyles: [UIImpactFeedbackGenerator.FeedbackStyle] = []
    var notificationTypes: [UINotificationFeedbackGenerator.FeedbackType] = []
    var selectionCount: Int = 0

    func impact(style: UIImpactFeedbackGenerator.FeedbackStyle) {
        impactStyles.append(style)
    }

    func notification(type: UINotificationFeedbackGenerator.FeedbackType) {
        notificationTypes.append(type)
    }

    func selection() {
        selectionCount += 1
    }
}

@Suite("Sensory Feedback Engine Tests (Haptics & Audio)")
@MainActor
struct SensoryManagerTests {

    @Test("HapticManager respects intensity levels and triggers appropriate styles")
    func testHapticIntensityStyles() {
        let mockProvider = MockHapticFeedbackProvider()
        let manager = HapticManager(provider: mockProvider)
        let theme = ThemeManager.shared
        theme.hapticsEnabled = true

        manager.triggerImpact(intensity: .disabled)
        #expect(mockProvider.impactStyles.isEmpty)

        manager.triggerImpact(intensity: .subtle)
        #expect(mockProvider.impactStyles.last == .light)

        manager.triggerImpact(intensity: .crisp)
        #expect(mockProvider.impactStyles.last == .medium)

        manager.triggerImpact(intensity: .heavy)
        #expect(mockProvider.impactStyles.last == .heavy)
    }

    @Test("HapticManager actions: setCompleted, restTimerExpired, dangerAlert, selectionChanged")
    func testHapticManagerActions() {
        let mockProvider = MockHapticFeedbackProvider()
        let manager = HapticManager(provider: mockProvider)
        let theme = ThemeManager.shared

        theme.hapticsEnabled = true
        theme.setHapticIntensity(.crisp)

        manager.setCompleted()
        #expect(mockProvider.impactStyles.last == .medium)

        manager.restTimerExpired()
        #expect(mockProvider.notificationTypes.last == .success)

        manager.dangerAlert()
        #expect(mockProvider.notificationTypes.last == .error)

        manager.selectionChanged()
        #expect(mockProvider.selectionCount == 1)

        // When haptics disabled, no actions triggered
        theme.hapticsEnabled = false
        manager.setCompleted()
        manager.restTimerExpired()
        manager.dangerAlert()
        manager.selectionChanged()
        manager.triggerImpact(intensity: .heavy)

        #expect(mockProvider.impactStyles.count == 1)
        #expect(mockProvider.notificationTypes.count == 2)
        #expect(mockProvider.selectionCount == 1)

        // Restore
        theme.hapticsEnabled = true
    }

    @Test("DefaultHapticFeedbackProvider can instantiate and trigger without crashing")
    func testDefaultHapticFeedbackProvider() {
        let provider = DefaultHapticFeedbackProvider()
        provider.impact(style: .light)
        provider.notification(type: .warning)
        provider.selection()
    }

    final class SoundCollector: @unchecked Sendable {
        var playedSoundIDs: [SystemSoundID] = []
        func append(_ id: SystemSoundID) {
            playedSoundIDs.append(id)
        }
    }

    @Test("SoundManager triggers appropriate system sounds when enabled")
    func testSoundManagerEnabled() {
        let collector = SoundCollector()
        let soundManager = SoundManager { soundID in
            collector.append(soundID)
        }
        let theme = ThemeManager.shared
        theme.soundEffectsEnabled = true

        soundManager.playRestTimerChime()
        #expect(collector.playedSoundIDs.last == 1057)

        soundManager.playCountdownBeep()
        #expect(collector.playedSoundIDs.last == 1052)

        soundManager.playWorkoutCompleteSound()
        #expect(collector.playedSoundIDs.last == 1025)
    }

    @Test("SoundManager respects soundEffectsEnabled == false")
    func testSoundManagerDisabled() {
        let collector = SoundCollector()
        let soundManager = SoundManager { soundID in
            collector.append(soundID)
        }
        let theme = ThemeManager.shared
        theme.soundEffectsEnabled = false

        soundManager.playRestTimerChime()
        soundManager.playCountdownBeep()
        soundManager.playWorkoutCompleteSound()

        #expect(collector.playedSoundIDs.isEmpty)

        // Restore
        theme.soundEffectsEnabled = true
    }

    @Test("SoundManager default playSystemSoundAction closure")
    func testSoundManagerDefaultClosure() {
        let defaultSoundManager = SoundManager()
        // Execute default closure
        defaultSoundManager.playSystemSoundAction(0)
    }
}
