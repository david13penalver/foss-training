package com.david13penalver.foss_training_api.domain.ports.out.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

public interface ExerciseRepository {
    
    List<Exercise> findAll();
    
    Optional<Exercise> findById(Integer id);
    
    Exercise save(Exercise exercise);
    
    void deleteById(Integer id);
    
    boolean existsById(Integer id);
}