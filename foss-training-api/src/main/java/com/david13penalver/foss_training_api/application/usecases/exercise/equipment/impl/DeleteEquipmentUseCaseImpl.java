package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.DeleteEquipmentUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteEquipmentUseCaseImpl implements DeleteEquipmentUseCase {
    
    private final EquipmentService equipmentService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteEquipmentUseCase with name: {}", name);
        equipmentService.deleteById(name);
    }
}

