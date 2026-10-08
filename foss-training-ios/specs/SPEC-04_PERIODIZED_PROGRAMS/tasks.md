# Tasks: SPEC-04 — Periodized Training Programs

**Module:** `SPEC-04_PERIODIZED_PROGRAMS`  
**Status:** Completed  
**Spec Reference:** [SPEC-04_PERIODIZED_PROGRAMS.md](../SPEC-04_PERIODIZED_PROGRAMS.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain & Mathematical Validation (TDD: Red ➔ Green)

- [x] **Task 1.1 (RED):** Write `ProgramDomainTests.swift` testing validation rules: program name cannot be blank, duration must be $\ge 1$ week, day of week must be between $1$ and $7$.
- [x] **Task 1.2 (GREEN):** Implement `TrainingProgram.validate()` and `ProgramWorkout.validate()`.
- [x] **Task 1.3 (RED):** Write `ProgramScheduleGenerationTests.swift` testing that schedule generation from a given `startDate` instantiates exactly $W \times \text{workouts}$ training sessions on the expected weekday dates.
- [x] **Task 1.4 (GREEN):** Implement schedule generation algorithm.
- [x] **Task 1.5 (RED):** Write `ProgramAdherenceCalculatorTests.swift` verifying completion rates ($0\%$, $100\%$, partial), streak counting, and status classification (`NOT_STARTED`, `ON_TRACK`, `BEHIND`, `AT_RISK`, `COMPLETED`).
- [x] **Task 1.6 (GREEN):** Implement `ProgramAdherenceCalculator.swift` with exact Java backend parity.

---

## Phase 2: SwiftData Repository Operations (TDD)

- [x] **Task 2.1 (RED):** Write `SwiftDataTrainingProgramRepositoryTests.swift` testing program creation, retrieval, and cascade deletion.
- [x] **Task 2.2 (GREEN):** Implement `createProgram`, `getPrograms`, `getProgram(id:)`, and `deleteProgram(id:)` in `SwiftDataTrainingProgramRepository.swift`.
- [x] **Task 2.3 (RED):** Write test for `cloneProgram(id:newName:)`: verifies cloned program creates independent duplicate records.
- [x] **Task 2.4 (GREEN):** Implement program cloning in `SwiftDataTrainingProgramRepository`.
- [x] **Task 2.5 (RED/GREEN):** Implement `generateSchedule(programId:startDate:)` persisting generated `SDTraining` instances in the `ModelContext`.

---

## Phase 3: Remote REST Adapter (Spring Boot Parity)

- [x] **Task 3.1 (RED):** Write `RemoteTrainingProgramRepositoryTests.swift` mocking `/api/programs` endpoints.
- [x] **Task 3.2 (GREEN):** Implement `RemoteTrainingProgramRepository.swift` calling Spring Boot endpoints.

---

## Phase 4: ViewModel Layer (`@Observable`, `@MainActor`)

- [x] **Task 4.1 (RED):** Write `ProgramsListViewModelTests.swift` verifying loading state, filter by level, and adherence metrics attachment.
- [x] **Task 4.2 (GREEN):** Implement `ProgramsListViewModel` with reactive state.
- [x] **Task 4.3 (RED/GREEN):** Implement `ProgramDetailViewModel` with weekly timetable and schedule generation sheet trigger.

---

## Phase 5: SwiftUI Views & Adherence UI

- [x] **Task 5.1:** Implement `ProgramCardView.swift` showing level badge, duration chip, weekly frequency, and quick actions menu.
- [x] **Task 5.2:** Implement `ProgramsListView.swift` with level filter segmented control and floating *"New Program"* action.
- [x] **Task 5.3:** Implement `ProgramDetailView.swift` with 7-day visual timetable (Monday-Sunday) and rest days.
- [x] **Task 5.4:** Implement `GenerateScheduleSheet.swift` with `DatePicker` and preview list of calculated workout dates.
- [x] **Task 5.5:** Implement `ProgramAdherenceCard.swift` with circular progress ring and streak flame indicator.

---

## Phase 6: Verification & Regression

- [x] **Task 6.1:** Run `FOSSTrainingTests` verifying all program domain, schedule math, and adherence tests pass 100%.
