package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindAllMovementPatternsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllMovementPatternsUseCaseImpl implements FindAllMovementPatternsUseCase {
    
    private final MovementPatternService movementPatternService;
    
    @Override
    public List<MovementPattern> execute() {
        log.debug("Executing FindAllMovementPatternsUseCase");
        return movementPatternService.findAll();
    }
}

