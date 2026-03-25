package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindMovementPatternByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindMovementPatternByNameUseCaseImpl implements FindMovementPatternByNameUseCase {
    
    private final MovementPatternService movementPatternService;
    
    @Override
    public Optional<MovementPattern> execute(String name) {
        log.debug("Executing FindMovementPatternByNameUseCase with name: {}", name);
        return movementPatternService.findById(name);
    }
}

