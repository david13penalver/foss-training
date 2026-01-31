package com.david13penalver.foss_training_api.domain.ports.out.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

public interface EnduranceTypeRepository {
    
    List<EnduranceType> findAll();
    
    Optional<EnduranceType> findById(String name);
    
    EnduranceType save(EnduranceType enduranceType);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}