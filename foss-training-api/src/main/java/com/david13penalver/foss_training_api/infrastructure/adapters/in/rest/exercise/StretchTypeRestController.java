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

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.DeleteStretchTypeUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindAllStretchTypesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindStretchTypeByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.SaveStretchTypeUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.StretchTypeExistsUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stretch-types")
@RequiredArgsConstructor
public class StretchTypeRestController {

    private final FindAllStretchTypesUseCase findAllStretchTypesUseCase;
    private final FindStretchTypeByNameUseCase findStretchTypeByNameUseCase;
    private final SaveStretchTypeUseCase saveStretchTypeUseCase;
    private final DeleteStretchTypeUseCase deleteStretchTypeUseCase;
    private final StretchTypeExistsUseCase stretchTypeExistsUseCase;

    @GetMapping
    public ResponseEntity<List<StretchType>> getAllStretchTypes() {
        List<StretchType> stretchTypes = findAllStretchTypesUseCase.execute();
        return ResponseEntity.ok(stretchTypes);
    }

    @GetMapping("/{name}")
    public ResponseEntity<StretchType> getStretchTypeByName(@PathVariable String name) {
        Optional<StretchType> stretchType = findStretchTypeByNameUseCase.execute(name);
        return stretchType.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StretchType> createStretchType(@RequestBody StretchType stretchType) {
        StretchType savedStretchType = saveStretchTypeUseCase.execute(stretchType);
        return ResponseEntity.ok(savedStretchType);
    }

    @PutMapping("/{name}")
    public ResponseEntity<StretchType> updateStretchType(@PathVariable String name, @RequestBody StretchType stretchType) {
        // TODO: Set the name from the path variable to ensure we're updating the correct entity
        StretchType savedStretchType = saveStretchTypeUseCase.execute(stretchType);
        return ResponseEntity.ok(savedStretchType);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteStretchType(@PathVariable String name) {
        if (!stretchTypeExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteStretchTypeUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> stretchTypeExists(@PathVariable String name) {
        boolean exists = stretchTypeExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}
