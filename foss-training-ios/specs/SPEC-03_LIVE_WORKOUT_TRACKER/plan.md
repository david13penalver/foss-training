# Implementation Plan: SPEC-03 — Live Workout Execution & Tracker

**Module:** `SPEC-03_LIVE_WORKOUT_TRACKER`  
**Status:** Planned  
**Target:** iOS 26+ (Swift 6, SwiftUI, SwiftData, ActivityKit, Swift Testing)  
**Spec Document:** [SPEC-03_LIVE_WORKOUT_TRACKER.md](../SPEC-03_LIVE_WORKOUT_TRACKER.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph Presentation ["Features/Trainings (SwiftUI & ActivityKit)"]
        AWV["ActiveWorkoutView (HUD)"]
        RTB["RestTimerBannerView"]
        CWM["CompleteWorkoutModal"]
        THV["TrainingHistoryView"]
        VM["ActiveWorkoutViewModel (@Observable, @MainActor)"]
        AK["WorkoutActivityAttributes (Live Activity)"]
    end

    subgraph Domain Layer ["Domain (Pure Swift)"]
        TR["Training (Aggregate)"]
        SM["WorkoutStateMachine"]
        PORT["TrainingRepository (Protocol)"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDTR["SwiftDataTrainingRepository"]
        SDT["@Model SDTraining"]
        RTR["RemoteTrainingRepository"]
    end

    AWV --> VM
    RTB --> VM
    CWM --> VM
    THV --> VM
    VM --> AK
    VM --> PORT
    VM --> SM
    PORT <|.. SDTR
    PORT <|.. RTR
    SDTR --> SDT
    SDT --> TR
```

### Architectural Invariants
1. **Strict Lifecycle State Machine:** Status transitions follow `PLANNED` $\rightarrow$ `IN_PROGRESS` $\rightleftarrows$ `PAUSED` $\rightarrow$ `COMPLETED` or `CANCELLED`. Terminal states (`COMPLETED`, `CANCELLED`) reject subsequent modifications.
2. **Volume Invariant:** Total volume is strictly calculated from *completed* sets:
   $$\text{Total Volume (kg)} = \sum_{s \in \text{sets}, s.\text{isCompleted}} (s.\text{weightKg} \times s.\text{repetitions})$$
3. **Lock Screen Visibility (ActivityKit):** Active workout and rest countdown timers persist onto Dynamic Island and Lock Screen via `ActivityKit`.
4. **Haptic & Audio Feedback:** Set completion triggers `.sensoryFeedback(.success)`; rest timer zero triggers audio chime and double haptic pulse.

---

## 2. Data Contracts & Mapping

### 2.1 Domain Aggregate
- `Training`: `id: Int`, `name: String`, `description: String?`, `trainingDate: Date`, `startTime: Date?`, `endTime: Date?`, `status: TrainingStatus`, `notes: String?`, `overallRpe: Double?`, `programId: Int?`, `loggedExercises: [SessionExerciseItem]`.
- Status Enum: `TrainingStatus` (`PLANNED`, `IN_PROGRESS`, `PAUSED`, `COMPLETED`, `CANCELLED`).

### 2.2 Live Activity Attributes (`WorkoutActivityAttributes`)
```swift
public struct WorkoutActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        public var currentExerciseName: String
        public var currentSetNumber: Int
        public var totalSets: Int
        public var restTimeRemaining: TimeInterval?
        public var isRestActive: Bool
    }
    public var workoutName: String
    public var startTime: Date
}
```

### 2.3 Remote REST Parity
- Parity with Spring Boot `/api/trainings` (`TrainingResponseDto`, `POST /{id}/start`, `POST /{id}/pause`, `POST /{id}/resume`, `POST /{id}/complete`, `POST /sets`).

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/Training.swift` (State validation, volume calculation)
- `FOSSTraining/Domain/Enums/TrainingStatus.swift`
- `FOSSTraining/Domain/Repositories/TrainingRepository.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Models/SDTraining.swift`
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataTrainingRepository.swift`
- `FOSSTraining/Data/Remote/Repositories/RemoteTrainingRepository.swift`

### ActivityKit & Background
- `FOSSTraining/Core/LiveActivity/WorkoutActivityAttributes.swift`
- `FOSSTraining/Core/LiveActivity/WorkoutActivityManager.swift`

### Features & Presentation
- `FOSSTraining/Features/Trainings/ViewModels/ActiveWorkoutViewModel.swift`
- `FOSSTraining/Features/Trainings/ViewModels/TrainingHistoryViewModel.swift`
- `FOSSTraining/Features/Trainings/Views/ActiveWorkoutView.swift` (HUD, stopwatch, sets)
- `FOSSTraining/Features/Trainings/Views/RestTimerBannerView.swift` (Rest countdown overlay)
- `FOSSTraining/Features/Trainings/Views/CompleteWorkoutModal.swift` (RPE rating slider & notes)
- `FOSSTraining/Features/Trainings/Views/TrainingHistoryView.swift` (Timeline list)

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/TrainingStateMachineTests.swift`
- `FOSSTrainingTests/Data/SwiftDataTrainingRepositoryTests.swift`
- `FOSSTrainingTests/Features/ActiveWorkoutViewModelTests.swift`

---

## 4. Testing Strategy

1. **State Machine Tests:** Test all valid and invalid transitions; ensure starting sets `startTime`, completing sets `endTime`, and terminal states throw errors if altered.
2. **Volume Invariant Tests:** Verify sets with `isCompleted == false` contribute zero to volume; completing a set recalculates volume immediately.
3. **Rest Timer & ActivityKit Tests:** Unit test rest countdown duration calculation and Live Activity state emission.
4. **Repository Tests:** Verify that set logging persists in-memory SwiftData context without blocking the main actor.
