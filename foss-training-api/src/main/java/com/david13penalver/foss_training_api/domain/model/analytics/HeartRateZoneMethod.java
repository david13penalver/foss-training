package com.david13penalver.foss_training_api.domain.model.analytics;

public enum HeartRateZoneMethod {
    KARVONEN("Karvonen Heart Rate Reserve", "Calculates target training zones using Heart Rate Reserve (HRR = HRmax - HRrest)"),
    PERCENT_MAX_HR("Percentage of Max Heart Rate", "Calculates target training zones as direct percentages of maximum heart rate");

    private final String displayName;
    private final String description;

    HeartRateZoneMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static HeartRateZoneMethod fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (HeartRateZoneMethod method : values()) {
            if (method.name().equalsIgnoreCase(text.trim()) || method.displayName.equalsIgnoreCase(text.trim())) {
                return method;
            }
        }
        throw new IllegalArgumentException("No heart rate zone method with name " + text + " found");
    }
}
