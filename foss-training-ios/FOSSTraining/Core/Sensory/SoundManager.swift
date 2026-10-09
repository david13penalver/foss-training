import AudioToolbox
import Foundation

@MainActor
public final class SoundManager {
    public static let shared = SoundManager()

    public var playSystemSoundAction: @Sendable (SystemSoundID) -> Void

    public init(playSystemSoundAction: (@Sendable (SystemSoundID) -> Void)? = nil) {
        if let playSystemSoundAction = playSystemSoundAction {
            self.playSystemSoundAction = playSystemSoundAction
        } else {
            self.playSystemSoundAction = { soundID in
                AudioServicesPlaySystemSound(soundID)
            }
        }
    }

    public func playRestTimerChime() {
        guard ThemeManager.shared.soundEffectsEnabled else { return }
        playSystemSoundAction(1057)
    }

    public func playCountdownBeep() {
        guard ThemeManager.shared.soundEffectsEnabled else { return }
        playSystemSoundAction(1052)
    }

    public func playWorkoutCompleteSound() {
        guard ThemeManager.shared.soundEffectsEnabled else { return }
        playSystemSoundAction(1025)
    }
}
