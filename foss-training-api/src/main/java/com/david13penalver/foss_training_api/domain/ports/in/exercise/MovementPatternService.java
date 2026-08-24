package com.david13penalver.foss_training_api.domain.ports.in.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

public interface MovementPatternService {
    
    List<MovementPattern> findAll();
    
    Optional<MovementPattern> findById(String name);
}