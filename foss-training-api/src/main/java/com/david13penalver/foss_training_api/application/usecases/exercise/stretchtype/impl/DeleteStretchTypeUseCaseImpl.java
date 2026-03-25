package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.DeleteStretchTypeUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteStretchTypeUseCaseImpl implements DeleteStretchTypeUseCase {
    
    private final StretchTypeService stretchTypeService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteStretchTypeUseCase with name: {}", name);
        stretchTypeService.deleteById(name);
    }
}

