package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "WeeklyMuscleVolumeResponse", description = "Weekly muscle group working volume and hypertrophy balance analysis")
public class WeeklyMuscleVolumeResponseDto {

    @Schema(description = "Start date of the evaluation window", example = "2026-09-15")
    private LocalDate startDate;

    @Schema(description = "End date of the evaluation window", example = "2026-09-21")
    private LocalDate endDate;

    @Schema(description = "Total completed working sets across all exercises", example = "48")
    private int totalWorkingSets;

    @Schema(description = "Total tonnage moved in kilograms", example = "32500.0")
    private double totalVolumeKg;

    @Schema(description = "Volume breakdown per muscle group, ordered by effective sets descending")
    private List<MuscleGroupVolumeDto> muscleVolumes;

    @Schema(description = "Effective sets aggregated by anatomical category (UPPER_BODY, LOWER_BODY, etc.)")
    private Map<MuscleCategory, Double> categoryVolumes;

    @Schema(description = "Ratio of pushing sets to pulling sets", example = "1.10")
    private double pushPullRatio;

    @Schema(description = "Ratio of upper body sets to lower body sets", example = "1.45")
    private double upperLowerRatio;

    @Schema(description = "Major muscle groups with insufficient volume (< 6 sets)")
    private List<MuscleGroup> neglectedMuscleGroups;

    @Schema(description = "Muscle groups within the optimal hypertrophy zone (10-20 sets)")
    private List<MuscleGroup> optimalMuscleGroups;

    @Schema(description = "Muscle groups exceeding maximum recoverable volume (> 20 sets)")
    private List<MuscleGroup> overtrainedMuscleGroups;

    @Schema(description = "Actionable evidence-based coaching recommendations")
    private List<String> recommendations;
}
