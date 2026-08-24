package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MuscleGroupServiceImpl implements MuscleGroupService {
    
    @Override
    public List<MuscleGroup> findAll() {
        log.debug("Finding all muscle groups");
        return List.of(MuscleGroup.values());
    }
    
    @Override
    public Optional<MuscleGroup> findById(String name) {
        log.debug("Finding muscle group by name: {}", name);
        return Arrays.stream(MuscleGroup.values())
                .filter(group -> group.name().equalsIgnoreCase(name))
                .findFirst();
    }
}