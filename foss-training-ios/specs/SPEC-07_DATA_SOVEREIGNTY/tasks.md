# Tasks: SPEC-07 — Data Sovereignty & Migration Bridge

**Module:** `SPEC-07_DATA_SOVEREIGNTY`  
**Status:** Ready to Execute  
**Spec Reference:** [SPEC-07_DATA_SOVEREIGNTY.md](../SPEC-07_DATA_SOVEREIGNTY.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain Schemas & Serialization (TDD: Red ➔ Green)

- [ ] **Task 1.1 (RED):** Write `BackupSerializationTests.swift` testing encoding and decoding of `FullBackupData` containing exercises, sessions, programs, trainings, and bodyweights.
- [ ] **Task 1.2 (GREEN):** Implement `FullBackupData.swift` and `ImportSummary.swift` conforming to `Codable` and `Sendable`.
- [ ] **Task 1.3 (RED):** Write `WorkoutsCsvExportTests.swift` verifying canonical CSV header, date formatting, and quotation character escaping.
- [ ] **Task 1.4 (GREEN):** Implement CSV formatting logic adhering to `ExportWorkoutsCsvService.java` parity.

---

## Phase 2: SwiftData Persistence Adapter (TDD)

- [ ] **Task 2.1 (RED):** Write `SwiftDataPortabilityRepositoryTests.swift` testing `generateBackup()` extracts all entities from the in-memory context.
- [ ] **Task 2.2 (GREEN):** Enhance `SwiftDataPortabilityRepository.swift` to include `SDTrainingProgram` and profile data.
- [ ] **Task 2.3 (RED):** Write test for `restoreBackup` with `ImportMode.merge`, `overwrite`, and `skipExisting`.
- [ ] **Task 2.4 (GREEN):** Implement conflict resolution strategies in `SwiftDataPortabilityRepository.swift`.
- [ ] **Task 2.5 (RED/GREEN):** Implement `purgeLocalDatabase()` deleting all SwiftData entities cleanly.

---

## Phase 3: Remote REST Adapter & Cloud Migration Bridge

- [ ] **Task 3.1 (RED):** Write `CloudMigrationCoordinatorTests.swift` testing export -> HTTP POST to `/api/data/import/backup` -> response verification.
- [ ] **Task 3.2 (GREEN):** Implement `CloudMigrationCoordinator.swift` managing the transfer handshake.
- [ ] **Task 3.3 (RED/GREEN):** Implement `RemotePortabilityRepository.swift` calling Spring Boot endpoints.

---

## Phase 4: ViewModel Layer (`@Observable`, `@MainActor`)

- [ ] **Task 4.1 (RED):** Write `DataPortabilityViewModelTests.swift` testing backup file generation state, import file selection, and purge confirmation.
- [ ] **Task 4.2 (GREEN):** Implement `DataPortabilityViewModel` with `@MainActor`.

---

## Phase 5: SwiftUI Views & File Handling

- [ ] **Task 5.1:** Implement `DataPortabilityView.swift` with export buttons, restore file picker, and migration card.
- [ ] **Task 5.2:** Implement `.fileExporter` wiring for JSON (`UTType.json`) and CSV (`UTType.commaSeparatedText`).
- [ ] **Task 5.3:** Implement `.fileImporter` wiring and `ImportConflictModal.swift` for choosing conflict strategies.
- [ ] **Task 5.4:** Implement `CloudMigrationSheet.swift` with server URL input, connection ping indicator, and progress bar.
- [ ] **Task 5.5:** Implement Danger Zone section with double-confirmation modal for database purge.

---

## Phase 6: Verification & Regression

- [ ] **Task 6.1:** Run `FOSSTrainingTests` verifying backup roundtrip and CSV export test suites pass 100%.
