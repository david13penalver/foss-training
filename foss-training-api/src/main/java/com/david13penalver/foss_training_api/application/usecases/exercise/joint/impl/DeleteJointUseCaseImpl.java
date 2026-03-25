package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.DeleteJointUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteJointUseCaseImpl implements DeleteJointUseCase {
    
    private final JointService jointService;
    
    @Override
    public void execute(String name) {
        log.debug("Executing DeleteJointUseCase with name: {}", name);
        jointService.deleteById(name);
    }
}

