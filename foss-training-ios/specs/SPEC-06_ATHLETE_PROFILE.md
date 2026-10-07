# SPEC-06: Athlete Profile & Powerlifting Scoring — Detailed Specification

**Module:** Athlete Profile & Powerlifting Scoring Engine  
**Status:** Ready for Review (Specification Only — No Implementation Yet)  
**Target:** iOS 26+ (Swift 6, SwiftUI, Swift Charts, SwiftData, Swift Testing)  
**Backend Parity:** `AthleteRestController.java`, `RelativeStrengthCalculator.java`, `BodyweightEntry.java` (`foss-training-api`)  

---

## 1. Executive Summary & User Stories

The **Athlete Profile & Powerlifting Scoring** module provides personal biometrics management and competitive strength normalization. It enables athletes to track daily morning bodyweight trends with noise-filtered moving averages, log powerlifting personal records (Squat, Bench Press, Deadlift), and calculate bodyweight-adjusted strength scores using mathematically canonical **DOTS** and **Wilks** powerlifting coefficient formulas.

### User Stories
- **US-06.1 (Athlete Profile Setup):** As an athlete, I want to configure my biometric profile (Gender, Date of Birth, Height in cm, Experience Level, Primary Goal) so that the application can accurately calibrate strength metrics and caloric/volume suggestions.
- **US-06.2 (Morning Bodyweight Logging):** As an athlete, I want a fast, friction-free numeric logger for my daily morning weight (kg) with optional notes (e.g., "Post-refeed", "Dehydrated").
- **US-06.3 (7-Day Exponential Moving Average):** As an athlete, I want to see a 7-day smoothed moving average overlaying my raw daily weights in a chart so that natural water retention and digestion fluctuations do not obscure true fat-loss or muscle-gain trends.
- **US-06.4 (Powerlifting Big 3 Total):** As a strength athlete, I want to see my combined "Big 3" total (Squat + Bench Press + Deadlift) dynamically summed from my verified PRs.
- **US-06.5 (DOTS Score Calculator):** As an athlete, I want to calculate my official DOTS score based on my bodyweight and lifted total, receiving standard competitive tier classifications (*Novice*, *Intermediate*, *Advanced*, *Elite*, *International Elite*).
- **US-06.6 (Wilks Scoring Parity):** As an athlete, I want to switch between DOTS and classic Wilks scoring for historical comparability with past powerlifting competitions.
- **US-06.7 (Strength-to-Bodyweight Multipliers):** As an athlete, I want to view my relative strength ratios (e.g., $2.5\times$ BW Squat, $1.8\times$ BW Bench, $3.0\times$ BW Deadlift) on a visual radar or milestone card.

---

## 2. Mathematical Formulations & Parity

### 2.1 DOTS (Dynamic Objective Team Scoring) Formula
Normalizes powerlifting performance across gender and bodyweight using 4th-degree polynomials:

$$\text{DOTS Score} = \text{totalKg} \times \frac{500}{\text{denom}(x)}$$

where $x = \text{bodyweightKg}$ (clamped to realistic human limits $40 \le x \le 210$):

#### Male Denominator Polynomial:
$$\text{denom}_{\text{male}}(x) = -1.0930 \times 10^{-6} x^4 + 7.391293 \times 10^{-4} x^3 - 0.1918759221 x^2 + 24.0900786 x - 307.754178$$

#### Female Denominator Polynomial:
$$\text{denom}_{\text{female}}(x) = -1.0706 \times 10^{-6} x^4 + 5.158568 \times 10^{-4} x^3 - 0.1126655495 x^2 + 13.6175032 x - 57.96288$$

#### DOTS Classification Tiers:
| DOTS Range | Classification Tier | Description |
|---|---|---|
| $< 250$ | **Novice** | Developing foundational mechanics and base strength |
| $250 \dots 324.99$ | **Intermediate** | Consistent lifter with multi-year structured training |
| $325 \dots 399.99$ | **Advanced** | Regional/state competitive level lifter |
| $400 \dots 474.99$ | **Elite** | National championship contender |
| $\ge 475$ | **International Elite** | World-class international caliber |

