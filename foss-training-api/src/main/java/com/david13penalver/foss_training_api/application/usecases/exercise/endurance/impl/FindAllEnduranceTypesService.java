package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindAllEnduranceTypesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllEnduranceTypesService implements FindAllEnduranceTypesUseCase {
    
    @Override
    public List<EnduranceType> execute() {
        log.debug("Executing FindAllEnduranceTypesUseCase");
        return List.of(EnduranceType.values());
    }
}
