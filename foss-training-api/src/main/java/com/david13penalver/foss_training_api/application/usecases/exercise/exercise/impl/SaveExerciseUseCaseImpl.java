package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.SaveExerciseUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveExerciseUseCaseImpl implements SaveExerciseUseCase {
    
    private final ExerciseService exerciseService;
    
    @Override
    public Exercise execute(Exercise exercise) {
        log.debug("Executing SaveExerciseUseCase with exercise: {}", exercise.getName());
        return exerciseService.save(exercise);
    }
}

