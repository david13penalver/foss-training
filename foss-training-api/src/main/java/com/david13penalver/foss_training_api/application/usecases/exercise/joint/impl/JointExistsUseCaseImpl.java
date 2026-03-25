package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.JointExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JointExistsUseCaseImpl implements JointExistsUseCase {
    
    private final JointService jointService;
    
    @Override
    public boolean execute(String name) {
        log.debug("Executing JointExistsUseCase with name: {}", name);
        return jointService.existsById(name);
    }
}

