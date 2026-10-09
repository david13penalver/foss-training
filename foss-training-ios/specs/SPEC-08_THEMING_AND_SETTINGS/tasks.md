# Tasks: SPEC-08 — Theming, Settings & Ecosystem

**Module:** `SPEC-08_THEMING_AND_SETTINGS`  
**Status:** Completed  
**Spec Reference:** [SPEC-08_THEMING_AND_SETTINGS.md](../SPEC-08_THEMING_AND_SETTINGS.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Theme Persistence & Invariants (TDD: Red ➔ Green)

- [x] **Task 1.1 (RED):** Write `ThemeManagerTests.swift` testing that `ThemeManager` defaults to `.volt` and `.oledBlack`, and mutating accent or surface synchronously writes to `UserDefaults`.
- [x] **Task 1.2 (GREEN):** Verify `ThemeManager.swift` implementation guarantees synchronous `UserDefaults` read and write.
- [x] **Task 1.3 (RED/GREEN):** Test custom `@Entry` environment values for theme tokens in SwiftUI view hierarchy.

---

## Phase 2: Sensory Subsystems (Haptics & Audio)

- [x] **Task 2.1:** Implement `HapticManager.swift` isolating `UIImpactFeedbackGenerator` and `UINotificationFeedbackGenerator` with intensity thresholds.
- [x] **Task 2.2:** Implement `SoundManager.swift` wrapping audio chimes for rest countdown completion with silent mode respect.

---

## Phase 3: Settings & Infrastructure Connection Tier

- [x] **Task 3.1 (RED):** Write `SettingsViewModelTests.swift` testing server URL configuration, ping connection test, and dual-tier toggle.
- [x] **Task 3.2 (GREEN):** Implement `SettingsViewModel.swift` with `@MainActor`.

---

## Phase 4: SwiftUI Views & Design System

- [x] **Task 4.1:** Implement `AccentColorPicker.swift` horizontal palette of color swatches with active selection rings.
- [x] **Task 4.2:** Implement `SettingsView.swift` partitioned into Appearance, Gym Ergonomics, Data Connectivity, and About sections.
- [x] **Task 4.3:** Implement `ServerConfigurationSheet.swift` with URL validation and real-time latency ping indicator.
- [x] **Task 4.4:** Configure alternate app icons matching the 6 theme accents in the asset catalog.

---

## Phase 5: Verification & Regression

- [x] **Task 5.1:** Run `FOSSTrainingTests` verifying theme persistence and settings tests pass 100%.
