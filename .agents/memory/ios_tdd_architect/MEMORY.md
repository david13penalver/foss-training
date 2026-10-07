# iOS TDD Architect Memory — FOSS Training

Index of Swift 6 patterns, SwiftUI layouts, SwiftData persistence, and Swift Testing suites.

## Initial Knowledge
- **Stack**: Swift 6, iOS 26+, SwiftUI, SwiftData, Swift Testing, XcodeGen (`project.yml`).
- **Strict Concurrency**: `SWIFT_STRICT_CONCURRENCY: complete`. ViewModels on `@MainActor`, inter-actor models conform to `Sendable`.
- **Architecture**: MVVM + Hexagonal Ports & Adapters. Domain layer is pure Swift.
- **Persistence**: `SwiftData*Repository` for local storage, `Remote*Repository` for Spring Boot sync.
- **Testing**: Hermetic in-memory SwiftData containers (`ModelConfiguration(isStoredInMemoryOnly: true)`).
- **Theme Invariant**: Theme settings stored in `UserDefaults` / `@AppStorage` (`ThemeManager`), never SwiftData.

---
<!-- Add entries with format:
- [name](file.md) — brief description
-->
