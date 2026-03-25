package com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

public interface SaveMovementPatternUseCase {
    
    MovementPattern execute(MovementPattern movementPattern);
}

