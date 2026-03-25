package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.SaveMuscleGroupUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveMuscleGroupUseCaseImpl implements SaveMuscleGroupUseCase {
    
    private final MuscleGroupService muscleGroupService;
    
    @Override
    public MuscleGroup execute(MuscleGroup muscleGroup) {
        log.debug("Executing SaveMuscleGroupUseCase with muscle group: {}", muscleGroup.getName());
        return muscleGroupService.save(muscleGroup);
    }
}

