package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindAllStretchTypesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllStretchTypesService implements FindAllStretchTypesUseCase {
    
    @Override
    public List<StretchType> execute() {
        log.debug("Executing FindAllStretchTypesUseCase");
        return List.of(StretchType.values());
    }
}
