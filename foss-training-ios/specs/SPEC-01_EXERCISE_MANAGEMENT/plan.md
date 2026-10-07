# Implementation Plan: SPEC-01 — Exercise Catalog & Management

**Module:** `SPEC-01_EXERCISE_MANAGEMENT`  
**Status:** Planned  
**Target:** iOS 26+ (Swift 6, SwiftUI, SwiftData, Swift Testing)  
**Spec Document:** [SPEC-01_EXERCISE_MANAGEMENT.md](../SPEC-01_EXERCISE_MANAGEMENT.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph UI Layer ["Features/Exercises (SwiftUI)"]
        ELV["ExerciseListView"]
        EDV["ExerciseDetailView"]
        EEV["ExerciseEditorSheet"]
        VM["ExerciseListViewModel (@Observable, @MainActor)"]
    end

    subgraph Domain Layer ["Domain (Pure Swift)"]
        EX["Exercise (Aggregate)"]
        PORT["ExerciseRepository (Protocol)"]
        VAL["ExerciseValidator"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDR["SwiftDataExerciseRepository"]
        SDE["@Model SDExercise"]
        RER["RemoteExerciseRepository"]
        DTO["ExerciseResponseDto"]
    end

    ELV --> VM
    EDV --> VM
    EEV --> VM
    VM --> PORT
    PORT <|.. SDR
    PORT <|.. RER
    SDR --> SDE
    SDE --> EX
    RER --> DTO
    DTO --> EX
```

### Architectural Invariants
1. **Hexagonal Domain Isolation:** `Exercise` model and `ExerciseRepository` protocol have zero framework dependencies (no SwiftUI, no SwiftData, no Combine).
2. **Dual-Tier Dynamic Resolution:** The view model receives an injected `ExerciseRepository` protocol instance without knowing if it's talking to local SwiftData or the remote Spring Boot API.
3. **Soft-Delete Invariant:** Exercises are never hard-deleted from persistence. Deletion mutates `isActive = false` to safeguard historical workout telemetry.
4. **Hermetic Testing:** SwiftData repository tests run against in-memory configurations (`ModelConfiguration(isStoredInMemoryOnly: true)`).

---

## 2. Data Contracts & Mapping

### 2.1 Domain Aggregate
- `Exercise`: Complete domain entity capturing Resistance, Endurance, and Mobility parameters.
- Value Enums: `ExerciseCategory`, `MovementPattern`, `EquipmentCategory`, `DifficultyLevel`.

### 2.2 Persistence Entities (`SDExercise`)
- Attributes: `id: Int` (unique), `name: String`, `exerciseDescription: String?`, `images: [String]`, `videoUrl: String?`, `primaryCategory: String`, `secondaryCategories: [String]`, `primaryMuscleGroup: String?`, `secondaryMuscleGroups: [String]`, `movementPattern: String?`, `enduranceType: String?`, `mobilityType: String?`, `targetJoints: [String]`, `equipmentRequired: [String]`, `difficultyLevel: String`, `stepByStepInstructions: [String]`, `commonMistakes: [String]`, `safetyTips: [String]`, `tags: [String]`, `isActive: Bool`, `createdAt: Date?`, `updatedAt: Date?`.
- Mapping: Explicit bi-directional mapping methods `SDExercise.toDomain() -> Exercise` and `SDExercise.update(from: Exercise)`.

### 2.3 Remote Wire Format Parity
- Matches Spring Boot `/api/exercises` JSON contract (`ExerciseResponseDto`).

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/Exercise.swift` (Enhance validation and domain invariants)
- `FOSSTraining/Domain/Repositories/ExerciseRepository.swift` (Protocol contract)
- `FOSSTraining/Domain/Enums/ExerciseCategory.swift`
- `FOSSTraining/Domain/Enums/MovementPattern.swift`
- `FOSSTraining/Domain/Enums/EquipmentCategory.swift`
- `FOSSTraining/Domain/Enums/DifficultyLevel.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Models/SDExercise.swift` (SwiftData `@Model`)
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataExerciseRepository.swift` (Local-first persistence)
- `FOSSTraining/Data/SwiftData/Seeds/ExerciseCatalogSeed.swift` (Initial default catalog)
- `FOSSTraining/Data/Remote/Repositories/RemoteExerciseRepository.swift` (Spring Boot REST client)

### Features & Presentation
- `FOSSTraining/Features/Exercises/ViewModels/ExerciseListViewModel.swift` (`@Observable`, `@MainActor`)
- `FOSSTraining/Features/Exercises/Views/ExerciseListView.swift` (Search, category filter chips, list)
- `FOSSTraining/Features/Exercises/Views/ExerciseDetailView.swift` (Hero image, instructions, muscles)
- `FOSSTraining/Features/Exercises/Views/ExerciseEditorSheet.swift` (Modal creation/editing form)
- `FOSSTraining/Features/Exercises/Components/ExerciseCardView.swift` (Reusable glassmorphic card)

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/ExerciseDomainTests.swift`
- `FOSSTrainingTests/Data/SwiftDataExerciseRepositoryTests.swift`
- `FOSSTrainingTests/Data/RemoteExerciseRepositoryTests.swift`
- `FOSSTrainingTests/Features/ExerciseListViewModelTests.swift`

---

## 4. Testing Strategy & Red-Green-Refactor Flow

1. **Domain Tests (Red -> Green):** Validate that empty names throw errors, category filtering logic operates correctly, and modality properties validate.
2. **Repository Tests (Red -> Green):** Validate that `SwiftDataExerciseRepository` persists new exercises, filters by `isActive == true`, and soft-deletes properly without disk leaks.
3. **ViewModel Tests (Red -> Green):** Validate search debounce filtering, category selection state changes, error handling alert triggers, and optimistic UI updates.
4. **SwiftUI Previews:** Provide isolated in-memory preview containers for `ExerciseListView`, `ExerciseDetailView`, and `ExerciseEditorSheet`.
