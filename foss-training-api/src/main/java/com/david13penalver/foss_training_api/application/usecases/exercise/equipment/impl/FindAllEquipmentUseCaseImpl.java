package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.FindAllEquipmentUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllEquipmentUseCaseImpl implements FindAllEquipmentUseCase {
    
    private final EquipmentService equipmentService;
    
    @Override
    public List<Equipment> execute() {
        log.debug("Executing FindAllEquipmentUseCase");
        return equipmentService.findAll();
    }
}

