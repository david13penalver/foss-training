package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.MovementPatternExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovementPatternExistsUseCaseImpl implements MovementPatternExistsUseCase {
    
    private final MovementPatternService movementPatternService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing MovementPatternExistsUseCase with name: {}", name);
        return movementPatternService.existsById(name);
    }
}

