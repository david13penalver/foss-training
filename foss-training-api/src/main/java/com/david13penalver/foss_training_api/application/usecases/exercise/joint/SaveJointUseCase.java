package com.david13penalver.foss_training_api.application.usecases.exercise.joint;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

public interface SaveJointUseCase {
    
    Joint execute(Joint joint);
}

