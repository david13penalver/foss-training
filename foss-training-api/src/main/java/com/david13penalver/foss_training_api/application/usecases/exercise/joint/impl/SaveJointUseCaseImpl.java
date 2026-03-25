package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.SaveJointUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveJointUseCaseImpl implements SaveJointUseCase {
    
    private final JointService jointService;
    
    @Override
    public Joint execute(Joint joint) {
        log.debug("Executing SaveJointUseCase with joint: {}", joint);
        return jointService.save(joint);
    }
}

