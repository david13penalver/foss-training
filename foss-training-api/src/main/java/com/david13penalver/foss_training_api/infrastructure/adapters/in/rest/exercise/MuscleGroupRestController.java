package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindAllMuscleGroupsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindMuscleGroupByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/muscle-groups")
@RequiredArgsConstructor
public class MuscleGroupRestController {

    private final FindAllMuscleGroupsUseCase findAllMuscleGroupsUseCase;
    private final FindMuscleGroupByNameUseCase findMuscleGroupByNameUseCase;

    @GetMapping
    public ResponseEntity<List<MuscleGroup>> getAllMuscleGroups() {
        List<MuscleGroup> muscleGroups = findAllMuscleGroupsUseCase.execute();
        return ResponseEntity.ok(muscleGroups);
    }

    @GetMapping("/{name}")
    public ResponseEntity<MuscleGroup> getMuscleGroupByName(@PathVariable String name) {
        Optional<MuscleGroup> muscleGroup = findMuscleGroupByNameUseCase.execute(name);
        return muscleGroup.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
}
