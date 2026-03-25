package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.SaveEquipmentUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveEquipmentUseCaseImpl implements SaveEquipmentUseCase {
    
    private final EquipmentService equipmentService;
    
    @Override
    public Equipment execute(Equipment equipment) {
        log.debug("Executing SaveEquipmentUseCase with equipment: {}", equipment.getName());
        return equipmentService.save(equipment);
    }
}

