package com.david13penalver.foss_training_api.application.usecases.exercise.mobility;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

public interface FindMobilityTypeByNameUseCase {
    
    Optional<MobilityType> execute(String name);
}

