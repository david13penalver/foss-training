package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindAllExercisesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllExercisesUseCaseImpl implements FindAllExercisesUseCase {
    
    private final ExerciseService exerciseService;
    
    @Override
    public List<Exercise> execute() {
        log.debug("Executing FindAllExercisesUseCase");
        return exerciseService.findAll();
    }
}

