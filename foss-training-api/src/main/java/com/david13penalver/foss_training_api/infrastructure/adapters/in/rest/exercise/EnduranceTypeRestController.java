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

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.DeleteEnduranceTypeUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.EnduranceTypeExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindAllEnduranceTypesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.FindEnduranceTypeByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.SaveEnduranceTypeUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/endurance-types")
@RequiredArgsConstructor
public class EnduranceTypeRestController {

    private final FindAllEnduranceTypesUseCase findAllEnduranceTypesUseCase;
    private final FindEnduranceTypeByNameUseCase findEnduranceTypeByNameUseCase;
    private final SaveEnduranceTypeUseCase saveEnduranceTypeUseCase;
    private final DeleteEnduranceTypeUseCase deleteEnduranceTypeUseCase;
    private final EnduranceTypeExistsUseCase enduranceTypeExistsUseCase;

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

    @PostMapping
    public ResponseEntity<EnduranceType> createEnduranceType(@RequestBody EnduranceType enduranceType) {
        EnduranceType savedEnduranceType = saveEnduranceTypeUseCase.execute(enduranceType);
        return ResponseEntity.ok(savedEnduranceType);
    }

    @PutMapping("/{name}")
    public ResponseEntity<EnduranceType> updateEnduranceType(@PathVariable String name, @RequestBody EnduranceType enduranceType) {
        // TODO: Set the name from the path variable to ensure we're updating the correct entity
        EnduranceType savedEnduranceType = saveEnduranceTypeUseCase.execute(enduranceType);
        return ResponseEntity.ok(savedEnduranceType);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteEnduranceType(@PathVariable String name) {
        if (!enduranceTypeExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteEnduranceTypeUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> enduranceTypeExists(@PathVariable String name) {
        boolean exists = enduranceTypeExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}
