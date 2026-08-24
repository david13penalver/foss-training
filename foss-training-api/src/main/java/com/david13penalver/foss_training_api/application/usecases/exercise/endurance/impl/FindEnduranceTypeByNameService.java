package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindEnduranceTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindEnduranceTypeByNameService implements FindEnduranceTypeByNameUseCase {
    
    @Override
    public Optional<EnduranceType> execute(String name) {
        log.debug("Executing FindEnduranceTypeByNameUseCase with name: {}", name);
        return Arrays.stream(EnduranceType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
