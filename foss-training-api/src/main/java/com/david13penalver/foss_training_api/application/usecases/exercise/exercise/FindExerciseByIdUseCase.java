package com.david13penalver.foss_training_api.application.usecases.exercise.exercise;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

public interface FindExerciseByIdUseCase {
    
    Optional<Exercise> execute(Integer id);
}

