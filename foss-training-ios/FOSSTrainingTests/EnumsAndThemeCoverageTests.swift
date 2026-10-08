import Testing
import Foundation
import SwiftUI
@testable import FOSSTraining

@Suite("Enums, Theme & Domain Value Object Coverage Tests")
@MainActor
struct EnumsAndThemeCoverageTests {

    @Test("AppAccentColor cases, colors, glow, and badge text colors")
    func testAppAccentColor() {
        for accent in AppAccentColor.allCases {
            #expect(!accent.id.isEmpty)
            #expect(!accent.rawValue.isEmpty)
            _ = accent.color
            _ = accent.glowColor

            switch accent {
            case .volt, .monochrome:
                #expect(accent.badgeTextColor == .black)
            default:
                #expect(accent.badgeTextColor == .white)
            }
        }
    }

    @Test("SurfaceStyle cases, background colors, card colors, tertiary colors")
    func testSurfaceStyle() {
        for style in SurfaceStyle.allCases {
            #expect(!style.id.isEmpty)
            _ = style.backgroundColor
            _ = style.cardBackgroundColor
            _ = style.tertiaryBackgroundColor
        }
    }

    @Test("ExerciseCategory displayName and systemIcon")
    func testExerciseCategory() {
        for cat in ExerciseCategory.allCases {
            #expect(!cat.id.isEmpty)
            #expect(!cat.displayName.isEmpty)
            #expect(!cat.systemIcon.isEmpty)
        }
    }

    @Test("DifficultyLevel displayName")
    func testDifficultyLevel() {
        for diff in DifficultyLevel.allCases {
            #expect(!diff.id.isEmpty)
            #expect(!diff.displayName.isEmpty)
        }
    }

    @Test("EquipmentCategory displayName")
    func testEquipmentCategory() {
        for eq in EquipmentCategory.allCases {
            #expect(!eq.id.isEmpty)
            #expect(!eq.displayName.isEmpty)
        }
    }

    @Test("MovementPattern displayName")
    func testMovementPattern() {
        for pat in MovementPattern.allCases {
            #expect(!pat.id.isEmpty)
            #expect(!pat.displayName.isEmpty)
        }
    }

    @Test("SetType displayName, shortTag and countsAsWorkingVolume")
    func testSetType() {
        for st in SetType.allCases {
            #expect(!st.id.isEmpty)
            #expect(!st.displayName.isEmpty)
            #expect(!st.shortTag.isEmpty)
            if st == .warmUp {
                #expect(!st.countsAsWorkingVolume)
            } else {
                #expect(st.countsAsWorkingVolume)
            }
        }
    }

    @Test("TrainingStatus displayName, canStart and canComplete")
    func testTrainingStatus() {
        for status in TrainingStatus.allCases {
            #expect(!status.id.isEmpty)
            #expect(!status.displayName.isEmpty)

            switch status {
            case .planned, .paused:
                #expect(status.canStart == true)
            default:
                #expect(status.canStart == false)
            }

            switch status {
            case .inProgress, .paused:
                #expect(status.canComplete == true)
            default:
                #expect(status.canComplete == false)
            }
        }
    }

    @Test("SessionPartEnum displayName")
    func testSessionPartEnum() {
        for part in SessionPartEnum.allCases {
            #expect(!part.id.isEmpty)
            #expect(!part.displayName.isEmpty)
        }
    }

    @Test("AppTierMode title and id")
    func testAppTierMode() {
        for tier in AppTierMode.allCases {
            #expect(!tier.id.isEmpty)
            #expect(!tier.title.isEmpty)
        }
    }

    @Test("ThemeManager accent, surface, and haptic mutators")
    func testThemeManagerMutators() {
        let manager = ThemeManager.shared
        manager.setAccent(.crimson)
        #expect(manager.selectedAccent == .crimson)

        manager.setSurface(.charcoal)
        #expect(manager.surfaceStyle == .charcoal)

        manager.hapticsEnabled = false
        #expect(manager.hapticsEnabled == false)

        manager.hapticsEnabled = true
        #expect(manager.hapticsEnabled == true)

        manager.setAccent(.volt)
        manager.setSurface(.oledBlack)
    }

