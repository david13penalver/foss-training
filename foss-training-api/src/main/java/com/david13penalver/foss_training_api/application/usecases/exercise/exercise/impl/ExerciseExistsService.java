package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.ExerciseExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseExistsService implements ExerciseExistsUseCase {
    
    private final ExerciseRepository exerciseRepository;
    
    @Override
    public boolean execute(Integer id) {
        log.debug("Executing ExerciseExistsUseCase with id: {}", id);
        return exerciseRepository.existsById(id);
    }
}
