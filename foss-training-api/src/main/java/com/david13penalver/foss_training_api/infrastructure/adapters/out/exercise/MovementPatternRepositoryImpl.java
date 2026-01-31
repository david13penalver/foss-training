package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MovementPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MovementPatternRepositoryImpl implements MovementPatternRepository {
    @Override
    public List<MovementPattern> findAll() {
        return List.of();
    }

    @Override
    public Optional<MovementPattern> findById(String name) {
        return Optional.empty();
    }

    @Override
    public MovementPattern save(MovementPattern movementPattern) {
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
