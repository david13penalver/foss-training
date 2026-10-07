# Implementation Plan: SPEC-02 — Session Templates & Workout Builder

**Module:** `SPEC-02_SESSION_TEMPLATES`  
**Status:** Planned  
**Target:** iOS 26+ (Swift 6, SwiftUI, SwiftData, Swift Testing)  
**Spec Document:** [SPEC-02_SESSION_TEMPLATES.md](../SPEC-02_SESSION_TEMPLATES.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph UI Layer ["Features/Sessions (SwiftUI)"]
        SLV["SessionListView"]
        SBV["SessionBuilderView"]
        SPS["SessionPartSectionView"]
        STB["SetsTableEditorView"]
        VM["SessionListViewModel / SessionBuilderViewModel"]
    end

    subgraph Domain Layer ["Domain (Pure Swift)"]
        SES["Session (Aggregate)"]
        SEI["SessionExerciseItem"]
        RS["ResistanceSet"]
        PORT["SessionRepository (Protocol)"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDSR["SwiftDataSessionRepository"]
        SDS["@Model SDSession"]
        RSR["RemoteSessionRepository"]
        DTO["SessionResponseDto"]
    end

    SLV --> VM
    SBV --> VM
    SPS --> VM
    STB --> VM
    VM --> PORT
    PORT <|.. SDSR
    PORT <|.. RSR
    SDSR --> SDS
    SDS --> SES
    RSR --> DTO
    DTO --> SES
```

### Architectural Invariants
1. **Multi-Part Structure:** Every session partitions exercises into 3 distinct parts: `WARM_UP`, `MAIN`, and `COOLDOWN`.
2. **Cascade Lifetime Rule:** Deleting a session template permanently cascades deletion to all child exercise items and target set configurations.
3. **Cloning Independence:** Cloning creates a deep, disconnected replica titled `"{Name} (Copy)"` with newly generated IDs so edits to the clone never mutate the origin template.
4. **Workout Transition Contract:** Tapping *"Start This Workout"* converts the static `Session` template into a live mutable `Training` instance managed by SPEC-03.

---

## 2. Data Contracts & Mapping

### 2.1 Domain Aggregate
- `Session`: `id: Int`, `name: String`, `description: String?`, `notes: String?`, `estimatedDurationMinutes: Int?`, `exercises: [SessionExerciseItem]`.
- `SessionExerciseItem`: `id: String`, `orderIndex: Int`, `exerciseId: Int`, `exerciseName: String`, `part: SessionPartEnum`, `restSeconds: Int`, `sets: [ResistanceSet]`.
- `ResistanceSet`: `id: String`, `setNumber: Int`, `setType: SetType`, `weightKg: Double`, `repetitions: Int`, `rpe: Double?`, `restSeconds: Int`, `isCompleted: Bool`.

### 2.2 SwiftData Persistence Models
- `@Model final class SDSession`: Primary entity holding metadata and relationship `var sessionExercises: [SDSessionExercise]`.
- `@Model final class SDSessionExercise`: Junction entity holding ordering, session part, and `var sets: [SDResistanceSet]`.
- `@Model final class SDResistanceSet`: Set targets.

### 2.3 Remote REST Parity
- Parity with Spring Boot `/api/sessions` (`SessionRequestDto`, `SessionResponseDto`, `POST /api/sessions/{id}/clone`).

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/Session.swift` (Contains `Session`, `SessionExerciseItem`, `ResistanceSet`)
- `FOSSTraining/Domain/Enums/SessionPartEnum.swift`
- `FOSSTraining/Domain/Enums/SetType.swift`
- `FOSSTraining/Domain/Repositories/SessionRepository.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Models/SDSession.swift`
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataSessionRepository.swift`
- `FOSSTraining/Data/Remote/Repositories/RemoteSessionRepository.swift`

### Features & Presentation
- `FOSSTraining/Features/Sessions/ViewModels/SessionListViewModel.swift`
- `FOSSTraining/Features/Sessions/ViewModels/SessionBuilderViewModel.swift`
- `FOSSTraining/Features/Sessions/Views/SessionListView.swift`
- `FOSSTraining/Features/Sessions/Views/SessionDetailView.swift`
- `FOSSTraining/Features/Sessions/Views/SessionBuilderView.swift`
- `FOSSTraining/Features/Sessions/Components/SessionPartSectionView.swift`
- `FOSSTraining/Features/Sessions/Components/SetsTableEditorView.swift`

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/SessionDomainTests.swift`
- `FOSSTrainingTests/Data/SwiftDataSessionRepositoryTests.swift`
- `FOSSTrainingTests/Data/RemoteSessionRepositoryTests.swift`
- `FOSSTrainingTests/Features/SessionBuilderViewModelTests.swift`

---

## 4. Testing Strategy

1. **Domain Tests:** Verify session validation (non-empty name, non-negative duration), set volume calculations, and exercise ordering integrity.
2. **Repository Tests:** In-memory SwiftData tests verifying that deep cloning produces identical exercise/set arrays with independent IDs, and that deleting a session deletes all child sets.
3. **ViewModel Tests:** Test reordering exercises across parts via drag-and-drop, adding/deleting sets, and transitioning template to active training.
