package com.david13penalver.foss_training_api.domain.model.program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgram {

    private Integer id;
    private String name;
    private String description;
    private int durationWeeks;
    private PeriodizationType periodizationType;
    private ProgramLevel level;
    private List<ProgramWorkout> workouts = new ArrayList<>();
    private Boolean isActive = true;

    public void addWorkout(ProgramWorkout workout) {
        if (workout != null) {
            workout.validate();
            if (workouts == null) {
                workouts = new ArrayList<>();
            }
            workouts.add(workout);
        }
    }

    public List<ProgramWorkout> getWorkouts() {
        return workouts != null ? Collections.unmodifiableList(workouts) : Collections.emptyList();
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Program name cannot be null or blank");
        }
        if (durationWeeks <= 0) {
            throw new IllegalArgumentException("Program duration must be greater than zero weeks");
        }
        if (workouts != null) {
            workouts.forEach(ProgramWorkout::validate);
        }
    }
}
