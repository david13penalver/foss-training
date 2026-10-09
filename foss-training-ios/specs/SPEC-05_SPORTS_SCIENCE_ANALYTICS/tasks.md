# Tasks: SPEC-05 — Sports Science & Analytics

**Module:** `SPEC-05_SPORTS_SCIENCE_ANALYTICS`  
**Status:** Complete  
**Spec Reference:** [SPEC-05_SPORTS_SCIENCE_ANALYTICS.md](../SPEC-05_SPORTS_SCIENCE_ANALYTICS.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain Calculators (TDD: Red ➔ Green)

- [x] **Task 1.1 (RED):** Write `OneRepMaxParityTests.swift` testing Epley, Brzycki, Lombardi, Mayhew, O'Conner, Wathen across standard test vectors ($100$ kg lifted at $1, 5, 10$ reps).
- [x] **Task 1.2 (GREEN):** Implement/verify `OneRepMaxCalculator.swift` with rounding to 2 decimal places and $r=1$ identity check.
- [x] **Task 1.3 (RED):** Write `AcwrCalculatorTests.swift` testing daily session load calculation, 7d acute sum, 28d chronic average, and zone mapping (`UNDER_TRAINING`, `SWEET_SPOT`, `ELEVATED_RISK`, `DANGER_ZONE`).
- [x] **Task 1.4 (GREEN):** Implement `AcwrCalculator.swift`.
- [x] **Task 1.5 (RED):** Write `MuscleVolumeCalculatorTests.swift` testing weekly set grouping and Israetel volume status classification.
- [x] **Task 1.6 (GREEN):** Implement `MuscleVolumeCalculator.swift`.
- [x] **Task 1.7 (RED/GREEN):** Implement `PersonalRecordTracker.swift` identifying max weight, max volume, and max estimated 1RM.

---

## Phase 2: Local SwiftData Analytics Adapter (TDD)

- [x] **Task 2.1 (RED):** Write `SwiftDataAnalyticsRepositoryTests.swift` testing calculations over an in-memory database of completed workouts.
- [x] **Task 2.2 (GREEN):** Implement `SwiftDataAnalyticsRepository.swift` extracting completed workouts from `ModelContext` and executing pure domain calculators.

---

## Phase 3: Remote REST Adapter (Spring Boot Parity)

- [x] **Task 3.1 (RED):** Write `RemoteAnalyticsRepositoryTests.swift` mocking `/api/analytics` endpoints.
- [x] **Task 3.2 (GREEN):** Implement `RemoteAnalyticsRepository.swift` calling Spring Boot endpoints.

---

## Phase 4: ViewModel Layer (`@Observable`, `@MainActor`)

- [x] **Task 4.1 (RED):** Write `AnalyticsDashboardViewModelTests.swift` verifying ACWR ratio binding, muscle volume chart state, and PR badge display.
- [x] **Task 4.2 (GREEN):** Implement `AnalyticsDashboardViewModel` with `@MainActor`.
- [x] **Task 4.3 (RED/GREEN):** Implement `OneRepMaxCalculatorViewModel` managing interactive slider inputs and percentage table.

---

## Phase 5: SwiftUI Views & Swift Charts

- [x] **Task 5.1:** Implement `AcwrGaugeCard.swift` with circular or linear color-banded risk zone meter.
- [x] **Task 5.2:** Implement `HypertrophyVolumeChart.swift` using horizontal `BarMark` with MEV/MAV benchmark lines.
- [x] **Task 5.3:** Implement `ExerciseProgressionChart.swift` using `LineMark` and `AreaMark` with interactive touch scrubbing.
- [x] **Task 5.4:** Implement `OneRepMaxCalculatorView.swift` with weight/reps wheels and percentage table ($95\%$ to $50\%$).
- [x] **Task 5.5:** Implement `PersonalRecordBadgeView.swift` with gold/silver trophy badges.

---

## Phase 6: Verification & Regression

- [x] **Task 6.1:** Run `FOSSTrainingTests` verifying 100% test pass rate across all sports science parity test suites.
