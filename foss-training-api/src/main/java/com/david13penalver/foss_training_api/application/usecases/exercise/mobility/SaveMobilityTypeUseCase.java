package com.david13penalver.foss_training_api.application.usecases.exercise.mobility;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

public interface SaveMobilityTypeUseCase {
    
    MobilityType execute(MobilityType mobilityType);
}

