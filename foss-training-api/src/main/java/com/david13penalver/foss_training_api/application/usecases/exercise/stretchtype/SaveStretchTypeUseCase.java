package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

public interface SaveStretchTypeUseCase {
    
    StretchType execute(StretchType stretchType);
}

