package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import java.time.LocalDateTime;
import java.util.List;

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
@Schema(name = "WorkoutSummaryResponse", description = "Comprehensive live and post-workout summary metrics")
public class WorkoutSummaryResponseDto {

    @Schema(description = "Training session ID", example = "1")
    private Integer trainingId;

    @Schema(description = "Training session name", example = "Upper Body Hypertrophy")
    private String trainingName;

    @Schema(description = "Current lifecycle status", example = "COMPLETED")
    private TrainingStatusEnum status;

    @Schema(description = "Workout start timestamp")
    private LocalDateTime startTime;

    @Schema(description = "Workout end timestamp")
    private LocalDateTime endTime;

    @Schema(description = "Total workout duration in seconds", example = "3600")
    private Integer durationSeconds;

    @Schema(description = "Human-readable formatted duration", example = "1h 00m 00s")
    private String formattedDuration;

    @Schema(description = "Total volume tonnage lifted in kilograms", example = "14250.0")
    private Double totalVolumeKg;

    @Schema(description = "Total number of working sets performed across all exercises", example = "16")
    private Integer totalWorkingSets;

    @Schema(description = "Total repetitions completed across all working sets", example = "144")
    private Integer totalReps;

    @Schema(description = "Overall session RPE rating (1-10)", example = "8.5")
    private Double sessionRpe;

    @Schema(description = "Athlete notes")
    private String notes;

    @Schema(description = "Per-exercise performance breakdown")
    private List<WorkoutExerciseSummaryDto> exerciseSummaries;
}
