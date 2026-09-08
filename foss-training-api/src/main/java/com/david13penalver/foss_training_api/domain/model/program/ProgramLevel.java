package com.david13penalver.foss_training_api.domain.model.program;

public enum ProgramLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    ELITE;

    public static ProgramLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProgramLevel value cannot be null or blank");
        }
        for (ProgramLevel level : values()) {
            if (level.name().equalsIgnoreCase(value.trim())) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown ProgramLevel: " + value);
    }
}
