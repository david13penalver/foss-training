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

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.DeleteJointUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindAllJointsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.FindJointByNameUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.JointExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.SaveJointUseCase;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/joints")
@RequiredArgsConstructor
public class JointRestController {

    private final FindAllJointsUseCase findAllJointsUseCase;
    private final FindJointByNameUseCase findJointByNameUseCase;
    private final SaveJointUseCase saveJointUseCase;
    private final DeleteJointUseCase deleteJointUseCase;
    private final JointExistsUseCase jointExistsUseCase;

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

    @PostMapping
    public ResponseEntity<Joint> createJoint(@RequestBody Joint joint) {
        Joint savedJoint = saveJointUseCase.execute(joint);
        return ResponseEntity.ok(savedJoint);
    }

    @PutMapping("/{name}")
    public ResponseEntity<Joint> updateJoint(@PathVariable String name, @RequestBody Joint joint) {
        // TODO: Set the name from the path variable to ensure we're updating the correct entity
        Joint savedJoint = saveJointUseCase.execute(joint);
        return ResponseEntity.ok(savedJoint);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteJoint(@PathVariable String name) {
        if (!jointExistsUseCase.execute(name)) {
            return ResponseEntity.notFound().build();
        }
        deleteJointUseCase.execute(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<Boolean> jointExists(@PathVariable String name) {
        boolean exists = jointExistsUseCase.execute(name);
        return ResponseEntity.ok(exists);
    }
}
