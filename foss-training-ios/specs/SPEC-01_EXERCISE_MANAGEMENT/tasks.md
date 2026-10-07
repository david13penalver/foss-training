# Tasks: SPEC-01 — Exercise Catalog & Management

**Module:** `SPEC-01_EXERCISE_MANAGEMENT`  
**Status:** Completed (26/26 Tests Passing)  
**Spec Reference:** [SPEC-01_EXERCISE_MANAGEMENT.md](../SPEC-01_EXERCISE_MANAGEMENT.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain & Validation (TDD: Red ➔ Green)

- [x] **Task 1.1 (RED):** Write `ExerciseDomainTests.swift` testing exercise instantiation, name length validation ($\ge 2$ characters), and equipment category requirement.
- [x] **Task 1.2 (GREEN):** Implement domain validation method `Exercise.validate()` and throwing `ExerciseValidationError` domain errors.
- [x] **Task 1.3 (RED):** Write unit tests for category-specific invariants (Resistance must have movement pattern, Endurance must have endurance type, Mobility must have target joints).
- [x] **Task 1.4 (GREEN):** Implement category-specific validations in `Exercise.swift`.

---

## Phase 2: Repository Port & SwiftData Persistence Adapter (TDD)

- [x] **Task 2.1 (RED):** Write `SwiftDataExerciseRepositoryTests.swift` testing `createExercise`, `getExercises`, and `getExercise(id:)` using an in-memory `ModelContainer`.
- [x] **Task 2.2 (GREEN):** Implement CRUD operations in `SwiftDataExerciseRepository.swift` mapping to `@Model SDExercise`.
- [x] **Task 2.3 (RED):** Write repository test for soft-deletion: deleting an exercise sets `isActive = false` and verifies it is omitted from `getExercises()`.
- [x] **Task 2.4 (GREEN):** Implement `deleteExercise(id:)` in `SwiftDataExerciseRepository` as a soft-delete mutating `isActive = false`.
- [x] **Task 2.5 (GREEN):** Implement `searchExercises(query:category:)` with case-insensitive title and muscle group matching.
- [x] **Task 2.6 (SEED):** Verify `ExerciseCatalogSeed.swift` inserts default compound lifts (Squat, Bench, Deadlift, Overhead Press, Pull-Up, Barbell Row) when container is freshly initialized.

---

## Phase 3: Remote REST Adapter (Spring Boot Parity)

- [x] **Task 3.1 (RED):** Write `RemoteExerciseRepositoryTests.swift` using custom `URLProtocol` mock simulating Spring Boot `/api/exercises` responses.
- [x] **Task 3.2 (GREEN):** Implement `RemoteExerciseRepository.swift` methods calling `GET /api/exercises`, `POST /api/exercises`, `PUT /api/exercises/{id}`, and `DELETE /api/exercises/{id}`.
- [x] **Task 3.3 (REFACTOR):** Ensure uniform error handling mapping HTTP 404 to `ExerciseNotFoundError` and network timeouts to `NetworkOfflineError`.

---

## Phase 4: ViewModel Layer (`@Observable`, `@MainActor`)

- [x] **Task 4.1 (RED):** Write `ExerciseListViewModelTests.swift` verifying initial loading state, active exercises list binding, and error alert presentation.
- [x] **Task 4.2 (GREEN):** Implement `ExerciseListViewModel` with `@MainActor`, `loadExercises()`, and category filter state (`selectedCategory`).
- [x] **Task 4.3 (RED):** Write test for search debouncing: setting `searchQuery` updates filtered exercise output after delay.
- [x] **Task 4.4 (GREEN):** Implement computed or reactive filtered exercises stream in `ExerciseListViewModel`.
- [x] **Task 4.5 (RED/GREEN):** Implement `ExerciseEditorViewModel` for creating and editing exercises with live form validation feedback.

---

## Phase 5: SwiftUI Views & Design System

- [x] **Task 5.1:** Implement `ExerciseCardView.swift` featuring category icon badge, primary muscle group label, difficulty pill, and OLED Pure Black card surface.
- [x] **Task 5.2:** Enhance `ExerciseListView.swift` with `.searchable` bar, horizontal category filter chips, swipe-to-delete action, and floating add button.
- [x] **Task 5.3:** Implement `ExerciseDetailView.swift` showing muscle anatomy tags, movement pattern badge, and step-by-step instructions.
- [x] **Task 5.4:** Implement `ExerciseEditorSheet.swift` form allowing selection of categories, muscle groups, equipment, and dynamic instruction fields.

---

## Phase 6: Verification & Regression

- [x] **Task 6.1:** Run `FOSSTrainingTests` verifying 100% test pass rate across domain, repository, and view model suites.
- [x] **Task 6.2:** Verify zero compile warnings with `SWIFT_STRICT_CONCURRENCY: complete`.
