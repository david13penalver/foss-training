# SPEC-04: Periodized Training Programs — Detailed Specification

**Module:** Periodized Training Programs & Schedule Generator  
**Status:** Ready for Review (Specification Only — No Implementation Yet)  
**Target:** iOS 26+ (Swift 6, SwiftUI, SwiftData, Swift Testing)  
**Backend Parity:** `TrainingProgramRestController.java`, `ProgramAdherenceCalculator.java` (`foss-training-api`)  

---

## 1. Executive Summary & User Stories

The **Periodized Training Programs** module enables athletes to structure multi-week progressive training cycles (mesocycles and macrocycles). It bridges high-level periodization schemes (Linear, Daily Undulating, Block) with daily gym execution by automatically generating scheduled calendar workouts and calculating mathematical compliance metrics (adherence %, streak tracking, and missed-workout detection).

### User Stories
- **US-04.1 (Program Discovery & Overview):** As an athlete, I want to browse my custom training programs with difficulty level badges (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`), duration chips (e.g., 4, 8, 12 weeks), and periodization models so that I can select an appropriate cycle.
- **US-04.2 (Program Builder):** As an athlete, I want to create and edit a multi-week program by naming it, specifying duration in weeks, selecting a periodization type, and assigning workout session templates to specific days of the week (Monday through Sunday) with custom focus labels (e.g., "Leg Hypertrophy", "Heavy Upper").
- **US-04.3 (Automated Schedule Generation):** As an athlete, I want to pick a start date and tap *"Generate Calendar Schedule"* so that the app populates my training calendar with scheduled workouts for the entire multi-week duration.
- **US-04.4 (Adherence & Compliance Tracking):** As an athlete, I want real-time visibility into my program adherence percentage, current workout streak, and weekly completion breakdowns so that I stay accountable to my training plan.
- **US-04.5 (Program Cloning):** As an athlete, I want to clone an existing program (e.g., *"Hypertrophy Block 1"*) into a new cycle (*"Hypertrophy Block 2"*) so that I can iteratively tweak volume without building from scratch.
- **US-04.6 (Soft-Delete & Archival):** As an athlete, I want to deactivate completed programs while preserving historical logged workouts and analytics.

---

## 2. Business Invariants & Mathematical Formulations

```mermaid
flowchart TD
    TP["TrainingProgram (Duration: N weeks)"] -->|Assigns Workouts| PW["ProgramWorkout (Day: 1..7, Session Template)"]
    TP -->|Generate Schedule(startDate)| CAL["Instantiated Scheduled Trainings in Calendar"]
    CAL -->|Completed Workouts| PAC["ProgramAdherenceCalculator"]
    PAC --> ADH["ProgramAdherence Metrics (Streak, %, Status)"]
```

### 2.1 Validation Invariants
1. **Name:** `name` must not be blank and must contain $\ge 2$ characters.
2. **Duration:** `durationWeeks` must be strictly $> 0$ and $\le 52$.
3. **Day of Week:** `dayOfWeek` in `ProgramWorkout` must be an integer between $1$ (Monday) and $7$ (Sunday).
4. **Session Template:** `session` reference must not be nil and must refer to an active `Session`.
5. **No Duplicate Day Conflicts:** A program cannot assign two conflicting primary workouts to the exact same day of the week unless explicitly tagged as two distinct split sessions (morning / evening).

### 2.2 Schedule Generation Algorithm
Given a `TrainingProgram` with duration $W$ weeks and a start date $D_0$:
1. Determine the Monday of the start week:
   $$D_{\text{base}} = \text{startOfWeek}(D_0)$$
2. For each week index $w \in 0 \dots (W - 1)$:
   - For each workout template $pw \in \text{program.workouts}$:
     - Calculate target date:
       $$D_{w, pw} = D_{\text{base}} + (w \times 7 \text{ days}) + ((pw.\text{dayOfWeek} - 1) \text{ days})$$
     - If $D_{w, pw} < D_0$ and $w == 0$, adjust or schedule for next occurrence.
     - Instantiate a new `Training` aggregate:
       - `session = pw.session`
       - `name = "\(program.name) - W\(w + 1)D\(pw.dayOfWeek): \(pw.focus ?? pw.session.name)"`
       - `trainingDate = D_{w, pw}`
       - `status = PLANNED`

### 2.3 Program Adherence Calculation Engine
The adherence engine matches `ProgramAdherenceCalculator.java` parity:

$$\text{Workouts Per Week} = |\text{program.workouts}|$$
$$\text{Expected Total Workouts} = W \times \text{Workouts Per Week}$$

$$\text{Overall Completion Rate} = \frac{\text{completedWorkouts}}{\text{expectedTotalWorkouts}} \times 100.0$$

$$\text{Current Adherence Rate} = \frac{\text{completedWorkouts}}{\text{scheduledWorkoutsUpToToday}} \times 100.0$$

#### Status Classification:
- **NOT_STARTED:** $0$ completed workouts.
- **ON_TRACK:** $\text{Current Adherence Rate} \ge 85.0\%$.
- **BEHIND:** $50.0\% \le \text{Current Adherence Rate} < 85.0\%$.
- **AT_RISK:** $0\% < \text{Current Adherence Rate} < 50.0\%$.
- **COMPLETED:** All scheduled workouts completed at program end.

---

## 3. Hexagonal Outbound Port Contract

```swift
public protocol TrainingProgramRepository: Sendable {
    /// Retrieves all active training programs
    func getPrograms() async throws -> [TrainingProgram]

    /// Retrieves a single program by ID with populated workouts
    func getProgram(id: Int) async throws -> TrainingProgram?

    /// Saves a new training program
    func createProgram(_ program: TrainingProgram) async throws -> TrainingProgram

    /// Updates an existing program
    func updateProgram(_ program: TrainingProgram) async throws -> TrainingProgram

    /// Deactivates / soft-deletes a program
    func deleteProgram(id: Int) async throws

    /// Verifies existence of a program by ID
    func programExists(id: Int) async throws -> Bool

    /// Generates concrete scheduled Training instances across the duration weeks
    func generateSchedule(programId: Int, startDate: Date) async throws -> [Training]

    /// Clones an existing program with an optional custom name
    func cloneProgram(id: Int, newName: String?) async throws -> TrainingProgram

    /// Calculates real-time adherence and compliance metrics
    func getProgramAdherence(id: Int) async throws -> ProgramAdherence
}
```

---

## 4. Domain Models & Value Objects

```swift
public enum PeriodizationType: String, Codable, CaseIterable, Sendable {
    case linear = "LINEAR"
    case undulating = "UNDULATING"
    case block = "BLOCK"
    case reverseLinear = "REVERSE_LINEAR"
    case conjugated = "CONJUGATED"

    public var displayName: String {
        switch self {
        case .linear: return "Linear Periodization"
        case .undulating: return "Daily Undulating (DUP)"
        case .block: return "Block Periodization"
        case .reverseLinear: return "Reverse Linear"
        case .conjugated: return "Conjugated / Westside"
        }
    }
}

public enum ProgramLevel: String, Codable, CaseIterable, Sendable {
    case beginner = "BEGINNER"
    case intermediate = "INTERMEDIATE"
    case advanced = "ADVANCED"
    case elite = "ELITE"
}

public struct ProgramWorkout: Identifiable, Codable, Hashable, Sendable {
    public var id: UUID = UUID()
    public var dayOfWeek: Int // 1 = Monday, 7 = Sunday
    public var focus: String?
    public var session: Session

    public var dayName: String {
        switch dayOfWeek {
        case 1: return "Monday"
        case 2: return "Tuesday"
        case 3: return "Wednesday"
        case 4: return "Thursday"
        case 5: return "Friday"
        case 6: return "Saturday"
        case 7: return "Sunday"
        default: return "Day \(dayOfWeek)"
        }
    }
}

public struct TrainingProgram: Identifiable, Codable, Hashable, Sendable {
    public let id: Int
    public var name: String
    public var description: String?
    public var durationWeeks: Int
    public var periodizationType: PeriodizationType
    public var level: ProgramLevel
    public var workouts: [ProgramWorkout]
    public var isActive: Bool

    public var totalWorkoutsPerCycle: Int {
        durationWeeks * workouts.count
    }
}

public enum ProgramAdherenceStatus: String, Codable, Sendable {
    case notStarted = "NOT_STARTED"
    case onTrack = "ON_TRACK"
    case behind = "BEHIND"
    case atRisk = "AT_RISK"
    case completed = "COMPLETED"
}

public struct WeeklyAdherenceBreakdown: Identifiable, Codable, Hashable, Sendable {
    public var id: Int { weekNumber }
    public let weekNumber: Int
    public let expectedWorkouts: Int
    public let completedWorkouts: Int
    public let adherenceRate: Double
}

public struct ProgramAdherence: Codable, Hashable, Sendable {
    public let programId: Int
    public let programName: String
    public let durationWeeks: Int
    public let totalScheduledWorkouts: Int
    public let completedWorkouts: Int
    public let inProgressWorkouts: Int
    public let plannedWorkouts: Int
    public let missedWorkouts: Int
    public let cancelledWorkouts: Int
    public let overallCompletionRate: Double
    public let currentAdherenceRate: Double
    public let currentStreak: Int
    public let longestStreak: Int
    public let status: ProgramAdherenceStatus
    public let statusDescription: String
    public let weeklyBreakdowns: [WeeklyAdherenceBreakdown]
}
```

---

## 5. Dual-Tier Persistence Architecture

### 5.1 Local Offline Tier (`SwiftDataTrainingProgramRepository`)
- **SwiftData Models:**
  - `@Model final class SDTrainingProgram`: Stores `id`, `name`, `desc`, `durationWeeks`, `periodizationType`, `level`, `isActive`. Has `@Relationship(deleteRule: .cascade) var workouts: [SDProgramWorkout]`.
  - `@Model final class SDProgramWorkout`: Stores `dayOfWeek`, `focus`, and links to `@Relationship var session: SDSession`.
- **Offline Schedule Generation:**
  - SwiftData repository implements local `generateSchedule`: directly creates `SDTraining` instances in the `ModelContext` matching the computed dates and links to the session templates.
- **Offline Adherence Calculation:**
  - Queries local `SDTraining` objects matching the program or session IDs, evaluates completed dates against calendar weeks, and constructs `ProgramAdherence`.

### 5.2 Premium Cloud Tier (`RemoteTrainingProgramRepository`)
- Direct REST synchronization with Spring Boot:
  - `GET /api/programs`: Fetches all programs.
  - `GET /api/programs/{id}`: Fetches program with workouts.
  - `POST /api/programs`: Creates new program (`TrainingProgramRequestDto`).
  - `PUT /api/programs/{id}`: Updates existing program.
  - `DELETE /api/programs/{id}`: Deletes/deactivates program.
  - `POST /api/programs/{id}/generate-schedule?startDate=YYYY-MM-DD`: Server-side generation of `TrainingResponseDto` list.
  - `POST /api/programs/{id}/clone`: Clones program on server.
  - `GET /api/programs/{id}/adherence`: Fetches `ProgramAdherenceResponseDto`.

---

## 6. UI / UX Design Specifications

### 6.1 Programs Catalog View (`ProgramsListView`)
- **Header:** Title *"Training Programs"*, search bar, and primary *"New Program"* glassmorphic button.
- **Filter Segmented Control:** Filter by Level (`All`, `Beginner`, `Intermediate`, `Advanced`).
- **Program Card Component:**
  - Title and Periodization Type pill badge.
  - Duration chip (e.g., `"8 Weeks • 4 days/wk"`).
  - Workouts grid preview showing assigned days (M, W, F, Sa).
  - Quick actions context menu: *Generate Schedule*, *Clone Program*, *Edit*, *Delete*.

### 6.2 Program Detail & Schedule Generator (`ProgramDetailView`)
- **Macrocycle Stats Bar:** Total weeks, total planned sessions, weekly training frequency.
- **Weekly Schedule Timetable:**
  - 7-day visual calendar column (Monday through Sunday).
  - Assigned workout cards with session duration and exercise count preview.
  - Empty day slots showing *"Rest Day"*.
- **Generate Schedule Modal Sheet (`GenerateScheduleSheet`):**
  - Interactive `DatePicker` selecting `startDate` (defaults to next Monday).
  - Preview list of calculated dates (Week 1 through Week N).
  - CTA button: *"Add X Workouts to Calendar"*.

### 6.3 Adherence Dashboard Card (`ProgramAdherenceCard`)
- **Ring Chart:** Circular progress gauge displaying `overallCompletionRate` with accent gradient.
- **Streak Flame Metric:** Current workout streak counter with flame icon.
- **Weekly Progress Grid:** Horizontal scroll or grid showing Week 1 to Week N with miniature dots:
  - Green: Completed.
  - Yellow: In Progress.
  - Gray: Upcoming.
  - Red: Missed.

---

## 7. TDD Test Plan

### 7.1 Domain & Mathematical Validation Tests (`ProgramDomainTests.swift`)
- [ ] `testProgramValidation_withEmptyName_throwsError`
- [ ] `testProgramValidation_withZeroDuration_throwsError`
- [ ] `testProgramValidation_withInvalidDayOfWeek_throwsError`
- [ ] `testProgramAdherenceCalculator_withZeroTrainings_returnsNotStarted`
- [ ] `testProgramAdherenceCalculator_withAllCompleted_returns100PercentAndOnTrack`
- [ ] `testProgramAdherenceCalculator_streakCalculation_incrementsProperly`

### 7.2 Schedule Generation Tests (`ProgramScheduleTests.swift`)
- [ ] `testGenerateSchedule_createsCorrectNumberOfTrainings` ($durationWeeks \times workouts.count$).
- [ ] `testGenerateSchedule_assignsCorrectDayOfWeekDates`.
- [ ] `testGenerateSchedule_namespacedWorkoutTitlesCorrectly`.

### 7.3 Repository Parity Tests (`TrainingProgramRepositoryTests.swift`)
- [ ] `testSwiftDataTrainingProgramRepository_crud_persistsRelationshipsCascade`
- [ ] `testSwiftDataTrainingProgramRepository_clone_createsIndependentDuplicate`
- [ ] `testRemoteTrainingProgramRepository_mapsBackendDtosProperly`

---

## 8. Implementation Checklist

- [ ] **Phase 1 (Domain Models):** Implement `PeriodizationType`, `ProgramLevel`, `ProgramWorkout`, `TrainingProgram`, `ProgramAdherence`.
- [ ] **Phase 2 (Ports):** Define `TrainingProgramRepository` protocol.
- [ ] **Phase 3 (SwiftData Adapter):** Implement `SDTrainingProgram`, `SDProgramWorkout`, and `SwiftDataTrainingProgramRepository`.
- [ ] **Phase 4 (Remote Adapter):** Implement `RemoteTrainingProgramRepository` with URLSession endpoints.
- [ ] **Phase 5 (ViewModels):** Build `ProgramsListViewModel`, `ProgramEditorViewModel`, `ProgramScheduleViewModel`.
- [ ] **Phase 6 (SwiftUI Views):** Build `ProgramsListView`, `ProgramDetailView`, `ProgramEditorView`, `GenerateScheduleSheet`.
- [ ] **Phase 7 (Adherence UI):** Build `ProgramAdherenceCard` and weekly breakdown timeline.
