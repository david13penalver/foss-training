package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.DeleteMobilityTypeUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteMobilityTypeUseCaseImpl implements DeleteMobilityTypeUseCase {
    
    private final MobilityTypeService mobilityTypeService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteMobilityTypeUseCase with name: {}", name);
        mobilityTypeService.deleteById(name);
    }
}

