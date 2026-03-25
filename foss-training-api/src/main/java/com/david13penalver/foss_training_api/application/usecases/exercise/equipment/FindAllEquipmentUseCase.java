package com.david13penalver.foss_training_api.application.usecases.exercise.equipment;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

public interface FindAllEquipmentUseCase {
    
    List<Equipment> execute();
}

