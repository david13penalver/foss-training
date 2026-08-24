package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindAllMuscleGroupsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllMuscleGroupsService implements FindAllMuscleGroupsUseCase {
    
    @Override
    public List<MuscleGroup> execute() {
        log.debug("Executing FindAllMuscleGroupsUseCase");
        return List.of(MuscleGroup.values());
    }
}
