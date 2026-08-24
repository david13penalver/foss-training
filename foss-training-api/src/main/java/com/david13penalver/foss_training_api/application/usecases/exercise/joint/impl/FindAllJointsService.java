package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindAllJointsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindAllJointsService implements FindAllJointsUseCase {
    
    @Override
    public List<Joint> execute() {
        log.debug("Executing FindAllJointsUseCase");
        return List.of(Joint.values());
    }
}
