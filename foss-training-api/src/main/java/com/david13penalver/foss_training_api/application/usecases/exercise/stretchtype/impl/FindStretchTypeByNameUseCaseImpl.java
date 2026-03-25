package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindStretchTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindStretchTypeByNameUseCaseImpl implements FindStretchTypeByNameUseCase {
    
    private final StretchTypeService stretchTypeService;
    
    @Override
    public Optional<StretchType> execute(String name) {
        log.debug("Executing FindStretchTypeByNameUseCase with name: {}", name);
        return stretchTypeService.findById(name);
    }
}

