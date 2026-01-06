package com.david13penalver.foss_training_api.domain.model;

public enum RecommendedTiming {

    PRE_WORKOUT(
            "Pre-Workout",
            "Before training session to prepare muscles and joints",
            "Dynamic stretches, mobility drills"),

    POST_WORKOUT(
            "Post-Workout",
            "After training session to aid recovery and improve flexibility",
            "Static stretches, foam rolling"),

    STANDALONE(
            "Standalone Session",
            "Dedicated mobility or flexibility session separate from main workout",
            "Yoga, extended stretching routine"),

    DAILY(
            "Daily Practice",
            "Can be performed any time of day as part of daily routine",
            "Light mobility drills, gentle stretching"),

    ACTIVE_RECOVERY(
            "Active Recovery",
            "On rest days to promote circulation and reduce soreness",
            "Gentle yoga, foam rolling, light mobility work");

    private final String displayName;
    private final String description;
    private final String suitableActivities;

    RecommendedTiming(String displayName, String description, String suitableActivities) {
        this.displayName = displayName;
        this.description = description;
        this.suitableActivities = suitableActivities;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getSuitableActivities() {
        return suitableActivities;
    }

    public static RecommendedTiming fromString(String text) {
        for (RecommendedTiming timing : RecommendedTiming.values()) {
            if (timing.name().equalsIgnoreCase(text)) {
                return timing;
            }
        }
        throw new IllegalArgumentException("No recommended timing with name " + text + " found");
    }
}
