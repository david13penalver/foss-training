package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseServiceImpl implements ExerciseService {
    
    private final ExerciseRepository exerciseRepository;
    
    @Override
    public List<Exercise> findAll() {
        log.debug("Finding all exercises");
        return exerciseRepository.findAll();
    }
    
    @Override
    public Optional<Exercise> findById(Integer id) {
        log.debug("Finding exercise by id: {}", id);
        return exerciseRepository.findById(id);
    }
    
    @Override
    public Exercise save(Exercise exercise) {
        log.debug("Saving exercise: {}", exercise.getName());
        return exerciseRepository.save(exercise);
    }
    
    @Override
    public void deleteById(Integer id) {
        log.debug("Deleting exercise by id: {}", id);
        if (!exerciseRepository.existsById(id)) {
            throw new IllegalArgumentException("Exercise not found with id: " + id);
        }
        exerciseRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Integer id) {
        log.debug("Checking if exercise exists by id: {}", id);
        return exerciseRepository.existsById(id);
    }
}