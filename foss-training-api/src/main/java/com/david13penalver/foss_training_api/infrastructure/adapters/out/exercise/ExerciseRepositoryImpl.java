package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExerciseJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExercisePersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.SpringDataExerciseRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private final SpringDataExerciseRepository exerciseRepository;
    private final ExercisePersistenceMapper mapper;

    @Override
    public List<Exercise> findAll() {
        return exerciseRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Exercise> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return exerciseRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Exercise save(Exercise exercise) {
        ExerciseJpaEntity entity = mapper.toJpaEntity(exercise);
        ExerciseJpaEntity saved = exerciseRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            exerciseRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && exerciseRepository.existsById(id);
    }
}
