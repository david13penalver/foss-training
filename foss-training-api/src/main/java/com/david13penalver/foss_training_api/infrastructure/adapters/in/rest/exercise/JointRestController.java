package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindAllJointsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindJointByNameUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/joints")
@RequiredArgsConstructor
public class JointRestController {

    private final FindAllJointsUseCase findAllJointsUseCase;
    private final FindJointByNameUseCase findJointByNameUseCase;

    @GetMapping
    public ResponseEntity<List<Joint>> getAllJoints() {
        List<Joint> joints = findAllJointsUseCase.execute();
        return ResponseEntity.ok(joints);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Joint> getJointByName(@PathVariable String name) {
        Optional<Joint> joint = findJointByNameUseCase.execute(name);
        return joint.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}
