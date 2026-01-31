package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MuscleGroupRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MuscleGroupServiceImpl implements MuscleGroupService {
    
    private final MuscleGroupRepository muscleGroupRepository;
    
    @Override
    public List<MuscleGroup> findAll() {
        log.debug("Finding all muscle groups");
        return muscleGroupRepository.findAll();
    }
    
    @Override
    public Optional<MuscleGroup> findById(String name) {
        log.debug("Finding muscle group by name: {}", name);
        return muscleGroupRepository.findById(name);
    }
    
    @Override
    public MuscleGroup save(MuscleGroup muscleGroup) {
        log.debug("Saving muscle group: {}", muscleGroup.getName());
        return muscleGroupRepository.save(muscleGroup);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting muscle group by name: {}", name);
        if (!muscleGroupRepository.existsById(name)) {
            throw new IllegalArgumentException("Muscle group not found with name: " + name);
        }
        muscleGroupRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if muscle group exists by name: {}", name);
        return muscleGroupRepository.existsById(name);
    }
}