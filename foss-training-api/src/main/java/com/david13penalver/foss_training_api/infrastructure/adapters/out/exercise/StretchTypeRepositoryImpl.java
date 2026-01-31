package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.StretchTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StretchTypeRepositoryImpl implements StretchTypeRepository {
    @Override
    public List<StretchType> findAll() {
        return List.of();
    }

    @Override
    public Optional<StretchType> findById(String name) {
        return Optional.empty();
    }

    @Override
    public StretchType save(StretchType stretchType) {
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
