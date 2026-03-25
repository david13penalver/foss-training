package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.DeleteEnduranceTypeUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteEnduranceTypeUseCaseImpl implements DeleteEnduranceTypeUseCase {
    
    private final EnduranceTypeService enduranceTypeService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteEnduranceTypeUseCase with name: {}", name);
        enduranceTypeService.deleteById(name);
    }
}

