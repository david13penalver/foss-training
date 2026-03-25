package com.david13penalver.foss_training_api.application.usecases.exercise.joint;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

public interface FindAllJointsUseCase {
    
    List<Joint> execute();
}

