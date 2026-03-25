package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindAllMobilityTypesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllMobilityTypesUseCaseImpl implements FindAllMobilityTypesUseCase {
    
    private final MobilityTypeService mobilityTypeService;
    
    @Override
    public List<MobilityType> execute() {
        log.debug("Executing FindAllMobilityTypesUseCase");
        return mobilityTypeService.findAll();
    }
}

