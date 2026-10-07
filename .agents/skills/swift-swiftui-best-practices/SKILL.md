---
name: swift-swiftui-best-practices
description: Conventions and guidelines for native iOS development with Swift 6, SwiftUI, SwiftData, Swift Testing, and Complete Strict Concurrency in foss-training-ios.
---

# Swift 6 & SwiftUI Best Practices

Standard operating guide for the native iOS application in `foss-training-ios`.

---

## 1. Concurrency & Swift 6 Mandates

- **Target**: iOS 26+
- **Strict Concurrency**: Configured with `SWIFT_STRICT_CONCURRENCY: complete`.
- **Actor Boundaries**:
  - All ViewModels and UI-state managers must be `@Observable` and isolated to `@MainActor`.
  - All domain models, value objects, and repository payloads crossing actor boundaries must conform to `Sendable`.
  - Background asynchronous tasks must handle actor boundaries safely without data races.

---

## 2. Architecture: MVVM + Hexagonal Ports & Adapters

```
FOSSTraining/
├── Domain/                      — Pure Swift. Zero framework dependencies (no SwiftUI, no SwiftData)
│   ├── Models/                  — Core entities (Exercise, Session, Training, Athlete)
│   ├── Enums/                   — Domain enums (ExerciseCategory, WeightUnit, etc.)
│   └── Repositories/            — Port protocols (ExerciseRepository, SessionRepository, etc.)
├── Data/
│   ├── SwiftData/               — Local persistence adapter
│   │   ├── Models/              — SDExercise, SDSession, SDTraining (@Model)
│   │   ├── Repositories/        — SwiftData*Repository implementations
│   │   └── Seeds/               — Seed data fixtures
│   └── Remote/                  — Remote network adapter
│       └── Repositories/        — Remote*Repository implementations (calling Spring Boot API)
├── Core/
│   ├── Network/                 — Network client and endpoints
│   └── Theme/                   — ThemeManager, Accent colors, OLED palettes
└── Features/                    — Presentation layer
    ├── Exercises/               — ExerciseListView & ExerciseListViewModel
    ├── Sessions/                — SessionListView & SessionListViewModel
    ├── Workouts/                — ActiveWorkoutView & ActiveWorkoutViewModel
    ├── Analytics/               — AnalyticsDashboardView & AnalyticsViewModel
    └── Settings/                — SettingsView & SettingsViewModel
```

### Invariants
1. **Domain Independence**: `Domain/` must never import `SwiftData`, `SwiftUI`, or `UIKit`.
2. **Dual Adapters**:
   - Local: `SwiftData*Repository` for offline, local-first operation.
   - Remote: `Remote*Repository` connecting to Spring Boot REST API (`foss-training-api`).
3. **Theming in `UserDefaults`**:
   - Theme settings (accent color, OLED Pure Black, surface style) must be stored in `UserDefaults` / `@AppStorage` via `ThemeManager`, **never in SwiftData**.
   - Rationale: Must load synchronously in `App.init()` before the first frame renders to prevent theme flashing.

---

## 3. Testing with Swift Testing & In-Memory SwiftData

- Primary test framework: **Swift Testing** (`import Testing`, `@Suite`, `@Test`, `#expect`) alongside XCTest.
- **Hermetic In-Memory Testing**:
  - All tests exercising SwiftData repositories must use an in-memory `ModelContainer`:
    ```swift
    let config = ModelConfiguration(isStoredInMemoryOnly: true)
    let container = try ModelContainer(for: SDExercise.self, configurations: config)
    ```
  - Never write to disk during unit tests.
- **Test Suite Command** (on macOS):
  ```bash
  xcodebuild -project FOSSTraining.xcodeproj -scheme FOSSTraining -destination 'generic/platform=iOS Simulator' test
  ```

---

## 4. UI Design & Accessibility Guidelines

- **Native Controls**: Use native `List`, `Form`, `Section`, `Gauge`, and `Charts` (Swift Charts).
- **Haptic Feedback**: Integrate sensory haptic feedback (`.sensoryFeedback(.success, trigger: ...)`).
- **Accessibility**:
  - Support Dynamic Type with `@ScaledMetric` and system font scales.
  - Provide `.accessibilityLabel`, `.accessibilityValue`, and `.accessibilityHint` for charts, timers, and metrics.
- **Dark Mode**: Provide true *OLED Pure Black* (`#000000`) and *Charcoal Slate* (`#161618`) surfaces.
