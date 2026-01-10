package com.david13penalver.foss_training_api.domain.model.session;

public enum SessionPartEnum {

    ACTIVATION(
            "Activation Phase",
            "Prepares neuromuscular system with CNS priming, mobility work, and movement-specific preparation to optimize performance and reduce injury risk",
            "Foam rolling, joint mobility drills, muscle activation exercises, dynamic stretches",
            5,
            10),

    WARMUP(
            "Warm-up Phase",
            "Gradually increases heart rate, muscle temperature, and blood flow while preparing cardiovascular and musculoskeletal systems for exercise",
            "Light cardio (jogging, cycling), dynamic stretching, sport-specific movements at low intensity",
            5,
            15),

    SKILL_WORK(
            "Skill/Technical Work",
            "Develops movement patterns, technique refinement, and motor learning when CNS is fresh before fatigue accumulates",
            "Olympic lift practice, sport-specific drills, movement quality focus, balance exercises",
            10,
            20),

    MAIN_WORK(
            "Main Work Phase",
            "Primary training stimulus targeting specific adaptations through progressive overload of strength, power, hypertrophy, or endurance goals",
            "Heavy compound lifts, high-intensity intervals, sport-specific conditioning, volume training",
            20,
            60),

    ACCESSORY_WORK(
            "Accessory/Auxiliary Work",
            "Supplemental exercises addressing weak points, muscle imbalances, or adding training volume for secondary muscle groups",
            "Isolation exercises, corrective movements, additional volume for lagging muscles",
            10,
            25),

    METABOLIC_CONDITIONING(
            "Metabolic Conditioning",
            "High-intensity work capacity training that challenges energy systems and improves cardiovascular endurance and lactate tolerance",
            "HIIT circuits, AMRAP, EMOM, Tabata protocols, conditioning finishers",
            5,
            20),

    COOLDOWN(
            "Cool-down Phase",
            "Gradually lowers heart rate and body temperature while initiating recovery processes and reducing post-exercise muscle soreness",
            "Low-intensity cardio (walking, easy cycling), gradual activity reduction",
            5,
            10),

    RECOVERY(
            "Recovery/Adaptation Phase",
            "Promotes parasympathetic nervous system activation, restores range of motion, and accelerates recovery through static stretching and breathing work",
            "Static stretching (20-30s holds), foam rolling, breathwork, meditation, nervous system regulation",
            5,
            15);

    private final String name;
    private final String description;
    private final String examples;
    private final int recommendedMinDuration; // minutes
    private final int recommendedMaxDuration; // minutes

    SessionPartEnum(String name, String description, String examples,
            int recommendedMinDuration, int recommendedMaxDuration) {
        this.name = name;
        this.description = description;
        this.examples = examples;
        this.recommendedMinDuration = recommendedMinDuration;
        this.recommendedMaxDuration = recommendedMaxDuration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getExamples() {
        return examples;
    }

    public int getRecommendedMinDuration() {
        return recommendedMinDuration;
    }

    public int getRecommendedMaxDuration() {
        return recommendedMaxDuration;
    }

    // Helper methods
    public boolean isPreWorkout() {
        return this == ACTIVATION || this == WARMUP || this == SKILL_WORK;
    }

    public boolean isMainTraining() {
        return this == MAIN_WORK || this == ACCESSORY_WORK || this == METABOLIC_CONDITIONING;
    }

    public boolean isPostWorkout() {
        return this == COOLDOWN || this == RECOVERY;
    }

    public boolean requiresHighCNSFreshness() {
        return this == SKILL_WORK || this == MAIN_WORK;
    }

    public static SessionPartEnum fromString(String text) {
        for (SessionPartEnum part : SessionPartEnum.values()) {
            if (part.name().equalsIgnoreCase(text)) {
                return part;
            }
        }
        throw new IllegalArgumentException("No session part with name " + text + " found");
    }
}
