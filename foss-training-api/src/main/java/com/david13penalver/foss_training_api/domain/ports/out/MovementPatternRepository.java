package com.david13penalver.foss_training_api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

public interface MovementPatternRepository {
    
    List<MovementPattern> findAll();
    
    Optional<MovementPattern> findById(String name);
    
    MovementPattern save(MovementPattern movementPattern);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}