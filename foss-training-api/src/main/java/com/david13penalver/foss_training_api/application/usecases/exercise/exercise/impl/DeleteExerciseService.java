package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.DeleteExerciseUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteExerciseService implements DeleteExerciseUseCase {
    
    private final ExerciseRepository exerciseRepository;
    
    @Override
    public void execute(Integer id) {
        log.debug("Executing DeleteExerciseUseCase with id: {}", id);
        if (!exerciseRepository.existsById(id)) {
            throw new IllegalArgumentException("Exercise not found with id: " + id);
        }
        exerciseRepository.deleteById(id);
    }
}
