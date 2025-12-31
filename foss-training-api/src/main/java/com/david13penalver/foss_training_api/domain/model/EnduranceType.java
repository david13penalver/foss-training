package com.david13penalver.foss_training_api.domain.model;

public enum EnduranceType {

    AEROBIC(
            "Aerobic Endurance",
            "Sustained low-to-moderate intensity exercise using oxygen for energy. Typically 60-80% max heart rate.",
            "Long-distance running, cycling, swimming"),

    ANAEROBIC(
            "Anaerobic Endurance",
            "High-intensity exercise where the body works without sufficient oxygen. Typically 80-95% max heart rate.",
            "Sprint intervals, hill repeats, track workouts"),

    HIIT(
            "High-Intensity Interval Training",
            "Short bursts of maximum effort followed by rest periods. Alternates between aerobic and anaerobic zones.",
            "Tabata, sprint intervals, circuit training"),

    LISS(
            "Low-Intensity Steady State",
            "Prolonged low-intensity cardio at consistent pace. Typically 50-65% max heart rate for 30-60+ minutes.",
            "Walking, light jogging, easy cycling"),

    TEMPO(
            "Tempo Training",
            "Sustained effort at lactate threshold (70-85% max heart rate). Challenging but sustainable pace.",
            "Tempo runs, threshold intervals"),

    FARTLEK(
            "Fartlek Training",
            "Unstructured speed play mixing fast and slow intervals. Swedish for 'speed play'.",
            "Variable pace running with spontaneous surges"),

    STEADY_STATE(
            "Steady State Cardio",
            "Moderate intensity maintained for extended duration. Base endurance building.",
            "Consistent pace running, cycling, rowing");

    private final String name;
    private final String description;
    private final String examples;

    EnduranceType(String name, String description, String examples) {
        this.name = name;
        this.description = description;
        this.examples = examples;
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

    public boolean isHighIntensity() {
        return this == HIIT || this == ANAEROBIC;
    }

    public boolean isLowIntensity() {
        return this == LISS || this == AEROBIC;
    }

    public static EnduranceType fromString(String text) {
        for (EnduranceType type : EnduranceType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No endurance type with name " + text + " found");
    }
}
