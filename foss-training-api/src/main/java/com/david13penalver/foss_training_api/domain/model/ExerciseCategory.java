package com.david13penalver.foss_training_api.domain.model;

public enum ExerciseCategory {

    // Primary categories
    RESISTANCE("Resistance Training", "Exercises that build strength and muscle mass through resistance"),
    ENDURANCE("Endurance Training", "Cardiovascular exercises that improve stamina and aerobic capacity"),
    MOBILITY("Mobility & Flexibility", "Exercises that improve range of motion and joint flexibility"),

    // Additional specialized categories
    BALANCE("Balance Training", "Exercises that improve stability and coordination"),
    PLYOMETRICS("Plyometric Training", "Explosive power exercises involving jumping and rapid movements"),
    CORE("Core Stability", "Exercises targeting abdominal and lower back muscles"),
    CALISTHENICS("Calisthenics", "Bodyweight exercises using multiple muscle groups");

    private final String name;
    private final String description;

    ExerciseCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static ExerciseCategory fromString(String text) {
        for (ExerciseCategory category : ExerciseCategory.values()) {
            if (category.name().equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
