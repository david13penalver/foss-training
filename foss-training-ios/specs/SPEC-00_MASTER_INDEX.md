# FOSS Training — iOS Master Specifications Catalog (SPEC-01 to SPEC-08)

**Version:** 1.0.0  
**Target Platform:** iOS 26+ (Swift 6, SwiftUI, SwiftData, Swift Testing)  
**Architecture:** MVVM + Hexagonal Ports & Adapters  
**Status:** Canonical Master Specification  

---

## Index of Detailed Specifications, Plans & Tasks

| Module ID | Specification (`WHAT & WHY`) | Technical Plan (`HOW`) | Atomic Tasks (`TDD STEPS`) |
|---|---|---|---|
| **SPEC-01** | [Exercise Catalog & Management](SPEC-01_EXERCISE_MANAGEMENT.md) | [SPEC-01 Plan](SPEC-01_EXERCISE_MANAGEMENT/plan.md) | [SPEC-01 Tasks](SPEC-01_EXERCISE_MANAGEMENT/tasks.md) |
| **SPEC-02** | [Session Templates & Workout Builder](SPEC-02_SESSION_TEMPLATES.md) | [SPEC-02 Plan](SPEC-02_SESSION_TEMPLATES/plan.md) | [SPEC-02 Tasks](SPEC-02_SESSION_TEMPLATES/tasks.md) |
| **SPEC-03** | [Live Workout Execution & Tracker](SPEC-03_LIVE_WORKOUT_TRACKER.md) | [SPEC-03 Plan](SPEC-03_LIVE_WORKOUT_TRACKER/plan.md) | [SPEC-03 Tasks](SPEC-03_LIVE_WORKOUT_TRACKER/tasks.md) |
| **SPEC-04** | [Periodized Training Programs](SPEC-04_PERIODIZED_PROGRAMS.md) | [SPEC-04 Plan](SPEC-04_PERIODIZED_PROGRAMS/plan.md) | [SPEC-04 Tasks](SPEC-04_PERIODIZED_PROGRAMS/tasks.md) |
| **SPEC-05** | [Sports Science & Analytics](SPEC-05_SPORTS_SCIENCE_ANALYTICS.md) | [SPEC-05 Plan](SPEC-05_SPORTS_SCIENCE_ANALYTICS/plan.md) | [SPEC-05 Tasks](SPEC-05_SPORTS_SCIENCE_ANALYTICS/tasks.md) |
| **SPEC-06** | [Athlete Profile & Powerlifting Scoring](SPEC-06_ATHLETE_PROFILE.md) | [SPEC-06 Plan](SPEC-06_ATHLETE_PROFILE/plan.md) | [SPEC-06 Tasks](SPEC-06_ATHLETE_PROFILE/tasks.md) |
| **SPEC-07** | [Data Sovereignty & Migration Bridge](SPEC-07_DATA_SOVEREIGNTY.md) | [SPEC-07 Plan](SPEC-07_DATA_SOVEREIGNTY/plan.md) | [SPEC-07 Tasks](SPEC-07_DATA_SOVEREIGNTY/tasks.md) |
| **SPEC-08** | [Theming, Settings & Ecosystem](SPEC-08_THEMING_AND_SETTINGS.md) | [SPEC-08 Plan](SPEC-08_THEMING_AND_SETTINGS/plan.md) | [SPEC-08 Tasks](SPEC-08_THEMING_AND_SETTINGS/tasks.md) |

---

## SPEC-01: Exercise Catalog & Management

### 1.1 Scope & Purpose
Defines the management, classification, querying, and soft-deletion of the exercise catalog across resistance, endurance, and mobility modalities.

### 1.2 Functional Requirements
- **FR-01.1 (Catalog Querying):** The system shall allow users to search exercises by name, targeted muscle, or tag via a native `.searchable` bar with debounced filtering.
- **FR-01.2 (Category Filtering):** The system shall provide category filter chips (`ALL`, `RESISTANCE`, `ENDURANCE`, `MOBILITY`).
- **FR-01.3 (Exercise Creation):** The system shall provide a modal sheet allowing users to create a custom exercise with validation (name $\ge 2$ characters, at least 1 equipment category, difficulty level).
- **FR-01.4 (Modality Metrics):**
  - *Resistance:* Must capture primary muscle group, secondary muscle groups (array), and movement pattern (`SQUAT`, `HINGE`, `PUSH`, `PULL`, `CARRY`, `ROTATION`, `ISOLATION`).
  - *Endurance:* Must capture endurance type (`Running`, `Cycling`, `Rowing`, `Swimming`).
  - *Mobility:* Must capture mobility type (`Dynamic`, `Static`, `PNF`) and target joints (`Hip`, `Shoulder`, `Thoracic`, etc.).
