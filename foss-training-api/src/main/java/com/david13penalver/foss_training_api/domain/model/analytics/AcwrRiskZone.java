package com.david13penalver.foss_training_api.domain.model.analytics;

public enum AcwrRiskZone {
    UNDERTRAINING(
            "Undertraining",
            "Acute workload is significantly below your chronic training baseline. Risk of deconditioning and elevated injury hazard upon sudden return to high volume."),
    OPTIMAL(
            "Optimal Zone",
            "Acute workload is in the optimal 'sweet spot' (0.80 - 1.30). Fitness gains are maximized while injury risk remains minimal."),
    OVERREACHING(
            "Overreaching Zone",
            "Acute workload is moderately elevated above chronic baseline (1.30 - 1.50). Fatigue is accumulating; monitor recovery and sleep closely."),
    HIGH_RISK(
            "High Injury Risk",
            "Acute workload spike exceeds safe thresholds (ratio > 1.50). Injury risk is exponentially elevated. A deload week or active recovery is strongly recommended.");

    private final String displayName;
    private final String description;

    AcwrRiskZone(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static AcwrRiskZone fromRatio(double ratio) {
        if (ratio < 0.80) {
            return UNDERTRAINING;
        } else if (ratio <= 1.30) {
            return OPTIMAL;
        } else if (ratio <= 1.50) {
            return OVERREACHING;
        } else {
            return HIGH_RISK;
        }
    }
}
