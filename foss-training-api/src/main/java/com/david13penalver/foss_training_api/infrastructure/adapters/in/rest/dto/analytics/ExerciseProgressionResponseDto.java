package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.time.LocalDate;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.analytics.ProgressionTrend;
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
@Schema(description = "Exercise strength progression time-series and trend analytics")
public class ExerciseProgressionResponseDto {

    @Schema(description = "Exercise identifier", example = "1")
    private Integer exerciseId;

    @Schema(description = "Exercise name", example = "Barbell Bench Press")
    private String exerciseName;

    @Schema(description = "Formula used for estimated 1RM calculations", example = "EPLEY")
    private OneRepMaxFormula formula;

    @Schema(description = "Start date of analysis range", example = "2026-08-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "End date of analysis range", example = "2026-09-21")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "Number of completed sessions with this exercise", example = "8")
    private int totalSessions;

    @Schema(description = "Estimated 1RM from the earliest session in kg", example = "95.0")
    private double initial1RmKg;

    @Schema(description = "Estimated 1RM from the most recent session in kg", example = "115.0")
    private double latest1RmKg;

    @Schema(description = "Absolute change in estimated 1RM (latest - initial) in kg", example = "20.0")
    private double absolute1RmGainKg;

    @Schema(description = "Relative change in estimated 1RM as a percentage", example = "21.05")
    private double relative1RmGainPercentage;

    @Schema(description = "All-time peak estimated 1RM across sessions in kg", example = "115.0")
    private double allTimeBest1RmKg;

    @Schema(description = "All-time peak top set weight across sessions in kg", example = "105.0")
    private double allTimeBestTopWeightKg;

    @Schema(description = "All-time peak single-session volume across sessions in kg", example = "3200.0")
    private double allTimeMaxVolumeKg;

    @Schema(description = "Strength progression trajectory trend", example = "IMPROVING")
    private ProgressionTrend trend;

    @Schema(description = "Display name for progression trend", example = "Improving")
    private String trendDisplayName;

    @Schema(description = "Explanation of progression trend status", example = "Strength performance has increased over time")
    private String trendDescription;

    @Schema(description = "Chronologically ordered session progression data points")
    private List<ProgressionDataPointDto> dataPoints;
}
