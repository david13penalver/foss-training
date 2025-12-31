package com.david13penalver.foss_training_api.domain.model;

public enum DifficultyLevel {

    BEGINNER(
            "Beginner",
            "Suitable for those new to exercise or returning after a break. Focus on learning proper form and technique.",
            1),

    INTERMEDIATE(
            "Intermediate",
            "For those with 6+ months of consistent training. Requires good form and moderate strength/endurance.",
            2),

    ADVANCED(
            "Advanced",
            "For experienced athletes with strong technique and conditioning. High intensity and complexity.",
            3),

    EXPERT(
            "Expert",
            "Elite level requiring exceptional strength, skill, and experience. Not recommended without proper progression.",
            4);

    private final String name;
    private final String description;
    private final int level;

    DifficultyLevel(String name, String description, int level) {
        this.name = name;
        this.description = description;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public boolean isEasierThan(DifficultyLevel other) {
        return this.level < other.level;
    }

    public boolean isHarderThan(DifficultyLevel other) {
        return this.level > other.level;
    }

    public static DifficultyLevel fromLevel(int level) {
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            if (difficulty.level == level) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("No difficulty level with level " + level + " found");
    }

    public static DifficultyLevel fromString(String text) {
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            if (difficulty.name().equalsIgnoreCase(text)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("No difficulty level with name " + text + " found");
    }
}
