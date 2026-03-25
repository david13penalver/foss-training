package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.SaveMovementPatternUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveMovementPatternUseCaseImpl implements SaveMovementPatternUseCase {
    
    private final MovementPatternService movementPatternService;
    
    @Override
    public MovementPattern execute(MovementPattern movementPattern) {
        log.debug("Executing SaveMovementPatternUseCase with movement pattern: {}", movementPattern);
        return movementPatternService.save(movementPattern);
    }
}

