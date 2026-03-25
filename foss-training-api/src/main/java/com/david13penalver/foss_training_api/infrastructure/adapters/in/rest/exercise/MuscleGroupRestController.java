package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.DeleteMuscleGroupUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindAllMuscleGroupsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.FindMuscleGroupByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.MuscleGroupExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.SaveMuscleGroupUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/muscle-groups")
@RequiredArgsConstructor
public class MuscleGroupRestController {

    private final FindAllMuscleGroupsUseCase findAllMuscleGroupsUseCase;
    private final FindMuscleGroupByNameUseCase findMuscleGroupByNameUseCase;
    private final SaveMuscleGroupUseCase saveMuscleGroupUseCase;
    private final DeleteMuscleGroupUseCase deleteMuscleGroupUseCase;
    private final MuscleGroupExistsUseCase muscleGroupExistsUseCase;

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

    @PostMapping
    public ResponseEntity<MuscleGroup> createMuscleGroup(@RequestBody MuscleGroup muscleGroup) {
        MuscleGroup savedMuscleGroup = saveMuscleGroupUseCase.execute(muscleGroup);
        return ResponseEntity.ok(savedMuscleGroup);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteMuscleGroup(@PathVariable String name) {
        if (!muscleGroupExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteMuscleGroupUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> muscleGroupExists(@PathVariable String name) {
        boolean exists = muscleGroupExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}

