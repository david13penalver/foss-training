package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.DeleteExerciseUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteExerciseUseCaseImpl implements DeleteExerciseUseCase {
    
    private final ExerciseService exerciseService;
    
    @Override
    public void execute(Integer id) {
        log.debug("Executing DeleteExerciseUseCase with id: {}", id);
        exerciseService.deleteById(id);
    }
}

