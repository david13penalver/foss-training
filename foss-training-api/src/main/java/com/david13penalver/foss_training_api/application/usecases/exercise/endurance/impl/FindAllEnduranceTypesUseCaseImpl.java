package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindAllEnduranceTypesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllEnduranceTypesUseCaseImpl implements FindAllEnduranceTypesUseCase {
    
    private final EnduranceTypeService enduranceTypeService;
    
    @Override
    public List<EnduranceType> execute() {
        log.debug("Executing FindAllEnduranceTypesUseCase");
        return enduranceTypeService.findAll();
    }
}

