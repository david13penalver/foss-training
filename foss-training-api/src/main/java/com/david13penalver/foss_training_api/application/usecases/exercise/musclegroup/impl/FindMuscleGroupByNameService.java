package com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindMuscleGroupByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindMuscleGroupByNameService implements FindMuscleGroupByNameUseCase {
    
    @Override
    public Optional<MuscleGroup> execute(String name) {
        log.debug("Executing FindMuscleGroupByNameUseCase with name: {}", name);
        return Arrays.stream(MuscleGroup.values())
                .filter(group -> group.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
