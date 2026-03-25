package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindJointByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindJointByNameUseCaseImpl implements FindJointByNameUseCase {
    
    private final JointService jointService;
    
    @Override
    public Optional<Joint> execute(String name) {
        log.debug("Executing FindJointByNameUseCase with name: {}", name);
        return jointService.findById(name);
    }
}