### 2.2 Wilks Coefficient Formula
$$\text{Wilks Score} = \text{totalKg} \times \frac{500}{a + bx + cx^2 + dx^3 + ex^4 + fx^5}$$

#### Male Coefficients:
- $a = -216.0475144$
- $b = 16.2606339$
- $c = -0.002388645$
- $d = -0.00113732$
- $e = 7.01863 \times 10^{-6}$
- $f = -1.291 \times 10^{-8}$

#### Female Coefficients:
- $a = 594.31747775582$
- $b = -27.23842536447$
- $c = 0.82112226871$
- $d = -0.00930733913$
- $e = 4.731582 \times 10^{-5}$
- $f = -9.054 \times 10^{-8}$

### 2.3 7-Day Exponential Moving Average (EMA) for Bodyweight
$$\text{EMA}_t = \alpha \times \text{Weight}_t + (1 - \alpha) \times \text{EMA}_{t-1}$$
$$\alpha = \frac{2}{N + 1} = \frac{2}{7 + 1} = 0.25$$

---

## 3. Hexagonal Outbound Port Contract

```swift
public protocol AthleteRepository: Sendable {
    /// Retrieves current athlete profile
    func getProfile() async throws -> AthleteProfile?

    /// Creates or updates athlete profile
    func saveProfile(_ profile: AthleteProfile) async throws -> AthleteProfile

    /// Retrieves chronological bodyweight logs (most recent first)
    func getBodyweightEntries(limit: Int?) async throws -> [BodyweightEntry]

    /// Logs a morning bodyweight entry
    func logBodyweight(weightKg: Double, date: Date, notes: String?) async throws -> BodyweightEntry

    /// Deletes a bodyweight entry by ID
    func deleteBodyweight(id: Int) async throws

    /// Computes powerlifting relative strength metrics
    func calculateRelativeStrength(
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender,
        formula: ScoringFormula
    ) async throws -> RelativeStrengthScore
}
```

---

## 4. Domain Models & Value Objects

```swift
public enum Gender: String, Codable, CaseIterable, Sendable {
    case male = "MALE"
    case female = "FEMALE"
    case other = "OTHER"
}

public enum ScoringFormula: String, Codable, CaseIterable, Sendable {
    case dots = "DOTS"
    case wilks = "WILKS"

    public var displayName: String {
        switch self {
        case .dots: return "DOTS"
        case .wilks: return "Wilks"
        }
    }
}

public struct AthleteProfile: Identifiable, Codable, Hashable, Sendable {
    public var id: Int
    public var displayName: String
    public var gender: Gender
    public var dateOfBirth: Date?
    public var heightCm: Double?
    public var experienceLevel: ProgramLevel
    public var targetGoal: String?
    public var preferredUnit: WeightUnit
}

public enum WeightUnit: String, Codable, Sendable {
    case kg = "KG"
    case lbs = "LBS"
}

public struct BodyweightEntry: Identifiable, Codable, Hashable, Sendable {
    public var id: Int
    public var weightKg: Double
    public var date: Date
    public var notes: String?
}

public enum RelativeStrengthTier: String, Codable, Sendable {
    case novice = "NOVICE"
    case intermediate = "INTERMEDIATE"
    case advanced = "ADVANCED"
    case elite = "ELITE"
    case internationalElite = "INTERNATIONAL_ELITE"
}

public struct RelativeStrengthScore: Codable, Hashable, Sendable {
    public let formula: ScoringFormula
    public let score: Double
    public let totalKg: Double
    public let bodyweightKg: Double
    public let strengthToWeightRatio: Double
    public let tier: RelativeStrengthTier
    public let tierDescription: String
}
```

---

## 5. Dual-Tier Persistence Architecture

### 5.1 Local SwiftData Offline Implementation
- **Models:**
  - `@Model final class SDAthleteProfile`: Persists user profile, gender, height, and goal.
  - `@Model final class SDBodyweightEntry`: Persists individual timestamped weigh-ins.
- **Local Calculator (`RelativeStrengthCalculator`):**
  - Pure Swift struct implementing the polynomials above hermetically on device.
  - Generates `RelativeStrengthScore` instantly without network latency.

