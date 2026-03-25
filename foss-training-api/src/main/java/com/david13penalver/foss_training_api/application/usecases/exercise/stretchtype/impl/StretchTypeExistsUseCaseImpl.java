package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.StretchTypeExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StretchTypeExistsUseCaseImpl implements StretchTypeExistsUseCase {
    
    private final StretchTypeService stretchTypeService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing StretchTypeExistsUseCase with name: {}", name);
        return stretchTypeService.existsById(name);
    }
}

