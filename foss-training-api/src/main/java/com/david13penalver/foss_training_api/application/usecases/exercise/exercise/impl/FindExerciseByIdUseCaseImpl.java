package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindExerciseByIdUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindExerciseByIdUseCaseImpl implements FindExerciseByIdUseCase {
    
    private final ExerciseService exerciseService;
    
    @Override
    public Optional<Exercise> execute(Integer id) {
        log.debug("Executing FindExerciseByIdUseCase with id: {}", id);
        return exerciseService.findById(id);
    }
}

