# Phase 1: Domain Hardening, Value Objects & Rich Entities

## Overview
Phase 1 focuses on transforming the domain model from anemic data holders into a scientifically sound, strongly typed Domain-Driven Design (DDD) core. 

This includes:
1. Creating first-class immutable **Value Objects** (`Weight`, `Distance`, `Duration`, `Pace`, `Rpe`, `Tempo`, `HeartRateZone`).
2. Refactoring the **`SessionExercise` polymorphic hierarchy** and modeling realistic set-level tracking (`ResistanceSet`, `EnduranceInterval`, `MobilitySet`).
3. Relocating **`MovementPattern`** to `domain.model.exercise` as a top-level biomechanical classification.
4. Adding **business methods and validation invariants** to domain entities (`Exercise`, `Session`, `SessionPart`).
5. Cleaning up empty placeholder files ([`Training.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/training/Training.java)).

---

## User Review Required

> [!IMPORTANT]
> **Session Model Evolution**
> The current `SessionExercise` is an `abstract` class holding fields for all three modalities without concrete subclasses.
> We will refactor this into:
> - Base `abstract class SessionExercise` (common fields: `id`, `exercise`, `orderIndex`, `notes`).
> - Concrete subclasses:
>   - `ResistanceSessionExercise extends SessionExercise` (contains `List<ResistanceSet> sets`, `calculateVolume()`, `getWorkingSetsCount()`).
>   - `EnduranceSessionExercise extends SessionExercise` (contains `List<EnduranceInterval> intervals`, `calculateTotalDistance()`, `calculateTotalDuration()`).
>   - `MobilitySessionExercise extends SessionExercise` (contains `List<MobilitySet> sets`).
> - Set-level models:
>   - `ResistanceSet`: `setNumber`, `type` (`WARMUP`, `WORKING`, `DROP_SET`, `FAILURE`), `weight`, `reps`, `rpe`, `restSeconds`.
>   - `EnduranceInterval`: `intervalNumber`, `distance`, `duration`, `pace`, `avgHeartRate`, `avgPower`, `cadence`.
>   - `MobilitySet`: `setNumber`, `holdDuration`, `reps`, `isBilateral`.

> [!NOTE]
> **Value Objects Location**
> New value objects will be placed in `com.david13penalver.foss_training_api.domain.model.common.valueobjects` (or `domain.model.common`). They are pure Java records/classes with zero framework dependencies.

---

## Proposed Changes

### 1. Value Objects (`domain/model/common/`)

#### [NEW] [`Weight.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/Weight.java) & [`WeightUnit.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/WeightUnit.java)
- Immutable value object holding `double value` and `WeightUnit` (`KG`, `LBS`).
- Methods: `toKg()`, `toLbs()`, `plus(Weight)`, `times(double)`, validation (`value >= 0`).

#### [NEW] [`Distance.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/Distance.java) & [`DistanceUnit.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/DistanceUnit.java)
- Immutable value object holding `double value` and `DistanceUnit` (`METERS`, `KILOMETERS`, `MILES`).
- Methods: `toMeters()`, `toKilometers()`, `toMiles()`, `plus(Distance)`.

#### [NEW] [`Duration.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/Duration.java)
- Immutable value object holding `int totalSeconds`.
- Methods: `getSeconds()`, `getMinutes()`, `toFormattedString()` (e.g., `"01:30"`, `"45s"`), `plus(Duration)`.

#### [NEW] [`Pace.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/Pace.java)
- Calculated pace from `Distance` and `Duration` (e.g. seconds per km, minutes per mile string representation).

#### [NEW] [`Rpe.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/Rpe.java)
- Rate of Perceived Exertion (1.0–10.0 scale) and Reps in Reserve (`RIR` = $10 - RPE$).
- Methods: `isMaxEffort()`, `isWarmup()`, `getRir()`, validation ($1.0 \le \text{value} \le 10.0$).

#### [NEW] [`Tempo.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/Tempo.java)
- Cadence parser and validator: `eccentric`, `pauseEccentric`, `concentric`, `pauseConcentric` (e.g., `"3-1-1-0"`).
- Method: `getTotalRepDuration()`.

#### [NEW] [`HeartRateZone.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/common/HeartRateZone.java)
- Enum with 5 standard aerobic/anaerobic training zones (Zone 1 Recovery to Zone 5 Maximum).
- Helper: `fromHeartRate(int currentHr, int maxHr)`.

---

### 2. Biomechanics Placement (`domain/model/exercise/`)

#### [MODIFY] Relocate [`MovementPattern.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/exercise/MovementPattern.java)
- Move from `domain.model.exercise.mobility` to `domain.model.exercise` package.
- Update import references in `ResistanceMetrics.java`, `MovementPatternRestController.java`, use cases, and tests.

---

### 3. Session Hierarchy & Set Modeling (`domain/model/session/`)

#### [MODIFY] [`Session.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/Session.java)
- Add lifecycle methods: `start()`, `complete()`, `cancel()`, `calculateDuration()`, `calculateTotalVolume()`.
- Add fields: `startTime`, `endTime`, `notes`, `rpe`.

#### [MODIFY] [`SessionPart.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/SessionPart.java)
- Contains `List<SessionExercise> exercises`. Methods to add/order exercises.

#### [MODIFY] [`SessionExercise.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/SessionExercise.java)
- Base `abstract class SessionExercise` with `id`, `exercise`, `orderIndex`, `notes`.

#### [NEW] [`SetType.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/SetType.java)
- Enum: `WARMUP`, `WORKING`, `DROP_SET`, `MYOREP`, `FAILURE`.

#### [NEW] [`ResistanceSet.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/ResistanceSet.java)
- Individual resistance set: `setNumber`, `SetType`, `Weight`, `repetitions`, `Rpe`, `restSeconds`.
- Method: `calculateVolume()`.

#### [MODIFY] [`ResistanceSessionExercise.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/ResistanceSessionExercise.java)
- `extends SessionExercise`. Holds `List<ResistanceSet> sets`.
- Methods: `addSet(ResistanceSet)`, `calculateVolume()`, `getWorkingSetsCount()`.

#### [NEW] [`EnduranceInterval.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/EnduranceInterval.java)
- Individual interval: `intervalNumber`, `Distance`, `Duration`, `Pace`, `avgHeartRate`, `avgPower`, `cadence`.

#### [MODIFY] [`EnduranceSessionExercise.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/EnduranceSessionExercise.java)
- `extends SessionExercise`. Holds `List<EnduranceInterval> intervals`.
- Methods: `calculateTotalDistance()`, `calculateTotalDuration()`.

#### [NEW] [`MobilitySet.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/MobilitySet.java)
- Individual mobility set: `setNumber`, `Duration holdDuration`, `repetitions`, `isBilateral`.

#### [MODIFY] [`MobilitySessionExercise.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/session/MobilitySessionExercise.java)
- `extends SessionExercise`. Holds `List<MobilitySet> sets`.

---

### 4. Rich Entity Methods on `Exercise` (`domain/model/exercise/`)

#### [MODIFY] [`Exercise.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/exercise/Exercise.java)
- Add domain helper methods: `isResistance()`, `isEndurance()`, `isMobility()`, `hasMatchingMetrics()`, `deactivate()`, `activate()`.
- Add validation in constructors / builders.

#### [DELETE] [`Training.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/main/java/com/david13penalver/foss_training_api/domain/model/training/Training.java)
- Remove empty 0-byte stub file (program models will be introduced properly in Phase 3).

---

### 5. Unit Tests Updates (`src/test/java/unitary/.../domain/model/`)

#### [NEW] [`ValueObjectsTest.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/test/java/unitary/com/david13penalver/foss_training_api/domain/model/ValueObjectsTest.java)
- Unit tests for all value objects: `Weight` conversions, `Distance` conversions, `Duration` formatting, `Rpe` RIR calculation, `Tempo` parsing, `HeartRateZone` classification.

#### [MODIFY] [`SessionTest.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/test/java/unitary/com/david13penalver/foss_training_api/domain/model/SessionTest.java) & [`SessionExerciseMetricsTest.java`](file:///home/deivision/Projects/Programming/Spring/foss-training/foss-training-api/src/test/java/unitary/com/david13penalver/foss_training_api/domain/model/SessionExerciseMetricsTest.java)
- Update session tests to verify the new polymorphic inheritance hierarchy, set-level calculations (volume, total distance, duration), and session lifecycle transitions.

---

## Verification Plan

### Automated Tests
- Run complete test suite:
  ```sh
  ./mvnw clean test
  ```
- Run domain-specific unit tests:
  ```sh
  ./mvnw test -Dtest=*Test
  ```
- Verify 100% tests pass and zero compilation or execution warnings.