    @Test("ResistanceSet, SessionExerciseItem, and BackupDataPayload initializers and IDs")
    func testModelIdentifiableIDs() {
        let set = ResistanceSet(setNumber: 3, weightKg: 100, repetitions: 5)
        #expect(set.id == "3")

        let item = SessionExerciseItem(orderIndex: 2, exerciseId: 42, exerciseName: "Squat")
        #expect(item.id == "42-2")

        let payload = BackupDataPayload(
            exportVersion: "2.0",
            exportedAt: Date(),
            exercises: [],
            sessions: [],
            trainings: [],
            bodyweightEntries: []
        )
        #expect(payload.exportVersion == "2.0")
    }

    @Test("ViewModifiers theme card and badge extensions with layout evaluation")
    func testThemeViewModifiers() {
        let text = Text("Test")
        let card = text.themedCard()
        let badge = text.themedBadge()

        let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 300, height: 300))
        let hostCard = UIHostingController(rootView: card)
        window.rootViewController = hostCard
        window.makeKeyAndVisible()
        hostCard.view.layoutIfNeeded()

        let hostBadge = UIHostingController(rootView: badge)
        window.rootViewController = hostBadge
        hostBadge.view.layoutIfNeeded()
    }

    @Test("ThemeManager initialization with varied UserDefaults states")
    func testThemeManagerInitBranches() {
        // 1. Valid non-default
        UserDefaults.standard.set(AppAccentColor.electricBlue.rawValue, forKey: "app_accent_color")
        UserDefaults.standard.set(SurfaceStyle.charcoal.rawValue, forKey: "app_surface_style")
        UserDefaults.standard.set(false, forKey: "app_haptics_enabled")
        let tm1 = ThemeManager()
        #expect(tm1.selectedAccent == .electricBlue)
        #expect(tm1.surfaceStyle == .charcoal)
        #expect(tm1.hapticsEnabled == false)

        // 2. Invalid raw values (fallbacks to defaults)
        UserDefaults.standard.set("invalid_color", forKey: "app_accent_color")
        UserDefaults.standard.set("invalid_surface", forKey: "app_surface_style")
        UserDefaults.standard.removeObject(forKey: "app_haptics_enabled")
        let tm2 = ThemeManager()
        #expect(tm2.selectedAccent == .volt)
        #expect(tm2.surfaceStyle == .oledBlack)
        #expect(tm2.hapticsEnabled == true)

        // 3. Missing keys entirely
        UserDefaults.standard.removeObject(forKey: "app_accent_color")
        UserDefaults.standard.removeObject(forKey: "app_surface_style")
        let tm3 = ThemeManager()
        #expect(tm3.selectedAccent == .volt)
        #expect(tm3.surfaceStyle == .oledBlack)

        // Restore defaults
        UserDefaults.standard.set(AppAccentColor.volt.rawValue, forKey: "app_accent_color")
        UserDefaults.standard.set(SurfaceStyle.oledBlack.rawValue, forKey: "app_surface_style")
        UserDefaults.standard.set(true, forKey: "app_haptics_enabled")
    }

    @Test("FOSSTrainingApp colorScheme and isRunningTests")
    func testFOSSTrainingAppHelpers() {
        #expect(FOSSTrainingApp.colorScheme(for: .oledBlack) == .dark)
        #expect(FOSSTrainingApp.colorScheme(for: .charcoal) == .dark)
        #expect(FOSSTrainingApp.colorScheme(for: .systemAdaptive) == nil)
        #expect(FOSSTrainingApp.isRunningTests == true)
    }

    @Test("ExerciseValidationError error descriptions")
    func testExerciseValidationErrorDescriptions() {
        #expect(ExerciseValidationError.nameTooShort.errorDescription?.contains("2 characters") == true)
        #expect(ExerciseValidationError.missingMovementPattern.errorDescription?.contains("movement pattern") == true)
        #expect(ExerciseValidationError.missingEnduranceType.errorDescription?.contains("endurance type") == true)
        #expect(ExerciseValidationError.missingMobilityDetails.errorDescription?.contains("mobility type") == true)
    }
}
