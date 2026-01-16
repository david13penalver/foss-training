package com.david13penalver.foss_training_api.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

public interface ExerciseService {
    
    List<Exercise> findAll();
    
    Optional<Exercise> findById(Integer id);
    
    Exercise save(Exercise exercise);
    
    void deleteById(Integer id);
    
    boolean existsById(Integer id);
}