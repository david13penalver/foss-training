# Implementation Plan: SPEC-04 — Periodized Training Programs

**Module:** `SPEC-04_PERIODIZED_PROGRAMS`  
**Status:** Planned  
**Target:** iOS 26+ (Swift 6, SwiftUI, SwiftData, Swift Testing)  
**Spec Document:** [SPEC-04_PERIODIZED_PROGRAMS.md](../SPEC-04_PERIODIZED_PROGRAMS.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph Presentation ["Features/Programs (SwiftUI)"]
        PLV["ProgramsListView"]
        PDV["ProgramDetailView"]
        GSS["GenerateScheduleSheet"]
        PAC["ProgramAdherenceCard"]
        VM["ProgramsListViewModel / ProgramDetailViewModel"]
    end

    subgraph Domain Layer ["Domain (Pure Swift)"]
        TP["TrainingProgram (Aggregate)"]
        PW["ProgramWorkout (Entity)"]
        AC["ProgramAdherenceCalculator (Pure Logic)"]
        PORT["TrainingProgramRepository (Protocol)"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDPR["SwiftDataTrainingProgramRepository"]
        SDTP["@Model SDTrainingProgram"]
        SDPW["@Model SDProgramWorkout"]
        RPR["RemoteTrainingProgramRepository"]
    end

    PLV --> VM
    PDV --> VM
    GSS --> VM
    PAC --> VM
    VM --> PORT
    PORT <|.. SDPR
    PORT <|.. RPR
    SDPR --> SDTP
    SDPR --> SDPW
    SDTP --> TP
```

### Architectural Invariants
1. **Adherence Engine Parity:** `ProgramAdherenceCalculator` is implemented as a pure Swift stateless domain service matching `ProgramAdherenceCalculator.java` mathematical outputs.
2. **Schedule Generation Invariant:** `generateSchedule(startDate:)` creates $W \times |\text{workouts}|$ concrete `Training` aggregate instances with status `PLANNED`, assigned correct calendar dates mapped to Monday-Sunday offsets.
3. **Cascade Lifetime Rule:** Deleting a program cascades deletion to child `ProgramWorkout` records while leaving already completed historical `Training` sessions intact.
4. **Hermetic Testing:** All schedule generation and adherence math tests execute without database dependencies.

---

## 2. Data Contracts & Mapping

### 2.1 Domain Models
- `TrainingProgram`: `id: Int`, `name: String`, `description: String?`, `durationWeeks: Int`, `periodizationType: PeriodizationType`, `level: ProgramLevel`, `workouts: [ProgramWorkout]`, `isActive: Bool`.
- `ProgramWorkout`: `id: UUID`, `dayOfWeek: Int` ($1 \dots 7$), `focus: String?`, `session: Session`.
- `ProgramAdherence`: Complete compliance metrics including `overallCompletionRate`, `currentAdherenceRate`, `currentStreak`, `longestStreak`, `status`, `weeklyBreakdowns`.

### 2.2 SwiftData Models
- `@Model final class SDTrainingProgram`: Holds scalar properties and `@Relationship(deleteRule: .cascade) var workouts: [SDProgramWorkout]`.
- `@Model final class SDProgramWorkout`: Holds `dayOfWeek`, `focus`, and `@Relationship var session: SDSession`.

### 2.3 Remote REST Parity
- Parity with Spring Boot `/api/programs` (`TrainingProgramRequestDto`, `TrainingProgramResponseDto`, `POST /{id}/generate-schedule`, `POST /{id}/clone`, `GET /{id}/adherence`).

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/TrainingProgram.swift`
- `FOSSTraining/Domain/Models/ProgramWorkout.swift`
- `FOSSTraining/Domain/Models/ProgramAdherence.swift`
- `FOSSTraining/Domain/Enums/PeriodizationType.swift`
- `FOSSTraining/Domain/Enums/ProgramLevel.swift`
- `FOSSTraining/Domain/Enums/ProgramAdherenceStatus.swift`
- `FOSSTraining/Domain/Services/ProgramAdherenceCalculator.swift`
- `FOSSTraining/Domain/Repositories/TrainingProgramRepository.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Models/SDTrainingProgram.swift`
- `FOSSTraining/Data/SwiftData/Models/SDProgramWorkout.swift`
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataTrainingProgramRepository.swift`
- `FOSSTraining/Data/Remote/Repositories/RemoteTrainingProgramRepository.swift`

### Features & Presentation
- `FOSSTraining/Features/Programs/ViewModels/ProgramsListViewModel.swift`
- `FOSSTraining/Features/Programs/ViewModels/ProgramDetailViewModel.swift`
- `FOSSTraining/Features/Programs/Views/ProgramsListView.swift`
- `FOSSTraining/Features/Programs/Views/ProgramDetailView.swift`
- `FOSSTraining/Features/Programs/Views/ProgramEditorView.swift`
- `FOSSTraining/Features/Programs/Views/GenerateScheduleSheet.swift`
- `FOSSTraining/Features/Programs/Components/ProgramAdherenceCard.swift`

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/ProgramDomainTests.swift`
- `FOSSTrainingTests/Domain/ProgramAdherenceCalculatorTests.swift`
- `FOSSTrainingTests/Domain/ProgramScheduleGenerationTests.swift`
- `FOSSTrainingTests/Data/SwiftDataTrainingProgramRepositoryTests.swift`
- `FOSSTrainingTests/Features/ProgramsListViewModelTests.swift`

---

## 4. Testing Strategy

1. **Domain Validation Tests:** Ensure duration $> 0$, days of week $1 \dots 7$, non-empty names.
2. **Schedule Generation Tests:** Verify exact calendar date calculations, microcycle numbering ("Week 1 Day 1"), and start date adjustments.
3. **Adherence Math Tests:** Verify $0\%$, $100\%$, and partial streak tracking with `NOT_STARTED`, `ON_TRACK`, `BEHIND`, and `AT_RISK` thresholds.
4. **Repository Tests:** In-memory SwiftData verification of cascade deletion, cloning, and program retrieval.
