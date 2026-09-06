package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.net.URI;
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
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;

import jakarta.validation.Valid;
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
    private final ExerciseDtoMapper exerciseDtoMapper;

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDto>> getAllExercises() {
        List<Exercise> exercises = findAllExercisesUseCase.execute();
        return ResponseEntity.ok(exerciseDtoMapper.toResponseDtoList(exercises));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> getExerciseById(@PathVariable Integer id) {
        Optional<Exercise> exercise = findExerciseByIdUseCase.execute(id);
        return exercise.map(exerciseDtoMapper::toResponseDto)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDto> createExercise(@Valid @RequestBody ExerciseRequestDto requestDto) {
        Exercise exercise = exerciseDtoMapper.toEntity(requestDto);
        Exercise savedExercise = saveExerciseUseCase.execute(exercise);
        URI location = URI.create("/api/exercises/" + savedExercise.getId());
        return ResponseEntity.created(location).body(exerciseDtoMapper.toResponseDto(savedExercise));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@PathVariable Integer id, @Valid @RequestBody ExerciseRequestDto requestDto) {
        if (!exerciseExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        requestDto.setId(id);
        Exercise exercise = exerciseDtoMapper.toEntity(requestDto);
        Exercise savedExercise = saveExerciseUseCase.execute(exercise);
        return ResponseEntity.ok(exerciseDtoMapper.toResponseDto(savedExercise));
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
