package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.DeleteMuscleGroupUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteMuscleGroupUseCaseImpl implements DeleteMuscleGroupUseCase {
    
    private final MuscleGroupService muscleGroupService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteMuscleGroupUseCase with name: {}", name);
        muscleGroupService.deleteById(name);
    }
}

