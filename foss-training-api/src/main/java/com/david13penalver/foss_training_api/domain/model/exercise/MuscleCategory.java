package com.david13penalver.foss_training_api.domain.model;

public enum MuscleCategory {
    UPPER_BODY("Upper Body"),
    CORE("Core"),
    LOWER_BODY("Lower Body"),
    FULL_BODY("Full Body");

    private final String name;

    MuscleCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}