package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JointServiceImpl implements JointService {
    
    @Override
    public List<Joint> findAll() {
        log.debug("Finding all joints");
        return List.of(Joint.values());
    }
    
    @Override
    public Optional<Joint> findById(String name) {
        log.debug("Finding joint by name: {}", name);
        return Arrays.stream(Joint.values())
                .filter(joint -> joint.name().equalsIgnoreCase(name))
                .findFirst();
    }
}