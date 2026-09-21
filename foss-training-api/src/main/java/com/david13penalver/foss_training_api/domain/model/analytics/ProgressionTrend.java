package com.david13penalver.foss_training_api.domain.model.analytics;

public enum ProgressionTrend {
    IMPROVING("Improving", "Strength performance has increased over time"),
    STAGNANT("Stagnant", "Strength performance has plateaued within a +/- 2% margin"),
    DECLINING("Declining", "Strength performance has decreased compared to baseline"),
    INSUFFICIENT_DATA("Insufficient Data", "At least two completed sessions are required to determine a progression trend");

    private final String displayName;
    private final String description;

    ProgressionTrend(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static ProgressionTrend evaluate(double relativeGainPercentage, int sessionCount) {
        if (sessionCount < 2) {
            return INSUFFICIENT_DATA;
        }
        if (relativeGainPercentage >= 2.0) {
            return IMPROVING;
        } else if (relativeGainPercentage <= -2.0) {
            return DECLINING;
        } else {
            return STAGNANT;
        }
    }

    public static ProgressionTrend fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (ProgressionTrend trend : values()) {
            if (trend.name().equalsIgnoreCase(text.trim()) || trend.displayName.equalsIgnoreCase(text.trim())) {
                return trend;
            }
        }
        throw new IllegalArgumentException("No progression trend with name " + text + " found");
    }
}
