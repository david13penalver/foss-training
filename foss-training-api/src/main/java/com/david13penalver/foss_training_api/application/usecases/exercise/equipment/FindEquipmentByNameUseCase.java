package com.david13penalver.foss_training_api.application.usecases.exercise.equipment;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

public interface FindEquipmentByNameUseCase {
    
    Optional<Equipment> execute(String name);
}

