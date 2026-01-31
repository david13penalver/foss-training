package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.JointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JointServiceImpl implements JointService {
    
    private final JointRepository jointRepository;
    
    @Override
    public List<Joint> findAll() {
        log.debug("Finding all joints");
        return jointRepository.findAll();
    }
    
    @Override
    public Optional<Joint> findById(String name) {
        log.debug("Finding joint by name: {}", name);
        return jointRepository.findById(name);
    }
    
    @Override
    public Joint save(Joint joint) {
        log.debug("Saving joint: {}", joint);
        return jointRepository.save(joint);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting joint by name: {}", name);
        if (!jointRepository.existsById(name)) {
            throw new IllegalArgumentException("Joint not found with name: " + name);
        }
        jointRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if joint exists by name: {}", name);
        return jointRepository.existsById(name);
    }
}