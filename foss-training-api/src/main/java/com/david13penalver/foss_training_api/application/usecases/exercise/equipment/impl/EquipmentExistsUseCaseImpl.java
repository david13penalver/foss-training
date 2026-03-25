package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.EquipmentExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EquipmentExistsUseCaseImpl implements EquipmentExistsUseCase {
    
    private final EquipmentService equipmentService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing EquipmentExistsUseCase with name: {}", name);
        return equipmentService.existsById(name);
    }
}

