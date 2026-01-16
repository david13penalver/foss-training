package com.david13penalver.foss_training_api.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

public interface StretchTypeService {
    
    List<StretchType> findAll();
    
    Optional<StretchType> findById(String name);
    
    StretchType save(StretchType stretchType);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}