- **FR-01.5 (Instructions & Safety):** Must allow adding reorderable step-by-step instructions, common mistakes, and safety guidelines.
- **FR-01.6 (Exercise Editing):** Must allow modifying custom exercises while preserving historical integrity in previously completed workouts.
- **FR-01.7 (Soft-Delete):** Deleting an exercise shall set `isActive = false` rather than a hard delete, ensuring historical workouts remain valid.

### 1.3 Data Contract
```swift
struct Exercise: Identifiable, Codable, Hashable, Sendable {
    let id: Int
    var name: String
    var description: String?
    var images: [String]
    var video: String?
    var primaryCategory: ExerciseCategory
    var secondaryCategories: [ExerciseCategory]
    var primaryMuscleGroup: String?
    var secondaryMuscleGroups: [String]
    var movementPattern: MovementPattern?
    var enduranceType: String?
    var mobilityType: String?
    var targetJoints: [String]
    var equipmentRequired: [EquipmentCategory]
    var difficultyLevel: DifficultyLevel
    var stepByStepInstructions: [String]
    var commonMistakes: [String]
    var safetyTips: [String]
    var tags: [String]
    var isActive: Bool
}
```

### 1.4 Dual-Tier Implementation
- **Local:** `SwiftDataExerciseRepository` reading/writing `@Model SDExercise`.
- **Premium:** `RemoteExerciseRepository` calling `GET /api/exercises`, `POST /api/exercises`, `PUT /api/exercises/{id}`, `DELETE /api/exercises/{id}`.

### 1.5 TDD Acceptance Criteria
- [x] Exercise with empty name throws validation error.
- [x] Soft-deleted exercise is omitted from active catalog queries.
- [x] Category filtering correctly isolates modalities.

---

## SPEC-02: Session Templates & Workout Builder

### 2.1 Scope & Purpose
Defines reusable workout templates specifying exercises, set targets, rep ranges, load, and rest periods.

### 2.2 Functional Requirements
- **FR-02.1 (Template Creation):** The system shall allow creating templates with name, description, notes, and estimated duration (minutes).
- **FR-02.2 (Multi-Part Workout Sequencing):** Exercises must be assignable to one of three sections: `WARM_UP`, `MAIN`, or `COOLDOWN`.
- **FR-02.3 (Exercise Picker):** Allows picking exercises from the catalog and inserting them into the selected section.
- **FR-02.4 (Drag-and-Drop Reordering):** Exercises within or between sections must be reorderable using native SwiftUI `.onMove`.
- **FR-02.5 (Sets Builder):** For each exercise, users can define:
  - Set Number ($1, 2, 3\dots$)
  - Set Type (`NORMAL`, `WARM_UP`, `DROP_SET`, `FAILURE`, `REST_PAUSE`)
  - Target load (kg) and Target repetitions
  - Target rest seconds (30s to 300s)
- **FR-02.6 (Clone Template):** 1-tap cloning action creating a copy named `"{Name} (Copy)"`.
- **FR-02.7 (Delete Template):** Swipe-to-delete with confirmation modal.
- **FR-02.8 (Start Workout):** *"Start This Workout"* CTA button converting the template into an active live `Training` instance.

### 2.3 Data Contract
```swift
struct Session: Identifiable, Codable, Hashable, Sendable {
    let id: Int
    var name: String
    var description: String?
    var notes: String?
    var estimatedDurationMinutes: Int?
    var exercises: [SessionExerciseItem]
}
```

### 2.4 Dual-Tier Implementation
- **Local:** `SwiftDataSessionRepository` managing `SDSession` with cascade deletion rules.
- **Premium:** `RemoteSessionRepository` calling `GET /api/sessions`, `POST /api/sessions`, `POST /api/sessions/{id}/clone`.

### 2.5 TDD Acceptance Criteria
- [x] Template cloning copies all exercise items, sets, and rest intervals.
- [x] Deleting a template cascades delete to its child session exercises and sets.

---

## SPEC-03: Live Workout Execution & Tracker

### 3.1 Scope & Purpose
Governs real-time workout execution, stopwatch timers, set logging with tactile haptics, rest countdowns, and workout completion summaries.

