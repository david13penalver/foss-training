package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

public interface FindStretchTypeByNameUseCase {
    
    Optional<StretchType> execute(String name);
}

