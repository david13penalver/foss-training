package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.EquipmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {
    
    private final EquipmentRepository equipmentRepository;
    
    @Override
    public List<Equipment> findAll() {
        log.debug("Finding all equipment");
        return equipmentRepository.findAll();
    }
    
    @Override
    public Optional<Equipment> findById(String name) {
        log.debug("Finding equipment by name: {}", name);
        return equipmentRepository.findById(name);
    }
    
    @Override
    public Equipment save(Equipment equipment) {
        log.debug("Saving equipment: {}", equipment.getName());
        return equipmentRepository.save(equipment);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting equipment by name: {}", name);
        if (!equipmentRepository.existsById(name)) {
            throw new IllegalArgumentException("Equipment not found with name: " + name);
        }
        equipmentRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if equipment exists by name: {}", name);
        return equipmentRepository.existsById(name);
    }
}