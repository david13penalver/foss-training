# Implementation Plan: SPEC-07 — Data Sovereignty & Migration Bridge

**Module:** `SPEC-07_DATA_SOVEREIGNTY`  
**Status:** Planned  
**Target:** iOS 26+ (Swift 6, SwiftUI, SwiftData, UniformTypeIdentifiers, Swift Testing)  
**Spec Document:** [SPEC-07_DATA_SOVEREIGNTY.md](../SPEC-07_DATA_SOVEREIGNTY.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph Presentation ["Features/DataPortability (SwiftUI)"]
        DPV["DataPortabilityView"]
        ICM["ImportConflictModal"]
        CMR["CloudMigrationSheet"]
        VM["DataPortabilityViewModel (@Observable, @MainActor)"]
    end

    subgraph Domain Layer ["Domain (Pure Swift)"]
        FBD["FullBackupData (Schema)"]
        IS["ImportSummary"]
        IM["ImportMode (Strategy)"]
        PORT["DataPortabilityRepository (Protocol)"]
        CMC["CloudMigrationCoordinator"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDPR["SwiftDataPortabilityRepository"]
        RPR["RemotePortabilityRepository (Spring Boot /api/data)"]
        MC["ModelContext (Local SwiftData)"]
    end

    DPV --> VM
    ICM --> VM
    CMR --> VM
    VM --> PORT
    VM --> CMC
    PORT <|.. SDPR
    PORT <|.. RPR
    SDPR --> MC
    CMC --> SDPR
    CMC --> RPR
```

### Architectural Invariants
1. **Full Schema Parity:** The JSON backup format `FullBackupData` matches Spring Boot `FullBackupData.java` field-for-field.
2. **CSV Standard Alignment:** CSV workout export matches `ExportWorkoutsCsvService.java` header columns and row escaping rules.
3. **Non-Destructive Migration:** The 1-click cloud migration bridge uploads local data to the remote server and verifies HTTP 200 before offering to purge local records.
4. **Hermetic Testing:** JSON import/export and CSV generation are tested on in-memory SwiftData containers.

---

## 2. Data Contracts & Wire Format

### 2.1 Full Backup JSON Schema
```swift
public struct FullBackupData: Codable, Sendable {
    public var exportVersion: String = "1.0"
    public var exportedAt: Date = Date()
    public var exercises: [Exercise] = []
    public var sessions: [Session] = []
    public var programs: [TrainingProgram] = []
    public var trainings: [Training] = []
    public var bodyweightEntries: [BodyweightEntry] = []
}
```

### 2.2 CSV Column Header
```csv
training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes
```

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/FullBackupData.swift`
- `FOSSTraining/Domain/Models/ImportSummary.swift`
- `FOSSTraining/Domain/Enums/ImportMode.swift`
- `FOSSTraining/Domain/Services/CloudMigrationCoordinator.swift`
- `FOSSTraining/Domain/Repositories/DataPortabilityRepository.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataPortabilityRepository.swift`
- `FOSSTraining/Data/Remote/Repositories/RemotePortabilityRepository.swift`

### Features & Presentation
- `FOSSTraining/Features/Settings/ViewModels/DataPortabilityViewModel.swift`
- `FOSSTraining/Features/Settings/Views/DataPortabilityView.swift`
- `FOSSTraining/Features/Settings/Views/ImportConflictModal.swift`
- `FOSSTraining/Features/Settings/Views/CloudMigrationSheet.swift`

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/BackupSerializationTests.swift`
- `FOSSTrainingTests/Domain/WorkoutsCsvExportTests.swift`
- `FOSSTrainingTests/Data/SwiftDataPortabilityRepositoryTests.swift`
- `FOSSTrainingTests/Domain/CloudMigrationCoordinatorTests.swift`

---

## 4. Testing Strategy

1. **Serialization Roundtrip Tests:** Encode complete `FullBackupData` into JSON and decode back, verifying zero field loss.
2. **Conflict Resolution Tests:** Import backup with `MERGE`, `OVERWRITE`, and `SKIP_EXISTING`, verifying ID handling and entity preservation.
3. **CSV Export Tests:** Verify exact header sequence, quote escaping for names with commas, and handling empty sets.
4. **Migration Coordinator Tests:** Mock network responses to ensure failure triggers safe abort without data corruption.
