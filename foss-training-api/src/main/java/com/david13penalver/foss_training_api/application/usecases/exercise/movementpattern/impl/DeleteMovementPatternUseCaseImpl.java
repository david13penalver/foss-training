package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.DeleteMovementPatternUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteMovementPatternUseCaseImpl implements DeleteMovementPatternUseCase {
    
    private final MovementPatternService movementPatternService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteMovementPatternUseCase with name: {}", name);
        movementPatternService.deleteById(name);
    }
}

