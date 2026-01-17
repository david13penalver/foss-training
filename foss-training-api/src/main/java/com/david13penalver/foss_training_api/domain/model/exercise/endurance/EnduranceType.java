package com.david13penalver.foss_training_api.domain.model.exercise.endurance;

import lombok.Getter;

@Getter
public enum EnduranceType {

    AEROBIC(
            "Aerobic Endurance",
            "Increases mitochondrial density and oxidative enzyme activity; enhances cardiac stroke volume and oxygen extraction capacity. Improves capillary density for better oxygen delivery to muscles.",
            "Sustained low-to-moderate intensity exercise using oxygen for energy. Typically 60-80% max heart rate.",
            "Long-distance running, cycling, swimming"),

    ANAEROBIC(
            "Anaerobic Endurance",
            "Increases glycolytic enzyme activity and lactate buffering capacity; improves phosphocreatine (PCr) system efficiency. Enhances ability to tolerate and clear lactate during high-intensity efforts.",
            "High-intensity exercise where the body works without sufficient oxygen. Typically 80-95% max heart rate.",
            "Sprint intervals, hill repeats, track workouts"),

    HIIT(
            "High-Intensity Interval Training",
            "Maximizes VO2max improvement (comparable gains to 5x longer continuous training); increases mitochondrial content and AMPK activation. Improves both aerobic and anaerobic capacity simultaneously.",
            "Short bursts of maximum effort followed by rest periods. Alternates between aerobic and anaerobic zones.",
            "Tabata, sprint intervals, circuit training"),

    SIT(
            "Sprint Interval Training",
            "Induces rapid mitochondrial biogenesis and oxidative enzyme increases; improves insulin sensitivity and metabolic health with minimal time commitment. Produces 19% VO2max gains in 30 min/week vs 150 min/week MICT.",
            "All-out supramaximal sprints (95-100% max effort) interspersed with recovery periods. Typically 30-second sprints with 2-4 min recovery.",
            "Wingate sprints, cycle sprints, running sprints"),

    LISS(
            "Low-Intensity Steady State",
            "Develops aerobic base and mitochondrial function; improves fat oxidation capacity and parasympathetic recovery. Minimal CNS fatigue allowing frequent training.",
            "Prolonged low-intensity cardio at consistent pace. Typically 50-65% max heart rate for 30-60+ minutes.",
            "Walking, light jogging, easy cycling"),

    TEMPO(
            "Tempo Training",
            "Raises lactate threshold and improves sustained power output near lactate turnover; increases oxidative capacity in type I and type IIa fibers. Teaches body to work efficiently at race pace.",
            "Sustained effort at lactate threshold (70-85% max heart rate). Challenging but sustainable pace.",
            "Tempo runs, threshold intervals"),

    FARTLEK(
            "Fartlek Training",
            "Combines aerobic base development with anaerobic adaptations; trains neural recruitment patterns across varying intensities. Improves running economy and speed-endurance.",
            "Unstructured speed play mixing fast and slow intervals. Swedish for 'speed play'.",
            "Variable pace running with spontaneous surges"),

    STEADY_STATE(
            "Steady State Cardio",
            "Builds aerobic capacity and muscular endurance; improves cardiorespiratory efficiency at submaximal intensities. Develops stroke volume and ventilatory threshold.",
            "Moderate intensity maintained for extended duration. Base endurance building at 65-75% max heart rate.",
            "Consistent pace running, cycling, rowing");

    private final String name;
    private final String scientificObjective;
    private final String description;
    private final String examples;

    EnduranceType(String name, String scientificObjective, String description, String examples) {
        this.name = name;
        this.scientificObjective = scientificObjective;
        this.description = description;
        this.examples = examples;
    }

    public boolean isHighIntensity() {
        return this == HIIT || this == ANAEROBIC || this == SIT || this == TEMPO;
    }

    public boolean isLowIntensity() {
        return this == LISS || this == AEROBIC || this == STEADY_STATE;
    }

    public boolean isTimeEfficient() {
        return this == SIT || this == HIIT;
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
