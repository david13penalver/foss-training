package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.SaveExerciseUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveExerciseService implements SaveExerciseUseCase {
    
    private final ExerciseRepository exerciseRepository;
    
    @Override
    public Exercise execute(Exercise exercise) {
        log.debug("Executing SaveExerciseUseCase with exercise: {}", exercise.getName());
        return exerciseRepository.save(exercise);
    }
}
