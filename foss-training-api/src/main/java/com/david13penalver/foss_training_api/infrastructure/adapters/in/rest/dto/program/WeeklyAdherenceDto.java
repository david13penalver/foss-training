package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "WeeklyAdherence", description = "Week-by-week adherence metrics for a training program")
public class WeeklyAdherenceDto {

    @Schema(description = "Week number in the program cycle (1-indexed)", example = "1")
    private int weekNumber;

    @Schema(description = "Start date of the training week", example = "2026-09-01")
    private LocalDate weekStartDate;

    @Schema(description = "End date of the training week", example = "2026-09-07")
    private LocalDate weekEndDate;

    @Schema(description = "Number of scheduled workouts for this week", example = "4")
    private int scheduledWorkouts;

    @Schema(description = "Number of completed workouts for this week", example = "4")
    private int completedWorkouts;

    @Schema(description = "Number of missed or skipped workouts for this week", example = "0")
    private int missedWorkouts;

    @Schema(description = "Weekly adherence rate percentage (0.0 - 100.0)", example = "100.0")
    private double adherenceRate;

    @Schema(description = "Whether all scheduled workouts for this week have been completed", example = "true")
    private boolean completed;
}
