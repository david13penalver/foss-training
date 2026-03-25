package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.FindEquipmentByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindEquipmentByNameUseCaseImpl implements FindEquipmentByNameUseCase {
    
    private final EquipmentService equipmentService;
    
    @Override
    public Optional<Equipment> execute(String name) {
        log.debug("Executing FindEquipmentByNameUseCase with name: {}", name);
        return equipmentService.findById(name);
    }
}

