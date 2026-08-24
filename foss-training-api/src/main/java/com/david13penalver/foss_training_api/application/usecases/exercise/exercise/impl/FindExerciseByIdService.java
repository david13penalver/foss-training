package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindExerciseByIdUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindExerciseByIdService implements FindExerciseByIdUseCase {
    
    private final ExerciseRepository exerciseRepository;
    
    @Override
    public Optional<Exercise> execute(Integer id) {
        log.debug("Executing FindExerciseByIdUseCase with id: {}", id);
        return exerciseRepository.findById(id);
    }
}
