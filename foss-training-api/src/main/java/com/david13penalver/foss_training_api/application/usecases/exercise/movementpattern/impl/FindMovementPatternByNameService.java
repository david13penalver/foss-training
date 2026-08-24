package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindMovementPatternByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindMovementPatternByNameService implements FindMovementPatternByNameUseCase {
    
    @Override
    public Optional<MovementPattern> execute(String name) {
        log.debug("Executing FindMovementPatternByNameUseCase with name: {}", name);
        return Arrays.stream(MovementPattern.values())
                .filter(pattern -> pattern.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
