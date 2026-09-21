package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "WorkoutExerciseSummary", description = "Per-exercise volume and performance breakdown")
public class WorkoutExerciseSummaryDto {

    @Schema(description = "Exercise ID", example = "1")
    private Integer exerciseId;

    @Schema(description = "Exercise name", example = "Barbell Bench Press")
    private String exerciseName;

    @Schema(description = "Primary category", example = "RESISTANCE")
    private String category;

    @Schema(description = "Total completed working sets", example = "4")
    private Integer completedSets;

    @Schema(description = "Total repetitions completed across working sets", example = "32")
    private Integer totalReps;

    @Schema(description = "Heaviest weight lifted in kg", example = "100.0")
    private Double topWeightKg;

    @Schema(description = "Volume tonnage moved for this exercise in kg", example = "3200.0")
    private Double volumeKg;

    @Schema(description = "Estimated 1RM based on best set in kg", example = "116.67")
    private Double estimated1RmKg;
}
