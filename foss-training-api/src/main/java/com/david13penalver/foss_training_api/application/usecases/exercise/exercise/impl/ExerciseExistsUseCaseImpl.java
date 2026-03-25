package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.ExerciseExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseExistsUseCaseImpl implements ExerciseExistsUseCase {
    
    private final ExerciseService exerciseService;
    
    @Override
    public boolean execute(Integer id) {
        log.debug("Executing ExerciseExistsUseCase with id: {}", id);
        return exerciseService.existsById(id);
    }
}

