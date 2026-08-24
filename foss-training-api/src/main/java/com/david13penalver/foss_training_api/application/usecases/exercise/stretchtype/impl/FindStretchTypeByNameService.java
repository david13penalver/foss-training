package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindStretchTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindStretchTypeByNameService implements FindStretchTypeByNameUseCase {
    
    @Override
    public Optional<StretchType> execute(String name) {
        log.debug("Executing FindStretchTypeByNameUseCase with name: {}", name);
        return Arrays.stream(StretchType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
