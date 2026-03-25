package com.david13penalver.foss_training_api.application.usecases.exercise.exercise;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

public interface FindAllExercisesUseCase {
    
    List<Exercise> execute();
}

