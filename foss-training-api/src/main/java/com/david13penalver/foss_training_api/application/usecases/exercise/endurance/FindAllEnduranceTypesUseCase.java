package com.david13penalver.foss_training_api.application.usecases.exercise.endurance;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

public interface FindAllEnduranceTypesUseCase {
    
    List<EnduranceType> execute();
}

