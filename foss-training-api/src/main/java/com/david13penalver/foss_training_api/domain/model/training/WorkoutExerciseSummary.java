package com.david13penalver.foss_training_api.domain.model.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutExerciseSummary {

    private Integer exerciseId;
    private String exerciseName;
    private String category;
    private Integer completedSets;
    private Integer totalReps;
    private Double topWeightKg;
    private Double volumeKg;
    private Double estimated1RmKg;
}
