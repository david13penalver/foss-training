package com.david13penalver.foss_training_api.domain.ports.out.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

public interface MobilityTypeRepository {
    
    List<MobilityType> findAll();
    
    Optional<MobilityType> findById(String name);
    
    MobilityType save(MobilityType mobilityType);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}