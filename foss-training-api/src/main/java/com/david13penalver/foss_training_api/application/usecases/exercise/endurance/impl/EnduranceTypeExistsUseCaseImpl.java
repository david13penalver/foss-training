package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.EnduranceTypeExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnduranceTypeExistsUseCaseImpl implements EnduranceTypeExistsUseCase {
    
    private final EnduranceTypeService enduranceTypeService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing EnduranceTypeExistsUseCase with name: {}", name);
        return enduranceTypeService.existsById(name);
    }
}

