---
name: sports-science-parity
description: Mathematical formulas, definitions, and cross-platform parity guidelines for 1RM estimations, relative strength (DOTS/Wilks), ACWR workload ratios, and data portability across Java, Angular, and Swift.
---

# Sports Science Domain & Parity Guidelines

This skill defines the canonical mathematical models and algorithmic invariants implemented across the backend (`foss-training-api`), web frontend (`foss-training-web`), and iOS application (`foss-training-ios`).

---

## 1. One Repetition Maximum (1RM) Formulas

All implementations must round final results to 2 decimal places (`round(raw * 100.0) / 100.0`). When `reps == 1`, return `weightKg` directly.

| Formula | Mathematical Definition | Boundary Notes |
|---|---|---|
| **Epley** | $w \times (1 + \frac{r}{30})$ | Standard default formula for resistance training |
| **Brzycki** | $w \times \frac{36}{37 - r}$ | $r < 37$; fall back or throw exception when $r \ge 37$ |
| **Lombardi** | $w \times r^{0.10}$ | Conservative curve for high repetitions |
| **Mayhew et al.** | $\frac{100 \times w}{52.2 + 41.9 \times e^{-0.055 \times r}}$ | Bench press empirical standard |
| **O'Conner** | $w \times (1 + 0.025 \times r)$ | Linear progression formula |
| **Wathen** | $\frac{100 \times w}{48.8 + 53.8 \times e^{-0.075 \times r}}$ | Robust empirical non-linear model |

### Code Locations for Parity Verification:
- **Backend (Java):** `foss-training-api/.../domain/model/analytics/OneRepMaxFormula.java`
- **Web (TypeScript):** `foss-training-web/src/app/features/analytics/.../one-rep-max-calculator.component.ts`
- **iOS (Swift):** `foss-training-ios/FOSSTraining/Domain/Models/OneRepMaxFormula.swift`

---

## 2. Powerlifting Relative Strength: DOTS & Wilks

Normalizes strength across bodyweight categories. Output values: `dotsScore`, `wilksScore`, `relativeStrengthRatio` ($\frac{\text{totalKg}}{\text{bodyweightKg}}$).

### DOTS Formula:
$$\text{Score} = \text{totalKg} \times \frac{500}{\text{denom}(x)}$$
where $x = \text{bodyweightKg}$ and $\text{denom}(x)$ is a 4th-degree polynomial:
- **Male**: $-1.0930 \times 10^{-6} x^4 + 7.391293 \times 10^{-4} x^3 - 0.1918759221 x^2 + 24.0900786 x - 307.754178$
- **Female**: $-1.0706 \times 10^{-6} x^4 + 5.158568 \times 10^{-4} x^3 - 0.1126655495 x^2 + 13.6175032 x - 57.96288$

### Classification Thresholds (DOTS):
- `< 250`: Novice
- `250 ..< 325`: Intermediate
- `325 ..< 400`: Advanced
- `400 ..< 475`: Elite
- `\ge 475`: International Elite

### Code Locations for Parity Verification:
- **Backend (Java):** `foss-training-api/.../application/usecases/athlete/impl/CalculateRelativeStrengthService.java`
- **Web (TypeScript):** `foss-training-web/src/app/features/profile/.../relative-strength-calculator.component.ts`
- **iOS (Swift):** `foss-training-ios/FOSSTraining/Domain/Models/RelativeStrengthCalculator.swift`

---

## 3. Workload Management: Acute to Chronic Workload Ratio (ACWR)

Calculates injury risk and training fatigue:
- **Acute Workload:** Total training volume over the last 7 days ($\text{Load}_{\text{7d}}$).
- **Chronic Workload:** Average weekly training volume over the last 28 days ($\frac{\text{Load}_{\text{28d}}}{4}$).
- **Ratio:** $\text{ACWR} = \frac{\text{Acute Workload}}{\text{Chronic Workload}}$

### Classification Zones:
- `< 0.80`: **Under-training** (Risk of deconditioning)
- `0.80 ..< 1.30`: **Sweet Spot** (Optimal load progression, minimal injury risk)
- `1.30 ..< 1.50`: **Elevated Risk** (Monitor fatigue closely)
- `\ge 1.50`: **Danger Zone** (High risk of overreaching and injury)

---

## 4. Data Portability & Backup Schema Parity

The application guarantees full user data sovereignty:
- **JSON Format**: Defines arrays for `exercises`, `sessions`, `trainings`, and optional `athleteProfile`.
- The iOS client can export its local SwiftData database to JSON and import backups from Spring Boot:
  - Backend Endpoint: `POST /api/data/import/backup` and `GET /api/data/export/backup`
  - iOS Repository: `SwiftDataPortabilityRepository` and `RemotePortabilityRepository`
  - Angular Web Service: `DataPortabilityService`
- Whenever schemas evolve, verify backward and forward JSON compatibility across all 3 platforms.
