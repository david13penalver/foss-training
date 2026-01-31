package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExerciseRepositoryImpl implements ExerciseRepository {
    @Override
    public List<Exercise> findAll() {
        return List.of();
    }

    @Override
    public Optional<Exercise> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Exercise save(Exercise exercise) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public boolean existsById(Integer id) {
        return false;
    }
}
