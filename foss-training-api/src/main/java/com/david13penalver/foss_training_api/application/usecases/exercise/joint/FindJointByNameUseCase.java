package com.david13penalver.foss_training_api.application.usecases.exercise.joint;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

public interface FindJointByNameUseCase {
    
    Optional<Joint> execute(String name);
}

