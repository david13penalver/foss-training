package com.david13penalver.foss_training_api.domain.model.program;

public enum PeriodizationType {
    LINEAR,
    BLOCK,
    UNDULATING,
    REVERSE_LINEAR;

    public static PeriodizationType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PeriodizationType value cannot be null or blank");
        }
        for (PeriodizationType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PeriodizationType: " + value);
    }
}
