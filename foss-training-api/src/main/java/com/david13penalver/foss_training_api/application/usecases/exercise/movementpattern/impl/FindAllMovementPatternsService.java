package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindAllMovementPatternsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllMovementPatternsService implements FindAllMovementPatternsUseCase {
    
    @Override
    public List<MovementPattern> execute() {
        log.debug("Executing FindAllMovementPatternsUseCase");
        return List.of(MovementPattern.values());
    }
}
