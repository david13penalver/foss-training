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

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.DeleteExerciseUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.ExerciseExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindAllExercisesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindExerciseByIdUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.SaveExerciseUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseRestController {

    private final FindAllExercisesUseCase findAllExercisesUseCase;
    private final FindExerciseByIdUseCase findExerciseByIdUseCase;
    private final SaveExerciseUseCase saveExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;
    private final ExerciseExistsUseCase exerciseExistsUseCase;

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = findAllExercisesUseCase.execute();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Integer id) {
        Optional<Exercise> exercise = findExerciseByIdUseCase.execute(id);
        return exercise.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise) {
        Exercise savedExercise = saveExerciseUseCase.execute(exercise);
        return ResponseEntity.ok(savedExercise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Integer id, @RequestBody Exercise exercise) {
        // Set the ID from the path variable to ensure we're updating the correct entity
        exercise.setId(id);
        Exercise savedExercise = saveExerciseUseCase.execute(exercise);
        return ResponseEntity.ok(savedExercise);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Integer id) {
        if (!exerciseExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        deleteExerciseUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exerciseExists(@PathVariable Integer id) {
        boolean exists = exerciseExistsUseCase.execute(id);
        return ResponseEntity.ok(exists);
    }
}
