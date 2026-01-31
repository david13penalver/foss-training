package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MovementPatternRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementPatternServiceImpl implements MovementPatternService {
    
    private final MovementPatternRepository movementPatternRepository;
    
    @Override
    public List<MovementPattern> findAll() {
        log.debug("Finding all movement patterns");
        return movementPatternRepository.findAll();
    }
    
    @Override
    public Optional<MovementPattern> findById(String name) {
        log.debug("Finding movement pattern by name: {}", name);
        return movementPatternRepository.findById(name);
    }
    
    @Override
    public MovementPattern save(MovementPattern movementPattern) {
        log.debug("Saving movement pattern: {}", movementPattern);
        return movementPatternRepository.save(movementPattern);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting movement pattern by name: {}", name);
        if (!movementPatternRepository.existsById(name)) {
            throw new IllegalArgumentException("Movement pattern not found with name: " + name);
        }
        movementPatternRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if movement pattern exists by name: {}", name);
        return movementPatternRepository.existsById(name);
    }
}