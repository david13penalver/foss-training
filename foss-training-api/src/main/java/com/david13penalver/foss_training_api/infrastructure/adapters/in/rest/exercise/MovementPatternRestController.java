package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindAllMovementPatternsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindMovementPatternByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.MovementPattern;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movement-patterns")
@RequiredArgsConstructor
public class MovementPatternRestController {

    private final FindAllMovementPatternsUseCase findAllMovementPatternsUseCase;
    private final FindMovementPatternByNameUseCase findMovementPatternByNameUseCase;

    @GetMapping
    public ResponseEntity<List<MovementPattern>> getAllMovementPatterns() {
        List<MovementPattern> movementPatterns = findAllMovementPatternsUseCase.execute();
        return ResponseEntity.ok(movementPatterns);
    }

    @GetMapping("/{name}")
    public ResponseEntity<MovementPattern> getMovementPatternByName(@PathVariable String name) {
        Optional<MovementPattern> movementPattern = findMovementPatternByNameUseCase.execute(name);
        return movementPattern.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }
}
