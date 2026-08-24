package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.FindAllEquipmentUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllEquipmentService implements FindAllEquipmentUseCase {
    
    @Override
    public List<Equipment> execute() {
        log.debug("Executing FindAllEquipmentUseCase");
        return List.of(Equipment.values());
    }
}
