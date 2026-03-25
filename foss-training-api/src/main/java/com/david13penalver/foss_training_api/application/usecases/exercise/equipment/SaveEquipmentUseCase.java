package com.david13penalver.foss_training_api.application.usecases.exercise.equipment;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

public interface SaveEquipmentUseCase {
    
    Equipment execute(Equipment equipment);
}

