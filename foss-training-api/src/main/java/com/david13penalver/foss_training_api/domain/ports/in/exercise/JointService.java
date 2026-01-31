package com.david13penalver.foss_training_api.domain.ports.in.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

public interface JointService {
    
    List<Joint> findAll();
    
    Optional<Joint> findById(String name);
    
    Joint save(Joint joint);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}