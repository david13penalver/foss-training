package com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindMobilityTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindMobilityTypeByNameService implements FindMobilityTypeByNameUseCase {
    
    @Override
    public Optional<MobilityType> execute(String name) {
        log.debug("Executing FindMobilityTypeByNameUseCase with name: {}", name);
        return Arrays.stream(MobilityType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
