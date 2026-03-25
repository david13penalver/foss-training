package com.david13penalver.foss_training_api.application.usecases.exercise.mobility;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

public interface FindAllMobilityTypesUseCase {
    
    List<MobilityType> execute();
}

