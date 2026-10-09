# Implementation Plan: SPEC-05 — Sports Science & Analytics

**Module:** `SPEC-05_SPORTS_SCIENCE_ANALYTICS`  
**Status:** Complete  
**Target:** iOS 26+ (Swift 6, SwiftUI, Swift Charts, SwiftData, Swift Testing)  
**Spec Document:** [SPEC-05_SPORTS_SCIENCE_ANALYTICS.md](../SPEC-05_SPORTS_SCIENCE_ANALYTICS.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph Presentation ["Features/Analytics (SwiftUI & Swift Charts)"]
        ADV["AnalyticsDashboardView"]
        ORV["OneRepMaxCalculatorView"]
        ACG["AcwrGaugeCard"]
        HVC["HypertrophyVolumeChart"]
        EPC["ExerciseProgressionChart"]
        VM["AnalyticsDashboardViewModel (@Observable, @MainActor)"]
    end

    subgraph Pure Sports Science Domain ["Domain/Services (Pure Swift)"]
        ORM["OneRepMaxCalculator"]
        ACW["AcwrCalculator"]
        MVC["MuscleVolumeCalculator"]
        PRT["PersonalRecordTracker"]
        PORT["AnalyticsRepository (Protocol)"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDAR["SwiftDataAnalyticsRepository (Local Calculator Adapter)"]
        RAR["RemoteAnalyticsRepository (Spring Boot /api/analytics)"]
    end

    ADV --> VM
    ORV --> VM
    ACG --> VM
    HVC --> VM
    EPC --> VM
    VM --> PORT
    PORT <|.. SDAR
    PORT <|.. RAR
    SDAR --> ORM
    SDAR --> ACW
    SDAR --> MVC
    SDAR --> PRT
```

### Architectural Invariants
1. **Mathematical Parity:** Calculations for 1RM, ACWR, and Hypertrophy volume landmarks must match the outputs of Java `foss-training-api` within $\pm 0.01$.
2. **Offline-First Resilience:** In Local mode, all metrics are computed entirely on-device from SwiftData history with zero network dependency.
3. **Swift Charts Performance:** Chart mark pipelines must operate without dropping frames during touch scrubbing, utilizing downsampling for multi-year workout histories.
4. **Hermetic Testing:** Sports science mathematical engines are tested as pure functions with zero framework dependencies.

---

## 2. Data Contracts & Value Objects

### 2.1 Domain Models
- `OneRepMaxEstimate`: `formula: OneRepMaxFormula`, `estimatedOneRepMax: Double`, `percentages: [TrainingPercentage]`.
- `WorkloadRatio`: `acuteWorkload: Double`, `chronicWorkload: Double`, `acwrRatio: Double`, `riskZone: AcwrRiskZone`, `recommendation: String`, `dailyWorkloads: [DailyWorkload]`.
- `WeeklyMuscleVolume`: `weekStartDate: Date`, `totalSets: Int`, `muscleVolumes: [MuscleGroupVolume]`.
- `ExerciseProgression`: `exerciseId: Int`, `exerciseName: String`, `dataPoints: [ProgressionDataPoint]`, `trend: ProgressionTrend`, `percentageChange: Double`.
- `PersonalRecord`: `exerciseId: Int`, `exerciseName: String`, `recordType: PRRecordType`, `value: Double`, `unit: String`, `achievedDate: Date`, `trainingId: Int`.

### 2.2 Remote REST Parity
- Spring Boot `/api/analytics` endpoints (`/1rm`, `/acwr`, `/weekly-volume`, `/progression/{id}`, `/personal-records`).

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/OneRepMaxFormula.swift` (Already present; verify formulas)
- `FOSSTraining/Domain/Models/OneRepMaxEstimate.swift`
- `FOSSTraining/Domain/Models/WorkloadRatio.swift`
- `FOSSTraining/Domain/Models/WeeklyMuscleVolume.swift`
- `FOSSTraining/Domain/Models/ExerciseProgression.swift`
- `FOSSTraining/Domain/Models/PersonalRecord.swift`
- `FOSSTraining/Domain/Enums/AcwrRiskZone.swift`
- `FOSSTraining/Domain/Enums/HypertrophyVolumeStatus.swift`
- `FOSSTraining/Domain/Enums/ProgressionTrend.swift`
- `FOSSTraining/Domain/Services/OneRepMaxCalculator.swift`
- `FOSSTraining/Domain/Services/AcwrCalculator.swift`
- `FOSSTraining/Domain/Services/MuscleVolumeCalculator.swift`
- `FOSSTraining/Domain/Services/PersonalRecordTracker.swift`
- `FOSSTraining/Domain/Repositories/AnalyticsRepository.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataAnalyticsRepository.swift`
- `FOSSTraining/Data/Remote/Repositories/RemoteAnalyticsRepository.swift`

### Features & Presentation
- `FOSSTraining/Features/Analytics/ViewModels/AnalyticsDashboardViewModel.swift`
- `FOSSTraining/Features/Analytics/ViewModels/OneRepMaxCalculatorViewModel.swift`
- `FOSSTraining/Features/Analytics/Views/AnalyticsDashboardView.swift`
- `FOSSTraining/Features/Analytics/Views/OneRepMaxCalculatorView.swift`
- `FOSSTraining/Features/Analytics/Components/AcwrGaugeCard.swift`
- `FOSSTraining/Features/Analytics/Components/HypertrophyVolumeChart.swift`
- `FOSSTraining/Features/Analytics/Components/ExerciseProgressionChart.swift`
- `FOSSTraining/Features/Analytics/Components/PersonalRecordBadgeView.swift`

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/OneRepMaxParityTests.swift`
- `FOSSTrainingTests/Domain/AcwrCalculatorTests.swift`
- `FOSSTrainingTests/Domain/MuscleVolumeCalculatorTests.swift`
- `FOSSTrainingTests/Domain/PersonalRecordTrackerTests.swift`
- `FOSSTrainingTests/Features/AnalyticsDashboardViewModelTests.swift`

---

## 4. Testing Strategy

1. **1RM Parity Tests:** Compare outputs against canonical values from `sports-science-parity` skill across 1, 3, 5, 8, 10, 12 repetitions.
2. **ACWR Tests:** Verify Session-RPE math ($\text{Duration} \times \text{RPE}$), rolling 7-day and 28-day sum windows, zero division protection, and zone boundaries ($<0.8$, $0.8-1.3$, $1.3-1.5$, $\ge 1.5$).
3. **Volume Tests:** Verify classification of sets into Israel MEV/MAV/MRV landmarks.
4. **Swift Charts Previews:** Provide isolated mock data sources for charts and gauges.
