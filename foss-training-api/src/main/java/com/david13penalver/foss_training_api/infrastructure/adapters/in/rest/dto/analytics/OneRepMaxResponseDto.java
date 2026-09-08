package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.util.Map;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "OneRepMaxResponse", description = "Estimated 1RM and percentage-based training loads")
public class OneRepMaxResponseDto {

    @Schema(description = "Input weight value", example = "100.0")
    private double weight;

    @Schema(description = "Input weight unit", example = "KG")
    private WeightUnit unit;

    @Schema(description = "Input repetitions", example = "5")
    private int repetitions;

    @Schema(description = "Formula used for estimation", example = "EPLEY")
    private OneRepMaxFormula formula;

    @Schema(description = "Calculated 1RM estimate", example = "116.67")
    private double estimated1Rm;

    @Schema(description = "Training loads by percentage of 1RM (50% to 95%)")
    private Map<Integer, Double> percentages;
}