### 5.2 Premium Cloud REST Implementation
- Remote endpoints matching `AthleteRestController.java`:
  - `GET /api/athlete/profile`: Fetches athlete profile.
  - `PUT /api/athlete/profile`: Updates profile.
  - `GET /api/athlete/bodyweight`: Fetches historical weigh-ins.
  - `POST /api/athlete/bodyweight`: Logs new weight entry.
  - `DELETE /api/athlete/bodyweight/{id}`: Deletes entry.
  - `GET /api/athlete/relative-strength?totalKg=X&bodyweightKg=Y&gender=MALE&formula=DOTS`: Returns `RelativeStrengthScoreDto`.

---

## 6. UI / UX Design Specifications

### 6.1 Athlete Profile Screen (`AthleteProfileView`)
- **Avatar & Biometric Badge:** User initials or avatar, gender chip, height, and current 7-day EMA weight.
- **Powerlifting Big 3 Card:**
  - 3-column glassmorphic panel:
    - **Squat:** Best PR kg
    - **Bench:** Best PR kg
    - **Deadlift:** Best PR kg
  - Highlighted combined **Total (kg)** and **Relative Ratio ($X.X\times$ BW)**.

### 6.2 Relative Strength Gauge (`RelativeStrengthCard`)
- **Score Meter:** Prominent numerical score (e.g., `412.5 DOTS`).
- **Tier Badge:** Glassmorphic pill badge with accent gradient (e.g., `Elite`).
- **Formula Toggle:** Fast segmented toggle between `DOTS` and `Wilks`.

### 6.3 Bodyweight Trend Chart (`BodyweightTrendChart`)
- Native Swift Chart:
  - Scatter points (`PointMark`) representing individual morning weigh-ins.
  - Smooth curved line (`LineMark`) showing the 7-day Exponential Moving Average.
  - Range rule marks indicating weight boundaries.
  - Interactive touch drag scrubbing revealing exact day, weight, and note.

### 6.4 Quick Weight Log Modal (`LogWeightSheet`)
- Direct numeric keyboard with decimal point.
- Single-tap quick presets: `Same as yesterday`, `+0.2 kg`, `-0.2 kg`.
- Optional text field for morning notes.

---

## 7. TDD Test Plan

### 7.1 DOTS Mathematical Parity Tests (`DotsCalculatorTests.swift`)
- [ ] `testDotsMale_83kgBodyweight_650kgTotal_matchesCanonicalScore`
- [ ] `testDotsFemale_63kgBodyweight_420kgTotal_matchesCanonicalScore`
- [ ] `testDotsTierClassification_under250_returnsNovice`
- [ ] `testDotsTierClassification_over475_returnsInternationalElite`
- [ ] `testDotsPolynomial_handlesClampedBoundariesGracefully`

### 7.2 Wilks Mathematical Parity Tests (`WilksCalculatorTests.swift`)
- [ ] `testWilksMale_80kgBodyweight_500kgTotal_matchesCanonicalScore`
- [ ] `testWilksFemale_60kgBodyweight_350kgTotal_matchesCanonicalScore`

### 7.3 Bodyweight EMA Tests (`BodyweightMovingAverageTests.swift`)
- [ ] `testMovingAverage_withConstantWeight_returnsSameWeight`
- [ ] `testMovingAverage_withOutlierSpike_smoothsCurveAppropriately`

---

## 8. Implementation Checklist

- [ ] **Phase 1 (Domain & Calculators):** Implement `Gender`, `ScoringFormula`, `RelativeStrengthScore`, and `RelativeStrengthCalculator`.
- [ ] **Phase 2 (Ports):** Define `AthleteRepository` protocol.
- [ ] **Phase 3 (SwiftData Storage):** Implement `SDAthleteProfile`, `SDBodyweightEntry`, and `SwiftDataAthleteRepository`.
- [ ] **Phase 4 (Remote REST Adapter):** Implement `RemoteAthleteRepository` with URLSession endpoints.
- [ ] **Phase 5 (ViewModels):** Implement `AthleteProfileViewModel`, `BodyweightLogViewModel`, `RelativeStrengthViewModel`.
- [ ] **Phase 6 (SwiftUI Views):** Implement `AthleteProfileView`, `RelativeStrengthCard`, `BodyweightTrendChart`, `LogWeightSheet`.
