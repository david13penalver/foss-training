package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

public interface SaveMuscleGroupUseCase {
    
    MuscleGroup execute(MuscleGroup muscleGroup);
}

