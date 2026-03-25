package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.MobilityTypeExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MobilityTypeExistsUseCaseImpl implements MobilityTypeExistsUseCase {
    
    private final MobilityTypeService mobilityTypeService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing MobilityTypeExistsUseCase with name: {}", name);
        return mobilityTypeService.existsById(name);
    }
}