### 3.2 Functional Requirements
- **FR-03.1 (Lifecycle State Machine):**
  - Permitted transitions: `PLANNED` $\rightarrow$ `IN_PROGRESS` $\rightleftarrows$ `PAUSED` $\rightarrow$ `COMPLETED` or `CANCELLED`.
  - Invariants: Cannot complete an unstarted workout; cannot start a completed or cancelled workout.
- **FR-03.2 (Active Workout HUD):**
  - Full-screen modal presentation (`.fullScreenCover`).
  - Sticky header showing elapsed stopwatch (`TimelineView`) and progress gauge (`completedSets / totalSets`).
- **FR-03.3 (Interactive Set Logging):**
  - Numeric stepper/inputs for weight (kg) and reps.
  - Completion toggle triggers tactile haptic feedback (`.sensoryFeedback(.success)`).
  - Toggling set complete automatically initiates the Rest Timer.
- **FR-03.4 (Rest Timer & ActivityKit):**
  - Floating countdown banner displaying remaining rest seconds.
  - Integration with **ActivityKit (Live Activity & Dynamic Island)** showing rest countdown on the Lock Screen.
  - Double-tap haptic and audio cue upon timer expiration.
- **FR-03.5 (Complete Workout Flow):**
  - End-of-workout modal capturing overall Session RPE (1.0 to 10.0 scale) and workout notes.
  - Summary metrics: Total volume lifted ($\sum kg \times reps$), total sets completed, duration.
- **FR-03.6 (History Log):**
  - Chronological list of completed, planned, and cancelled workouts with status badges.

### 3.3 Data Contract
```swift
struct Training: Identifiable, Codable, Hashable, Sendable {
    let id: Int
    var name: String
    var description: String?
    var trainingDate: Date
    var startTime: Date?
    var endTime: Date?
    var status: TrainingStatus
    var notes: String?
    var overallRpe: Double?
    var programId: Int?
    var loggedExercises: [SessionExerciseItem]
}
```

### 3.4 Dual-Tier Implementation
- **Local:** `SwiftDataTrainingRepository` persisting live updates locally.
- **Premium:** `RemoteTrainingRepository` calling `POST /api/trainings/{id}/start`, `/sets`, `/complete`.

### 3.5 TDD Acceptance Criteria
- [x] Toggling set completion recalculates total workout volume.
- [x] Status transitions obey lifecycle rules.

---

## SPEC-04: Periodized Training Programs

### 4.1 Scope & Purpose
Manages multi-week periodized cycles (Hypertrophy, Strength, Peaking) with workout scheduling and adherence tracking.

