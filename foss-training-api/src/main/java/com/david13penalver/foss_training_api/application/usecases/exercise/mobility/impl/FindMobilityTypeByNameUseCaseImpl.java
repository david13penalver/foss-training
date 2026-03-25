package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindMobilityTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindMobilityTypeByNameUseCaseImpl implements FindMobilityTypeByNameUseCase {
    
    private final MobilityTypeService mobilityTypeService;
    
    @Override
    public Optional<MobilityType> execute(String name) {
        log.debug("Executing FindMobilityTypeByNameUseCase with name: {}", name);
        return mobilityTypeService.findById(name);
    }
}

