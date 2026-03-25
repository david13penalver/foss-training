package com.david13penalver.foss_training_api.application.usecases.exercise.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

public interface SaveExerciseUseCase {
    
    Exercise execute(Exercise exercise);
}

