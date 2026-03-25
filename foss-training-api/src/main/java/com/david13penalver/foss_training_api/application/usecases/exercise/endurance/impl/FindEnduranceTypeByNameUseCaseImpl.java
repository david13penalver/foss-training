package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindEnduranceTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindEnduranceTypeByNameUseCaseImpl implements FindEnduranceTypeByNameUseCase {
    
    private final EnduranceTypeService enduranceTypeService;
    
    @Override
    public Optional<EnduranceType> execute(String name) {
        log.debug("Executing FindEnduranceTypeByNameUseCase with name: {}", name);
        return enduranceTypeService.findById(name);
    }
}

