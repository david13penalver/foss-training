package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindAllExercisesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindAllExercisesService implements FindAllExercisesUseCase {
    
    private final ExerciseRepository exerciseRepository;
    
    @Override
    public List<Exercise> execute() {
        log.debug("Executing FindAllExercisesUseCase");
        return exerciseRepository.findAll();
    }
}
