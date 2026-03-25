package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

public interface FindMuscleGroupByNameUseCase {
    
    Optional<MuscleGroup> execute(String name);
}

