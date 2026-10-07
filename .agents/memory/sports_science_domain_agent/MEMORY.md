# Sports Science Domain Agent Memory — FOSS Training

Index of 1RM calculation formulas, ACWR thresholds, relative strength mathematics, and cross-platform verification test cases.

## Initial Knowledge
- **1RM Formulas**: Epley, Brzycki, Lombardi, Mayhew et al., O'Conner, Wathen.
- **1RM Invariant**: When reps = 1, return weight directly. All final outputs rounded to 2 decimal places.
- **DOTS Formula**: 4th-degree polynomial in denominator for Male and Female lifters, normalized with $500 / \text{denom}$.
- **Wilks Formula**: 5th-degree polynomial in denominator for Male and Female lifters.
- **ACWR Ratios**: Acute (7-day load) vs Chronic (28-day load / 4). Sweet spot: 0.8–1.3. Danger zone: $\ge 1.5$.
- **Verification Test Cases**:
  - Epley (100 kg, 5 reps) $\implies$ 116.67 kg
  - Brzycki (100 kg, 5 reps) $\implies$ 112.50 kg
  - O'Conner (100 kg, 5 reps) $\implies$ 112.50 kg
  - Male Lifter (75 kg BW, 400 kg Total) $\implies$ DOTS ~ 308.28

---
<!-- Add entries with format:
- [name](file.md) — brief description
-->
