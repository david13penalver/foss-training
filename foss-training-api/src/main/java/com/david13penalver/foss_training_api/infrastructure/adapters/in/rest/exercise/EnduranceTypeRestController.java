package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindAllEnduranceTypesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindEnduranceTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/endurance-types")
@RequiredArgsConstructor
public class EnduranceTypeRestController {

    private final FindAllEnduranceTypesUseCase findAllEnduranceTypesUseCase;
    private final FindEnduranceTypeByNameUseCase findEnduranceTypeByNameUseCase;

    @GetMapping
    public ResponseEntity<List<EnduranceType>> getAllEnduranceTypes() {
        List<EnduranceType> enduranceTypes = findAllEnduranceTypesUseCase.execute();
        return ResponseEntity.ok(enduranceTypes);
    }

    @GetMapping("/{name}")
    public ResponseEntity<EnduranceType> getEnduranceTypeByName(@PathVariable String name) {
        Optional<EnduranceType> enduranceType = findEnduranceTypeByNameUseCase.execute(name);
        return enduranceType.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
}
