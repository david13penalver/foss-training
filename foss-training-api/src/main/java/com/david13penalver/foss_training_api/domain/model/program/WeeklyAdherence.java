package com.david13penalver.foss_training_api.domain.model.program;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyAdherence {

    private int weekNumber;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private int scheduledWorkouts;
    private int completedWorkouts;
    private int missedWorkouts;
    private double adherenceRate; // percentage (0.0 to 100.0)
    private boolean completed; // true if all scheduled workouts for this week are completed
}
