package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindAllStretchTypesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.FindStretchTypeByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stretch-types")
@RequiredArgsConstructor
public class StretchTypeRestController {

    private final FindAllStretchTypesUseCase findAllStretchTypesUseCase;
    private final FindStretchTypeByNameUseCase findStretchTypeByNameUseCase;

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
}
