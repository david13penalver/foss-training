package com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindJointByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FindJointByNameService implements FindJointByNameUseCase {
    
    @Override
    public Optional<Joint> execute(String name) {
        log.debug("Executing FindJointByNameUseCase with name: {}", name);
        return Arrays.stream(Joint.values())
                .filter(joint -> joint.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
