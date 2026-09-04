package com.david13penalver.foss_training_api.domain.model.common;

public enum HeartRateZone {
    ZONE_1("Active Recovery", 0.50, 0.60, "Promotes recovery, improves fat metabolism"),
    ZONE_2("Aerobic Base", 0.60, 0.70, "Builds endurance base, optimizes mitochondrial function"),
    ZONE_3("Tempo / Aerobic Endurance", 0.70, 0.80, "Improves aerobic capacity, raises lactate threshold"),
    ZONE_4("Lactate Threshold", 0.80, 0.90, "Maximizes lactate tolerance and high-intensity stamina"),
    ZONE_5("Neuromuscular / Anaerobic", 0.90, 1.00, "Peak power output and VO2max intervals");

    private final String displayName;
    private final double minPercentage;
    private final double maxPercentage;
    private final String description;

    HeartRateZone(String displayName, double minPercentage, double maxPercentage, String description) {
        this.displayName = displayName;
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMinPercentage() {
        return minPercentage;
    }

    public double getMaxPercentage() {
        return maxPercentage;
    }

    public String getDescription() {
        return description;
    }

    public static HeartRateZone fromHeartRate(int currentHr, int maxHr) {
        if (maxHr <= 0) {
            throw new IllegalArgumentException("Max HR must be positive");
        }
        double percentage = (double) currentHr / maxHr;
        if (percentage < 0.60) {
            return ZONE_1;
        } else if (percentage < 0.70) {
            return ZONE_2;
        } else if (percentage < 0.80) {
            return ZONE_3;
        } else if (percentage < 0.90) {
            return ZONE_4;
        } else {
            return ZONE_5;
        }
    }
}
