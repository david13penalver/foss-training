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

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.DeleteMovementPatternUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindAllMovementPatternsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.FindMovementPatternByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.MovementPatternExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.SaveMovementPatternUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movement-patterns")
@RequiredArgsConstructor
public class MovementPatternRestController {

    private final FindAllMovementPatternsUseCase findAllMovementPatternsUseCase;
    private final FindMovementPatternByNameUseCase findMovementPatternByNameUseCase;
    private final SaveMovementPatternUseCase saveMovementPatternUseCase;
    private final DeleteMovementPatternUseCase deleteMovementPatternUseCase;
    private final MovementPatternExistsUseCase movementPatternExistsUseCase;

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

    @PostMapping
    public ResponseEntity<MovementPattern> createMovementPattern(@RequestBody MovementPattern movementPattern) {
        MovementPattern savedMovementPattern = saveMovementPatternUseCase.execute(movementPattern);
        return ResponseEntity.ok(savedMovementPattern);
    }

    @PutMapping("/{name}")
    public ResponseEntity<MovementPattern> updateMovementPattern(@PathVariable String name, @RequestBody MovementPattern movementPattern) {
        // TODO: Set the name from the path variable to ensure we're updating the correct entity
        MovementPattern savedMovementPattern = saveMovementPatternUseCase.execute(movementPattern);
        return ResponseEntity.ok(savedMovementPattern);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteMovementPattern(@PathVariable String name) {
        if (!movementPatternExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteMovementPatternUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> movementPatternExists(@PathVariable String name) {
        boolean exists = movementPatternExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}
