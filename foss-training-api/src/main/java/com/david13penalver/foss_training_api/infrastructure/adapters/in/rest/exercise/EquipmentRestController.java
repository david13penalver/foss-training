package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.DeleteEquipmentUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.EquipmentExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.FindAllEquipmentUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.FindEquipmentByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.SaveEquipmentUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentRestController {

    private final FindAllEquipmentUseCase findAllEquipmentUseCase;
    private final FindEquipmentByNameUseCase findEquipmentByNameUseCase;
    private final SaveEquipmentUseCase saveEquipmentUseCase;
    private final DeleteEquipmentUseCase deleteEquipmentUseCase;
    private final EquipmentExistsUseCase equipmentExistsUseCase;

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        List<Equipment> equipment = findAllEquipmentUseCase.execute();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Equipment> getEquipmentByName(@PathVariable String name) {
        Optional<Equipment> equipment = findEquipmentByNameUseCase.execute(name);
        return equipment.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        Equipment savedEquipment = saveEquipmentUseCase.execute(equipment);
        return ResponseEntity.ok(savedEquipment);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable String name) {
        if (!equipmentExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteEquipmentUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> equipmentExists(@PathVariable String name) {
        boolean exists = equipmentExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}

