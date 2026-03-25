package com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.SaveEnduranceTypeUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveEnduranceTypeUseCaseImpl implements SaveEnduranceTypeUseCase {
    
    private final EnduranceTypeService enduranceTypeService;
    
    @Override
    public EnduranceType execute(EnduranceType enduranceType) {
        log.debug("Executing SaveEnduranceTypeUseCase with endurance type: {}", enduranceType.getName());
        return enduranceTypeService.save(enduranceType);
    }
}

