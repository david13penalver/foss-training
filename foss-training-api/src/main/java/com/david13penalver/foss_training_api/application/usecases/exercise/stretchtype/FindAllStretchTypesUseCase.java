package com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

public interface FindAllStretchTypesUseCase {
    
    List<StretchType> execute();
}

