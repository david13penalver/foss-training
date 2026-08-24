package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {
    
    @Override
    public List<Equipment> findAll() {
        log.debug("Finding all equipment");
        return List.of(Equipment.values());
    }
    
    @Override
    public Optional<Equipment> findById(String name) {
        log.debug("Finding equipment by name: {}", name);
        return Arrays.stream(Equipment.values())
                .filter(equipment -> equipment.name().equalsIgnoreCase(name))
                .findFirst();
    }
}