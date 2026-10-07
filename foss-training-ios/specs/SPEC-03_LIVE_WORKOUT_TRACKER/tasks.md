# Tasks: SPEC-03 — Live Workout Execution & Tracker

**Module:** `SPEC-03_LIVE_WORKOUT_TRACKER`  
**Status:** Ready to Execute  
**Spec Reference:** [SPEC-03_LIVE_WORKOUT_TRACKER.md](../SPEC-03_LIVE_WORKOUT_TRACKER.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain State Machine & Volume Engine (TDD: Red ➔ Green)

- [ ] **Task 1.1 (RED):** Write `TrainingStateMachineTests.swift` testing valid state transitions (`PLANNED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `PAUSED` $\rightarrow$ `COMPLETED`).
- [ ] **Task 1.2 (GREEN):** Implement transition methods in `Training.swift` (`start()`, `pause()`, `resume()`, `complete(rpe:notes:)`, `cancel()`).
- [ ] **Task 1.3 (RED):** Write tests verifying invalid transitions throw `InvalidStateTransitionError` (e.g. attempting to start a `COMPLETED` workout).
- [ ] **Task 1.4 (GREEN):** Implement guard checks enforcing lifecycle invariants.
- [ ] **Task 1.5 (RED):** Write test for volume calculation: adding completed and uncompleted sets computes strictly completed volume.
- [ ] **Task 1.6 (GREEN):** Verify `totalVolumeKg` computation in `Training.swift`.

---

## Phase 2: SwiftData Repository Operations (TDD)

- [ ] **Task 2.1 (RED):** Write `SwiftDataTrainingRepositoryTests.swift` testing `createTrainingFromSession(sessionId:)`, instantiating a `PLANNED` workout from a template.
- [ ] **Task 2.2 (GREEN):** Implement `createTrainingFromSession` in `SwiftDataTrainingRepository.swift`.
- [ ] **Task 2.3 (RED):** Write tests for set logging: `logSet`, `updateSet`, and `deleteSet` update workout child items.
- [ ] **Task 2.4 (GREEN):** Implement set logging methods in `SwiftDataTrainingRepository.swift`.
- [ ] **Task 2.5 (RED/GREEN):** Implement status lifecycle transition repository methods (`startTraining`, `pauseTraining`, `completeTraining`).

---

## Phase 3: Remote REST Adapter (Spring Boot Parity)

- [ ] **Task 3.1 (RED):** Write `RemoteTrainingRepositoryTests.swift` verifying HTTP calls to `/api/trainings/{id}/start`, `/sets`, `/complete`.
- [ ] **Task 3.2 (GREEN):** Implement `RemoteTrainingRepository.swift` with DTO serialization.

---

## Phase 4: ActivityKit & Rest Timer Integration

- [ ] **Task 4.1:** Define `WorkoutActivityAttributes.swift` conforming to `ActivityAttributes`.
- [ ] **Task 4.2:** Implement `WorkoutActivityManager.swift` managing starting, updating, and ending Live Activities.
- [ ] **Task 4.3:** Build Dynamic Island compact, minimal, and expanded views displaying elapsed time and rest countdown.

---

## Phase 5: ViewModel Layer (`@Observable`, `@MainActor`)

- [ ] **Task 5.1 (RED):** Write `ActiveWorkoutViewModelTests.swift` testing stopwatch time tracking, set toggling, rest timer auto-trigger, and complete workout dialog.
- [ ] **Task 5.2 (GREEN):** Implement `ActiveWorkoutViewModel` managing stopwatch interval, active set index, and rest timer countdown.
- [ ] **Task 5.3 (RED/GREEN):** Implement `TrainingHistoryViewModel` managing chronological completed workouts list.

---

## Phase 6: SwiftUI Views & Tactile UX

- [ ] **Task 6.1:** Implement `ActiveWorkoutView.swift` full-screen HUD with elapsed stopwatch, exercise carousel, and completed sets progress bar.
- [ ] **Task 6.2:** Implement tactile set logging row with numeric inputs and `.sensoryFeedback(.success)` on checkmark tap.
- [ ] **Task 6.3:** Implement `RestTimerBannerView.swift` showing animated countdown bar with skip/add 30s buttons.
- [ ] **Task 6.4:** Implement `CompleteWorkoutModal.swift` capturing overall RPE slider (1.0 to 10.0), notes, and volume summary card.
- [ ] **Task 6.5:** Implement `TrainingHistoryView.swift` with status badges, date filtering, and expandable workout set summaries.

---

## Phase 7: Verification & Regression

- [ ] **Task 7.1:** Run `FOSSTrainingTests` verifying state machine, volume engine, and repository test suites pass 100%.
