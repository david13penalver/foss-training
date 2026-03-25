package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindAllMuscleGroupsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllMuscleGroupsUseCaseImpl implements FindAllMuscleGroupsUseCase {
    
    private final MuscleGroupService muscleGroupService;
    
    @Override
    public List<MuscleGroup> execute() {
        log.debug("Executing FindAllMuscleGroupsUseCase");
        return muscleGroupService.findAll();
    }
}

