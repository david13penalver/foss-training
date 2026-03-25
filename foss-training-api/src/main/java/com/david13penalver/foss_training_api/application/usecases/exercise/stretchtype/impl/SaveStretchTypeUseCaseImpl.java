package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.SaveStretchTypeUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveStretchTypeUseCaseImpl implements SaveStretchTypeUseCase {
    
    private final StretchTypeService stretchTypeService;
    
    @Override
    public StretchType execute(StretchType stretchType) {
        log.debug("Executing SaveStretchTypeUseCase with stretch type: {}", stretchType.getName());
        return stretchTypeService.save(stretchType);
    }
}

