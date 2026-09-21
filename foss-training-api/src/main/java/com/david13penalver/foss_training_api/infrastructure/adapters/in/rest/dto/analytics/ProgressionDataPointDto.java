package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Exercise strength performance metrics for a specific completed session")
public class ProgressionDataPointDto {

    @Schema(description = "ID of the completed training session", example = "10")
    private Integer trainingId;

    @Schema(description = "Date the workout was completed", example = "2026-09-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "Total sets performed for this exercise in this session", example = "4")
    private int totalSets;

    @Schema(description = "Total working sets performed", example = "3")
    private int workingSets;

    @Schema(description = "Total repetitions completed across working sets", example = "24")
    private int totalReps;

    @Schema(description = "Total session volume / tonnage for this exercise in kg", example = "2400.0")
    private double totalVolumeKg;

    @Schema(description = "Heaviest weight lifted in a single set (converted to kg)", example = "100.0")
    private double topWeightKg;

    @Schema(description = "Repetitions performed on the top weight set", example = "5")
    private int topWeightReps;

    @Schema(description = "RPE recorded on top weight set, if provided", example = "8.5")
    private Double topWeightRpe;

    @Schema(description = "Estimated 1RM for this session in kg", example = "116.67")
    private double estimated1RmKg;

    @Schema(description = "Average load intensity (volume / total reps in kg)", example = "83.33")
    private double averageIntensityKg;
}
