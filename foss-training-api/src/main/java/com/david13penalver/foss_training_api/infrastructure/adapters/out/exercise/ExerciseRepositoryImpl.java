package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private final InMemoryExerciseDao exerciseDao;

    @Override
    public List<Exercise> findAll() {
        return exerciseDao.findAll();
    }

    @Override
    public Optional<Exercise> findById(Integer id) {
        return exerciseDao.findById(id);
    }

    @Override
    public Exercise save(Exercise exercise) {
        return exerciseDao.save(exercise);
    }

    @Override
    public void deleteById(Integer id) {
        exerciseDao.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return exerciseDao.existsById(id);
    }
}