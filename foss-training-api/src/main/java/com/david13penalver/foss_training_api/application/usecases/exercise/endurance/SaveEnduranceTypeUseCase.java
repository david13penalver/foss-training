package com.david13penalver.foss_training_api.application.usecases.exercise.endurance;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

public interface SaveEnduranceTypeUseCase {
    
    EnduranceType execute(EnduranceType enduranceType);
}

