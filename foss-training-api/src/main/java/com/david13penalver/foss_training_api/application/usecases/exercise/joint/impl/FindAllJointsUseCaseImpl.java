package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindAllJointsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllJointsUseCaseImpl implements FindAllJointsUseCase {
    
    private final JointService jointService;
    
    @Override
    public List<Joint> execute() {
        log.debug("Executing FindAllJointsUseCase");
        return jointService.findAll();
    }
}

