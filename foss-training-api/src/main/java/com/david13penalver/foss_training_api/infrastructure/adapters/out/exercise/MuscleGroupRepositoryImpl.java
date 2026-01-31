package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MuscleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MuscleGroupRepositoryImpl implements MuscleGroupRepository {
    @Override
    public List<MuscleGroup> findAll() {
        return List.of();
    }

    @Override
    public Optional<MuscleGroup> findById(String name) {
        return Optional.empty();
    }

    @Override
    public MuscleGroup save(MuscleGroup muscleGroup) {
        return null;
    }

    @Override
    public void deleteById(String name) {

    }

    @Override
    public boolean existsById(String name) {
        return false;
    }
}
