package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.EnduranceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnduranceTypeRepositoryImpl implements EnduranceTypeRepository {
    @Override
    public List<EnduranceType> findAll() {
        return List.of();
    }

    @Override
    public Optional<EnduranceType> findById(String name) {
        return Optional.empty();
    }

    @Override
    public EnduranceType save(EnduranceType enduranceType) {
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
