# Implementation Plan: SPEC-06 — Athlete Profile & Powerlifting Scoring

**Module:** `SPEC-06_ATHLETE_PROFILE`  
**Status:** Planned  
**Target:** iOS 26+ (Swift 6, SwiftUI, Swift Charts, SwiftData, Swift Testing)  
**Spec Document:** [SPEC-06_ATHLETE_PROFILE.md](../SPEC-06_ATHLETE_PROFILE.md)  

---

## 1. Architectural Approach & Boundaries

```mermaid
flowchart TD
    subgraph Presentation ["Features/Athlete (SwiftUI & Swift Charts)"]
        APV["AthleteProfileView"]
        RSC["RelativeStrengthCard"]
        BTC["BodyweightTrendChart"]
        LWS["LogWeightSheet"]
        VM["AthleteProfileViewModel (@Observable, @MainActor)"]
    end

    subgraph Pure Domain & Scoring ["Domain/Services (Pure Swift)"]
        RSCAL["RelativeStrengthCalculator (DOTS & Wilks)"]
        EMA["BodyweightMovingAverageCalculator"]
        AP["AthleteProfile"]
        BWE["BodyweightEntry"]
        PORT["AthleteRepository (Protocol)"]
    end

    subgraph Data Layer ["Data (Adapters)"]
        SDAR["SwiftDataAthleteRepository"]
        SDAP["@Model SDAthleteProfile"]
        SDBW["@Model SDBodyweightEntry"]
        RAR["RemoteAthleteRepository (Spring Boot /api/athlete)"]
    end

    APV --> VM
    RSC --> VM
    BTC --> VM
    LWS --> VM
    VM --> PORT
    PORT <|.. SDAR
    PORT <|.. RAR
    SDAR --> SDAP
    SDAR --> SDBW
    SDAR --> RSCAL
```

### Architectural Invariants
1. **Mathematical Scoring Parity:** DOTS and Wilks polynomial evaluations must precisely match official IPF scoring and Java `RelativeStrengthCalculator.java`.
2. **Moving Average Filtering:** Bodyweight trend charts apply 7-day Exponential Moving Average ($\alpha = 0.25$) to isolate true physiological tissue changes from daily hydration volatility.
3. **Offline Calculation Resilience:** In Local tier, relative strength scoring evaluates instantly without internet access.
4. **Hermetic Testing:** Relative strength and EMA calculators are tested against established IPF benchmark lifters hermetically.

---

## 2. Data Contracts & Value Objects

### 2.1 Domain Models
- `AthleteProfile`: `id: Int`, `displayName: String`, `gender: Gender`, `dateOfBirth: Date?`, `heightCm: Double?`, `experienceLevel: ProgramLevel`, `targetGoal: String?`, `preferredUnit: WeightUnit`.
- `BodyweightEntry`: `id: Int`, `weightKg: Double`, `measuredDate: Date`, `notes: String?`.
- `RelativeStrengthScore`: `formula: ScoringFormula`, `score: Double`, `totalKg: Double`, `bodyweightKg: Double`, `strengthToWeightRatio: Double`, `tier: RelativeStrengthTier`, `tierDescription: String`.

### 2.2 Remote REST Parity
- Spring Boot `/api/athlete` endpoints (`/profile`, `/bodyweight`, `/relative-strength`).

---

## 3. Files to Create / Modify

### Domain Layer
- `FOSSTraining/Domain/Models/AthleteProfile.swift`
- `FOSSTraining/Domain/Models/BodyweightEntry.swift`
- `FOSSTraining/Domain/Models/RelativeStrengthScore.swift`
- `FOSSTraining/Domain/Enums/Gender.swift`
- `FOSSTraining/Domain/Enums/ScoringFormula.swift`
- `FOSSTraining/Domain/Enums/RelativeStrengthTier.swift`
- `FOSSTraining/Domain/Services/RelativeStrengthCalculator.swift`
- `FOSSTraining/Domain/Services/BodyweightMovingAverageCalculator.swift`
- `FOSSTraining/Domain/Repositories/AthleteRepository.swift`

### Data Layer
- `FOSSTraining/Data/SwiftData/Models/SDAthleteProfile.swift`
- `FOSSTraining/Data/SwiftData/Models/SDBodyweightEntry.swift`
- `FOSSTraining/Data/SwiftData/Repositories/SwiftDataAthleteRepository.swift`
- `FOSSTraining/Data/Remote/Repositories/RemoteAthleteRepository.swift`

### Features & Presentation
- `FOSSTraining/Features/Athlete/ViewModels/AthleteProfileViewModel.swift`
- `FOSSTraining/Features/Athlete/ViewModels/BodyweightLogViewModel.swift`
- `FOSSTraining/Features/Athlete/Views/AthleteProfileView.swift`
- `FOSSTraining/Features/Athlete/Views/LogWeightSheet.swift`
- `FOSSTraining/Features/Athlete/Components/RelativeStrengthCard.swift`
- `FOSSTraining/Features/Athlete/Components/BodyweightTrendChart.swift`

### Test Target (`FOSSTrainingTests/`)
- `FOSSTrainingTests/Domain/DotsCalculatorTests.swift`
- `FOSSTrainingTests/Domain/WilksCalculatorTests.swift`
- `FOSSTrainingTests/Domain/BodyweightMovingAverageTests.swift`
- `FOSSTrainingTests/Data/SwiftDataAthleteRepositoryTests.swift`
- `FOSSTrainingTests/Features/AthleteProfileViewModelTests.swift`

---

## 4. Testing Strategy

1. **DOTS Polynomial Tests:** Test male and female benchmark points (e.g. 83kg male with 650kg total; 63kg female with 420kg total) against expected IPF outputs.
2. **Wilks Formula Tests:** Test standard male and female lifters against classic Wilks coefficients.
3. **EMA Smoothing Tests:** Verify constant weight produces identical EMA; test recovery response to single-day water retention spikes.
4. **SwiftData Repository Tests:** Verify CRUD operations on weigh-in history and profile records on in-memory containers.
