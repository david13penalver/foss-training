package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.time.LocalDate;

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
@Schema(name = "PersonalRecordResponse", description = "Personal records achieved for an exercise across completed workouts")
public class PersonalRecordResponseDto {

    @Schema(description = "Exercise ID", example = "1")
    private Integer exerciseId;

    @Schema(description = "Exercise name", example = "Bench Press")
    private String exerciseName;

    @Schema(description = "Maximum weight record")
    private MaxWeightRecordDto maxWeight;

    @Schema(description = "Best estimated 1RM record")
    private BestEstimated1RmRecordDto bestEstimated1Rm;

    @Schema(description = "Maximum session volume record")
    private MaxSessionVolumeRecordDto maxSessionVolume;

    @Schema(description = "Maximum repetitions record")
    private MaxRepsRecordDto maxReps;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "MaxWeightRecord")
    public static class MaxWeightRecordDto {
        private double value;
        private WeightUnit unit;
        private int repetitions;
        private Integer trainingId;
        private LocalDate trainingDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "BestEstimated1RmRecord")
    public static class BestEstimated1RmRecordDto {
        private double estimated1Rm;
        private WeightUnit unit;
        private double sourceWeight;
        private int sourceReps;
        private OneRepMaxFormula formula;
        private Integer trainingId;
        private LocalDate trainingDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "MaxSessionVolumeRecord")
    public static class MaxSessionVolumeRecordDto {
        private double volume;
        private WeightUnit unit;
        private Integer trainingId;
        private LocalDate trainingDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "MaxRepsRecord")
    public static class MaxRepsRecordDto {
        private int repetitions;
        private double weight;
        private WeightUnit unit;
        private Integer trainingId;
        private LocalDate trainingDate;
    }
}
