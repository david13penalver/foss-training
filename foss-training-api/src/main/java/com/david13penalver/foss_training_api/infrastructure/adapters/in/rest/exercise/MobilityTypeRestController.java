package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.DeleteMobilityTypeUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindAllMobilityTypesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindMobilityTypeByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.MobilityTypeExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.SaveMobilityTypeUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mobility-types")
@RequiredArgsConstructor
public class MobilityTypeRestController {

    private final FindAllMobilityTypesUseCase findAllMobilityTypesUseCase;
    private final FindMobilityTypeByNameUseCase findMobilityTypeByNameUseCase;
    private final SaveMobilityTypeUseCase saveMobilityTypeUseCase;
    private final DeleteMobilityTypeUseCase deleteMobilityTypeUseCase;
    private final MobilityTypeExistsUseCase mobilityTypeExistsUseCase;

    @GetMapping
    public ResponseEntity<List<MobilityType>> getAllMobilityTypes() {
        List<MobilityType> mobilityTypes = findAllMobilityTypesUseCase.execute();
        return ResponseEntity.ok(mobilityTypes);
    }

    @GetMapping("/{name}")
    public ResponseEntity<MobilityType> getMobilityTypeByName(@PathVariable String name) {
        Optional<MobilityType> mobilityType = findMobilityTypeByNameUseCase.execute(name);
        return mobilityType.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MobilityType> createMobilityType(@RequestBody MobilityType mobilityType) {
        MobilityType savedMobilityType = saveMobilityTypeUseCase.execute(mobilityType);
        return ResponseEntity.ok(savedMobilityType);
    }

    @PutMapping("/{name}")
    public ResponseEntity<MobilityType> updateMobilityType(@PathVariable String name, @RequestBody MobilityType mobilityType) {
        // TODO: Set the name from the path variable to ensure we're updating the correct entity
        MobilityType savedMobilityType = saveMobilityTypeUseCase.execute(mobilityType);
        return ResponseEntity.ok(savedMobilityType);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteMobilityType(@PathVariable String name) {
        if (!mobilityTypeExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteMobilityTypeUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> mobilityTypeExists(@PathVariable String name) {
        boolean exists = mobilityTypeExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}
