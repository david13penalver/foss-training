package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

public interface FindMovementPatternByNameUseCase {
    
    Optional<MovementPattern> execute(String name);
}

