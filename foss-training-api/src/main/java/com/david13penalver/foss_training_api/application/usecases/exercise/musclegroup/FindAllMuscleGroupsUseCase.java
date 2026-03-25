package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

public interface FindAllMuscleGroupsUseCase {
    
    List<MuscleGroup> execute();
}

