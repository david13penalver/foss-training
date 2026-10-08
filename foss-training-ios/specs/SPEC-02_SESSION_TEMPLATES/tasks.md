# Tasks: SPEC-02 — Session Templates & Workout Builder

**Module:** `SPEC-02_SESSION_TEMPLATES`  
**Status:** Completed  
**Spec Reference:** [SPEC-02_SESSION_TEMPLATES.md](../SPEC-02_SESSION_TEMPLATES.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain Aggregates & Invariants (TDD: Red ➔ Green)

- [x] **Task 1.1 (RED):** Write `SessionDomainTests.swift` testing that initializing a session with an empty name or negative duration throws `SessionValidationError`.
- [x] **Task 1.2 (GREEN):** Implement `Session.validate()` adhering to domain invariants.
- [x] **Task 1.3 (RED):** Write tests verifying that `SessionExerciseItem` properly categorizes into `WARM_UP`, `MAIN`, or `COOLDOWN` and maintains correct sequential `orderIndex`.
- [x] **Task 1.4 (GREEN):** Implement ordering helpers and session part filters in `Session.swift`.

---

## Phase 2: SwiftData Persistence Adapter (TDD)

- [x] **Task 2.1 (RED):** Write `SwiftDataSessionRepositoryTests.swift` testing full CRUD lifecycle on in-memory `ModelContainer`.
- [x] **Task 2.2 (GREEN):** Implement `createSession`, `getSessions`, and `getSession(id:)` in `SwiftDataSessionRepository.swift`.
- [x] **Task 2.3 (RED):** Write test for template cloning: calling `cloneSession(id:)` creates a new `SDSession` named `"{Name} (Copy)"` with cloned exercise items and sets.
- [x] **Task 2.4 (GREEN):** Implement deep cloning logic in `SwiftDataSessionRepository`.
- [x] **Task 2.5 (RED):** Write test for cascade deletion: deleting an `SDSession` removes all associated `SDSessionExercise` and `SDResistanceSet` records from the context.
- [x] **Task 2.6 (GREEN):** Configure SwiftData `@Relationship(deleteRule: .cascade)` on `sessionExercises` and `sets`.

---

## Phase 3: Remote REST Adapter (Spring Boot Parity)

- [x] **Task 3.1 (RED):** Write `RemoteSessionRepositoryTests.swift` mocking Spring Boot endpoints (`GET /api/sessions`, `POST /api/sessions`, `POST /api/sessions/{id}/clone`).
- [x] **Task 3.2 (GREEN):** Implement `RemoteSessionRepository.swift` with URLSession serialization and DTO mapping.

---

## Phase 4: ViewModel Layer (`@Observable`, `@MainActor`)

- [x] **Task 4.1 (RED):** Write `SessionListViewModelTests.swift` verifying fetching, deletion, and cloning actions.
- [x] **Task 4.2 (GREEN):** Implement `SessionListViewModel` with reactive state.
- [x] **Task 4.3 (RED):** Write `SessionBuilderViewModelTests.swift` testing adding exercises, editing sets, reordering items, and saving templates.
- [x] **Task 4.4 (GREEN):** Implement `SessionBuilderViewModel` handling multi-part exercises, set insertions, and validation state.

---

## Phase 5: SwiftUI Views & Builder UI

- [x] **Task 5.1:** Implement `SessionCardView.swift` showing estimated duration chip, exercise count, and quick clone/delete context menu.
- [x] **Task 5.2:** Enhance `SessionListView.swift` with template search, pull-to-refresh, and *"New Template"* button.
- [x] **Task 5.3:** Implement `SessionPartSectionView.swift` rendering collapsible sections for Warm-Up, Main Workout, and Cooldown.
- [x] **Task 5.4:** Implement `SetsTableEditorView.swift` with tabular columns: Set #, Type badge, Weight (kg), Reps, and Rest (s).
- [x] **Task 5.5:** Implement `SessionBuilderView.swift` supporting native SwiftUI `.onMove` drag-and-drop exercise reordering.
- [x] **Task 5.6:** Implement *"Start This Workout"* CTA button converting template to active `Training`.

---

## Phase 6: Verification & Regression

- [x] **Task 6.1:** Run `FOSSTrainingTests` verifying 100% test pass rate for all Session suites.
- [x] **Task 6.2:** Verify Swift 6 concurrency compliance across all Session models and ViewModels.

