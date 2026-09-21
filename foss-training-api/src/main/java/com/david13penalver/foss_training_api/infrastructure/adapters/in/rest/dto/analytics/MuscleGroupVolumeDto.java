package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import com.david13penalver.foss_training_api.domain.model.analytics.HypertrophyVolumeStatus;
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
@Schema(name = "MuscleGroupVolume", description = "Aggregated working volume for an individual muscle group")
public class MuscleGroupVolumeDto {

    @Schema(description = "Muscle group identifier", example = "CHEST")
    private MuscleGroup muscleGroup;

    @Schema(description = "Human-readable muscle group name", example = "Chest")
    private String muscleGroupName;

    @Schema(description = "Anatomical category", example = "UPPER_BODY")
    private MuscleCategory category;

    @Schema(description = "Number of primary/direct working sets", example = "12")
    private int directSets;

    @Schema(description = "Number of secondary/indirect synergist sets", example = "6")
    private int indirectSets;

    @Schema(description = "Effective working sets (direct + 0.5 * indirect)", example = "15.0")
    private double effectiveSets;

    @Schema(description = "Total tonnage moved in kilograms", example = "8400.0")
    private double totalVolumeKg;

    @Schema(description = "Hypertrophy landmark classification", example = "OPTIMAL")
    private HypertrophyVolumeStatus status;

    @Schema(description = "Human-readable status label", example = "Optimal Hypertrophy")
    private String statusDisplayName;

    @Schema(description = "Evidence-based guideline for this volume level")
    private String statusDescription;
}
