package com.david13penalver.foss_training_api.application.usecases.exercise.endurance;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

public interface FindEnduranceTypeByNameUseCase {
    
    Optional<EnduranceType> execute(String name);
}

