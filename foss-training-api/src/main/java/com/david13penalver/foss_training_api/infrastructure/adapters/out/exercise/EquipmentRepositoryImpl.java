package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EquipmentRepositoryImpl implements EquipmentRepository {
    @Override
    public List<Equipment> findAll() {
        return List.of();
    }

    @Override
    public Optional<Equipment> findById(String name) {
        return Optional.empty();
    }

    @Override
    public Equipment save(Equipment equipment) {
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
