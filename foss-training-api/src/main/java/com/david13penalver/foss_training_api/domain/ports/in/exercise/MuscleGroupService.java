package com.david13penalver.foss_training_api.domain.ports.in.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

public interface MuscleGroupService {
    
    List<MuscleGroup> findAll();
    
    Optional<MuscleGroup> findById(String name);
    
    MuscleGroup save(MuscleGroup muscleGroup);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}