# Tasks: SPEC-06 — Athlete Profile & Powerlifting Scoring

**Module:** `SPEC-06_ATHLETE_PROFILE`  
**Status:** Ready to Execute  
**Spec Reference:** [SPEC-06_ATHLETE_PROFILE.md](../SPEC-06_ATHLETE_PROFILE.md)  
**Plan Reference:** [plan.md](plan.md)  

---

## Phase 1: Pure Domain & Mathematical Parity (TDD: Red ➔ Green)

- [x] **Task 1.1 (RED):** Write `DotsCalculatorTests.swift` testing 4th-degree polynomials for Male and Female athletes with boundary clipping ($40 \le x \le 210$).
- [x] **Task 1.2 (GREEN):** Implement `RelativeStrengthCalculator.swift` with DOTS formula and tier classification (`NOVICE` to `INTERNATIONAL_ELITE`).
- [x] **Task 1.3 (RED):** Write `WilksCalculatorTests.swift` testing 5th-degree polynomial evaluation against canonical IPF Wilks values.
- [x] **Task 1.4 (GREEN):** Implement Wilks calculation in `RelativeStrengthCalculator.swift`.
- [x] **Task 1.5 (RED):** Write `BodyweightMovingAverageTests.swift` testing 7-day Exponential Moving Average ($\alpha = 0.25$).
- [x] **Task 1.6 (GREEN):** Implement `BodyweightMovingAverageCalculator.swift`.

---

## Phase 2: SwiftData Repository Operations (TDD)

- [x] **Task 2.1 (RED):** Write `SwiftDataAthleteRepositoryTests.swift` testing profile persistence and weigh-in history fetching.
- [x] **Task 2.2 (GREEN):** Enhance `SwiftDataAthleteRepository.swift` with profile save/load and weigh-in deletion.
- [x] **Task 2.3 (RED/GREEN):** Implement domain mapping between `SDAthleteProfile` / `SDBodyweightEntry` and pure domain structs.

---

## Phase 3: Remote REST Adapter (Spring Boot Parity)

- [x] **Task 3.1 (RED):** Write `RemoteAthleteRepositoryTests.swift` mocking `/api/athlete` endpoints.
- [x] **Task 3.2 (GREEN):** Implement `RemoteAthleteRepository.swift` with DTO serialization.

---

## Phase 4: ViewModel Layer (`@Observable`, `@MainActor`)

- [x] **Task 4.1 (RED):** Write `AthleteProfileViewModelTests.swift` testing Big 3 total computation, DOTS score derivation, and weight log updates.
- [x] **Task 4.2 (GREEN):** Implement `AthleteProfileViewModel` with `@MainActor`.
- [x] **Task 4.3 (RED/GREEN):** Implement `BodyweightLogViewModel` handling rapid morning weigh-in entry.

---

## Phase 5: SwiftUI Views & Swift Charts

- [x] **Task 5.1:** Implement `AthleteProfileView.swift` showing avatar, biometric badge, and Big 3 compound total summary.
- [x] **Task 5.2:** Implement `RelativeStrengthCard.swift` with DOTS/Wilks segmented picker and tier classification badge.
- [x] **Task 5.3:** Implement `BodyweightTrendChart.swift` using `PointMark` for raw weigh-ins and `LineMark` for 7-day EMA.
- [x] **Task 5.4:** Implement `LogWeightSheet.swift` with large numeric keypad, quick adjustments (+0.2 / -0.2 kg), and morning notes.

---

## Phase 6: Verification & Regression

- [x] **Task 6.1:** Run `FOSSTrainingTests` verifying 100% test pass rate for all DOTS, Wilks, and bodyweight moving average tests.
