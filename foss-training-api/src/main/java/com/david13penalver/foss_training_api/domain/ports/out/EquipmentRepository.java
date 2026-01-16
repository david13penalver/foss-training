package com.david13penalver.foss_training_api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

public interface EquipmentRepository {
    
    List<Equipment> findAll();
    
    Optional<Equipment> findById(String name);
    
    Equipment save(Equipment equipment);
    
    void deleteById(String name);
    
    boolean existsById(String name);
}