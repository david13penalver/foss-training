package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import java.time.LocalDate;

import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "WorkoutAdherenceItem", description = "Workout-level compliance detail for a scheduled session")
public class WorkoutAdherenceItemDto {

    @Schema(description = "Training execution ID", example = "101")
    private Integer trainingId;

    @Schema(description = "Workout session name", example = "12-Week Hypertrophy - W1D1: Upper Strength")
    private String workoutName;

    @Schema(description = "Scheduled workout date", example = "2026-09-01")
    private LocalDate scheduledDate;

    @Schema(description = "Actual completion date", example = "2026-09-01")
    private LocalDate completedDate;

    @Schema(description = "Training execution status", example = "COMPLETED")
    private TrainingStatusEnum status;

    @Schema(description = "Session RPE rating (1-10)", example = "8.5")
    private Double sessionRpe;

    @Schema(description = "Total volume load lifted in kg", example = "8500.0")
    private Double volumeKg;

    @Schema(description = "Whether workout was completed on-time (within grace period)", example = "true")
    private boolean onTime;
}
