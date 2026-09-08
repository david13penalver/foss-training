package com.david13penalver.foss_training_api.domain.model.analytics;

import java.time.LocalDate;

import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalRecord {

    private Integer exerciseId;
    private String exerciseName;
    private MaxWeightRecord maxWeight;
    private BestEstimated1RmRecord bestEstimated1Rm;
    private MaxSessionVolumeRecord maxSessionVolume;
    private MaxRepsRecord maxReps;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaxWeightRecord {
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
    public static class BestEstimated1RmRecord {
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
    public static class MaxSessionVolumeRecord {
        private double volume;
        private WeightUnit unit;
        private Integer trainingId;
        private LocalDate trainingDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaxRepsRecord {
        private int repetitions;
        private double weight;
        private WeightUnit unit;
        private Integer trainingId;
        private LocalDate trainingDate;
    }
}
