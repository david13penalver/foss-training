# FOSS Training — iOS Agent Guide

This document defines the architectural conventions, testing standards, and specialized AI agent roles for the native iOS application (`foss-training-ios`).

---

## 1. Core Development Rules

### Swift 6 & Complete Strict Concurrency
- Target: **iOS 26+**
- Strict Concurrency: `SWIFT_STRICT_CONCURRENCY: complete`
- Models and Repositories passing actor boundaries must conform to `Sendable`.
- ViewModels and UI-bound services must be isolated to `@MainActor`.

### TDD (Test-Driven Development) Mandate
- **Red-Green-Refactor**: Every new domain feature, state transition, repository method, or calculator must be preceded by or accompanied by tests.
- Testing Framework: Modern **Swift Testing** (`import Testing`, `@Suite`, `@Test`, `#expect`) and XCTest.
- **Hermetic In-Memory Testing**: All SwiftData tests must use an in-memory container (`ModelConfiguration(isStoredInMemoryOnly: true)`). Never write to disk during unit tests.

### Architecture: MVVM + Hexagonal Ports & Adapters
- **Domain Layer**: Pure Swift. Zero framework dependencies.
- **Ports (Protocols)**: `ExerciseRepository`, `SessionRepository`, `TrainingRepository`, `AthleteRepository`, `DataPortabilityRepository`.
- **Adapters**:
  - **Local (Free)**: `SwiftData*Repository` utilizing on-device `ModelContext`.
  - **Premium**: `Remote*Repository` calling Spring Boot REST API (`foss-training-api`).
- **Presentation**: SwiftUI Views + `@Observable` ViewModels.

### Theming & Color Customization
- **Strict Rule**: Theme preferences (Accent Color, Surface Style, OLED Pure Black) **must be stored in `UserDefaults` / `@AppStorage` (`ThemeManager`)**, NEVER in SwiftData.
- Rationale: Appearance must be loaded synchronously in `App.init()` to prevent theme flashing on launch.

---

## 2. Specialized Agent Personas & Workflows

### 🏛️ `ios_tdd_architect`
- **Focus**: Test-Driven Development, test coverage, and concurrency verification.
- **Workflow**:
  1. Write `@Suite` tests in `FOSSTrainingTests/`.
  2. Implement minimal code to pass tests.
  3. Verify with `xcodebuild -project FOSSTraining.xcodeproj -scheme FOSSTraining -destination 'generic/platform=iOS Simulator' -derivedDataPath ./build/DerivedData test`.

### 💾 `swiftdata_persistence_specialist`
- **Focus**: SwiftData schemas (`@Model`), relationships, in-memory fixtures, and backup portability.
- **Key Files**:
  - `Data/SwiftData/Models/`
  - `Data/SwiftData/Repositories/`
  - `Data/SwiftData/Seeds/`
- **Migration Bridge**: Ensures `BackupDataPayload` matches the Spring Boot `/api/data/import/backup` schema.

### 🔬 `sports_science_domain_agent`
- **Focus**: Exact mathematical parity with sports science literature and the Java backend.
- **Formulas Covered**:
  - 1RM: Epley, Brzycki, Lombardi, Mayhew, O'Conner, Wathen.
  - ACWR: Acute (7-day) vs Chronic (28-day) workload ratios and injury risk classification.
  - Powerlifting Relative Strength: DOTS and Wilks polynomial equations (Male & Female).

### 🎨 `swiftui_design_specialist`
- **Focus**: 100% native Apple design system, accessibility, and visual polish.
- **Components**:
  - Native `List`, `Form`, `Section`, `Gauge`, and **Swift Charts**.
  - Dynamic Type, VoiceOver accessibility labels, and sensory haptic feedback (`.sensoryFeedback`).
  - Dark Mode palettes: *OLED Pure Black* (#000000) and *Charcoal Slate* (#161618).

### ⌚ `apple_ecosystem_specialist`
- **Focus**: Future platform integrations:
  - ActivityKit / Live Activities & Dynamic Island rest timers.
  - Apple Watch companion application.
  - HealthKit heart rate and workout synchronization.
