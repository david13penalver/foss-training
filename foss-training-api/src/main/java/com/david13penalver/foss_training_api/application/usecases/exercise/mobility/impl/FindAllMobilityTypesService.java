package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindAllMobilityTypesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllMobilityTypesService implements FindAllMobilityTypesUseCase {
    
    @Override
    public List<MobilityType> execute() {
        log.debug("Executing FindAllMobilityTypesUseCase");
        return List.of(MobilityType.values());
    }
}
