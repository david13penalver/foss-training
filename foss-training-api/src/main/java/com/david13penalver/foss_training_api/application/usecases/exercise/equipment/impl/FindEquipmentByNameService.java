package com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.FindEquipmentByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindEquipmentByNameService implements FindEquipmentByNameUseCase {
    
    @Override
    public Optional<Equipment> execute(String name) {
        log.debug("Executing FindEquipmentByNameUseCase with name: {}", name);
        return Arrays.stream(Equipment.values())
                .filter(equipment -> equipment.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
