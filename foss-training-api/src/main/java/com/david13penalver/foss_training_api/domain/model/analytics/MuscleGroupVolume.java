package com.david13penalver.foss_training_api.domain.model.analytics;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MuscleGroupVolume {

    private MuscleGroup muscleGroup;
    private String muscleGroupName;
    private MuscleCategory category;
    private int directSets;
    private int indirectSets;
    private double effectiveSets;
    private double totalVolumeKg;
    private HypertrophyVolumeStatus status;
}
