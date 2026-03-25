package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.SaveMobilityTypeUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveMobilityTypeUseCaseImpl implements SaveMobilityTypeUseCase {
    
    private final MobilityTypeService mobilityTypeService;
    
    @Override
    public MobilityType execute(MobilityType mobilityType) {
        log.debug("Executing SaveMobilityTypeUseCase with mobility type: {}", mobilityType.getName());
        return mobilityTypeService.save(mobilityType);
    }
}

