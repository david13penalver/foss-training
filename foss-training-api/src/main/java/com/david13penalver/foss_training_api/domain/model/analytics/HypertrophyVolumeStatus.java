package com.david13penalver.foss_training_api.domain.model.analytics;

public enum HypertrophyVolumeStatus {
    UNDERTRAINED(
            "Undertrained",
            "< 6 weekly sets. Volume is below maintenance volume (MV); insufficient to maintain maximum muscular development."),
    MAINTENANCE(
            "Maintenance",
            "6–9 weekly sets. Minimum Effective Volume (MEV); sufficient to maintain muscle mass with modest adaptation."),
    OPTIMAL(
            "Optimal Hypertrophy",
            "10–20 weekly sets. Maximum Adaptive Volume (MAV); the evidence-based sweet spot for muscle growth."),
    OVERTRAINED(
            "Excessive Volume",
            "> 20 weekly sets. Approaches or exceeds Maximum Recoverable Volume (MRV); risk of junk volume and delayed recovery.");

    private final String displayName;
    private final String description;

    HypertrophyVolumeStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static HypertrophyVolumeStatus fromSets(double effectiveSets) {
        if (effectiveSets < 6.0) {
            return UNDERTRAINED;
        } else if (effectiveSets < 10.0) {
            return MAINTENANCE;
        } else if (effectiveSets <= 20.0) {
            return OPTIMAL;
        } else {
            return OVERTRAINED;
        }
    }
}
