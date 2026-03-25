package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.MuscleGroupExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MuscleGroupExistsUseCaseImpl implements MuscleGroupExistsUseCase {
    
    private final MuscleGroupService muscleGroupService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing MuscleGroupExistsUseCase with name: {}", name);
        return muscleGroupService.existsById(name);
    }
}

