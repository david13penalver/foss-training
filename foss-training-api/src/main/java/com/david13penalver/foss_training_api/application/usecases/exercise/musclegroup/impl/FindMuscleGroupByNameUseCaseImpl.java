package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindMuscleGroupByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindMuscleGroupByNameUseCaseImpl implements FindMuscleGroupByNameUseCase {
    
    private final MuscleGroupService muscleGroupService;
    
    @Override
    public Optional<MuscleGroup> execute(String name) {
        log.debug("Executing FindMuscleGroupByNameUseCase with name: {}", name);
        return muscleGroupService.findById(name);
    }
}

