package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MobilityTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MobilityTypeRepositoryImpl implements MobilityTypeRepository {
    @Override
    public List<MobilityType> findAll() {
        return List.of();
    }

    @Override
    public Optional<MobilityType> findById(String name) {
        return Optional.empty();
    }

    @Override
    public MobilityType save(MobilityType mobilityType) {
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
