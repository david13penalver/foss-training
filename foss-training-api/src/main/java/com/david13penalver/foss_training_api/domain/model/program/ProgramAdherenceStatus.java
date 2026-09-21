package com.david13penalver.foss_training_api.domain.model.program;

public enum ProgramAdherenceStatus {
    ON_TRACK(
            "On Track",
            "Excellent adherence to program schedule. Consistency is optimal for athletic adaptations."),
    BEHIND_SCHEDULE(
            "Behind Schedule",
            "Adherence has dipped below target. Consider scheduling makeup workouts or adjusting rest days."),
    AT_RISK(
            "At Risk",
            "Multiple workouts have been missed. Re-evaluate program intensity, volume, or calendar schedule."),
    COMPLETED(
            "Completed",
            "All scheduled program workouts have been completed! Congratulations on finishing the mesocycle."),
    NOT_STARTED(
            "Not Started",
            "Program workouts have not yet commenced.");

    private final String displayName;
    private final String description;

    ProgramAdherenceStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