### 4.2 Functional Requirements
- **FR-04.1 (Program Definition):** Name, description, duration in weeks (4, 8, 12 weeks), target level (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`), periodization type (`LINEAR`, `UNDULATING`, `BLOCK`).
- **FR-04.2 (Weekly Schedule Mapping):** Defines workout days mapping to specific Session templates.
- **FR-04.3 (Schedule Generator):** Generates scheduled `Training` records for future dates based on starting date.
- **FR-04.4 (Adherence Calculation):** Calculates adherence percentage:
  $$\text{Adherence Rate} = \frac{\text{Completed Workouts}}{\text{Scheduled Workouts}} \times 100\%$$
  Displayed with visual circular progress ring.

### 4.3 Data Contract
```swift
struct TrainingProgram: Identifiable, Codable, Hashable, Sendable {
    let id: Int
    var name: String
    var description: String?
    var durationWeeks: Int
    var periodizationType: PeriodizationType
    var level: DifficultyLevel
    var workouts: [ProgramWorkoutItem]
    var isActive: Bool
}
```

---

## SPEC-05: Sports Science & Analytics

### 5.1 Scope & Purpose
Provides evidence-based sports science analytics: workload ratios (injury prevention), volume tracking, and 1RM estimations.

### 5.2 Functional Requirements
- **FR-05.1 (Acute:Chronic Workload Ratio - ACWR):**
  - Session-RPE Workload: $\text{Session Load} = \text{Duration (min)} \times \text{RPE}$
  - Acute Load = 7-day sum; Chronic Load = 28-day sum / 4.
  - $\text{ACWR} = \frac{\text{Acute Load}}{\text{Chronic Load}}$
  - Risk Zones:
    - `LOW`: $< 0.8$ (Under-training)
    - `OPTIMAL`: $0.8 \le \text{ACWR} \le 1.3$ (Sweet spot)
    - `CAUTION`: $1.3 < \text{ACWR} \le 1.5$ (Elevated risk)
    - `HIGH`: $> 1.5$ (Danger zone)
- **FR-05.2 (1RM Estimator):**
  - Supports 6 canonical formulas:
    - *Epley:* $w \times (1 + r / 30)$
    - *Brzycki:* $w \times (36 / (37 - r))$
    - *Lombardi:* $w \times r^{0.10}$
    - *Mayhew:* $(100 \times w) / (52.2 + 41.9 \times e^{-0.055 \times r})$
    - *O'Conner:* $w \times (1 + 0.025 \times r)$
    - *Wathen:* $(100 \times w) / (48.8 + 53.8 \times e^{-0.075 \times r})$
- **FR-05.3 (Volume by Muscle Group):**
  - Swift Charts bar chart aggregating total volume (kg) per muscle group over 7-day, 30-day, and all-time windows.
- **FR-05.4 (Personal Records Tracker):**
  - Highest weight lifted, highest set volume, and highest estimated 1RM for each exercise.

### 5.3 TDD Acceptance Criteria
- [x] All 6 1RM formulas produce identical results to Java backend calculators.
- [x] ACWR calculation assigns correct risk zone.

---

## SPEC-06: Athlete Profile & Powerlifting Scoring

### 6.1 Scope & Purpose
Tracks athlete bodyweight trends and computes standardized powerlifting relative strength coefficients.

### 6.2 Functional Requirements
- **FR-06.1 (Daily Bodyweight Logging):** Fast numeric entry for bodyweight (kg) with date and optional notes.
- **FR-06.2 (Bodyweight Trend Line):** Swift Charts visualization showing historical entries with a 7-day exponential moving average.
- **FR-06.3 (Powerlifting Relative Strength Scoring):**
  - Computes **DOTS Score** using 4th-degree polynomial equations for Male and Female athletes.
  - Computes **Wilks Score** using 5th-degree polynomial equations for Male and Female athletes.
  - Produces standardized classification: *Novice*, *Intermediate*, *Advanced*, *Elite*, *International Elite*.

### 6.3 Data Contract
```swift
struct RelativeStrengthScore: Codable, Sendable {
    let totalWeightKg: Double
    let bodyweightKg: Double
    let gender: AthleteGender
    let relativeStrengthRatio: Double
    let dotsScore: Double
    let wilksScore: Double
    let classification: String
}
```

### 6.4 TDD Acceptance Criteria
- [x] DOTS and Wilks scores match IPF official tables and backend outputs within $\pm 0.01$.

---

## SPEC-07: Data Sovereignty & Migration Bridge

### 7.1 Scope & Purpose
Guarantees 100% data ownership with full backup export/import and seamless local-to-cloud migration.

### 7.2 Functional Requirements
- **FR-07.1 (Full JSON Snapshot Export):**
  - Serializes all Exercises, Sessions, Trainings, and Bodyweight entries into `BackupDataPayload`.
  - Exportable via native `ShareLink`.
  - Format 100% identical to backend `/api/data/export/backup`.
- **FR-07.2 (Full JSON Snapshot Restore):**
  - Reads a `.json` backup file via `.fileImporter` and imports all records into SwiftData.
- **FR-07.3 (Workouts CSV Export):**
  - Exports spreadsheet-ready CSV containing training IDs, dates, exercises, set numbers, weights, reps, and RPE.
- **FR-07.4 (1-Click Local-to-Cloud Migration Bridge):**
  - When switching to Premium, exports local SwiftData and uploads directly to `POST /api/data/import/backup`.

### 7.5 TDD Acceptance Criteria
- [x] Full export/import roundtrip restores all records without data loss.

---

## SPEC-08: Theming, Settings & Ecosystem

### 8.1 Scope & Purpose
Manages user visual preferences, operating tier switches, and system integrations.

### 8.2 Functional Requirements
- **FR-08.1 (Synchronous Color Customization):**
  - 6 energetic accent presets: *Volt Lime*, *Amber Blaze*, *Electric Blue*, *Crimson Pulse*, *Cyber Violet*, *Titanium Monochrome*.
  - Surface styles: *OLED Pure Black* (#000000), *Charcoal Slate* (#161618), *System Adaptive*.
  - **Storage Invariant:** Must be stored in `@AppStorage` / `UserDefaults` via `ThemeManager`, NEVER in SwiftData.
- **FR-08.2 (Operating Mode Switcher):**
  - Picker switching between **Local (SwiftData)** and **Premium (Cloud API)**.
  - Server URL text field with live latency connection test ping.
- **FR-08.3 (ActivityKit Rest Timer):**
  - Displays live rest timer countdown in Dynamic Island and on Lock Screen.
