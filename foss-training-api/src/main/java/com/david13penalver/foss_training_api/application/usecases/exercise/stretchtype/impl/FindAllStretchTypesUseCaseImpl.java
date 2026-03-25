package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindAllStretchTypesUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllStretchTypesUseCaseImpl implements FindAllStretchTypesUseCase {
    
    private final StretchTypeService stretchTypeService;
    
    @Override
    public List<StretchType> execute() {
        log.debug("Executing FindAllStretchTypesUseCase");
        return stretchTypeService.findAll();
    }
}

