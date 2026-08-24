package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindAllMobilityTypesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.FindMobilityTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mobility-types")
@RequiredArgsConstructor
public class MobilityTypeRestController {

    private final FindAllMobilityTypesUseCase findAllMobilityTypesUseCase;
    private final FindMobilityTypeByNameUseCase findMobilityTypeByNameUseCase;

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
}
