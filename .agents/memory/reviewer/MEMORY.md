# Reviewer Memory — FOSS Training

Index of architectural compliance checks, quality invariants, and review lessons.

## Initial Knowledge
- **Java 26 Command Rule**: Always invoke Maven with `JAVA_HOME=$(mise where java) ./mvnw`.
- **Pure Domain Boundary**: Zero Spring or JPA imports inside `foss-training-api/.../domain/`.
- **In-Memory DAOs**: Never delete `InMemory*Dao` classes (retained for migration simulation).
- **Angular Standards**: No NgModules (standalone only), state uses Signals, modern control flow (`@if`, `@for`).
- **iOS Concurrency**: `SWIFT_STRICT_CONCURRENCY: complete`, `@MainActor` ViewModels, `Sendable` domain models.
- **SwiftData Testing**: All tests must use `ModelConfiguration(isStoredInMemoryOnly: true)`. Never write to disk in unit tests.
- **Theme Manager Invariant**: Theme settings must be stored in `UserDefaults`, never SwiftData.

---
<!-- Add entries with format:
- [name](file.md) — brief description
-->
