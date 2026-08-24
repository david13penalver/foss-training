package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovementPatternServiceImpl implements MovementPatternService {
    
    @Override
    public List<MovementPattern> findAll() {
        log.debug("Finding all movement patterns");
        return List.of(MovementPattern.values());
    }
    
    @Override
    public Optional<MovementPattern> findById(String name) {
        log.debug("Finding movement pattern by name: {}", name);
        return Arrays.stream(MovementPattern.values())
                .filter(pattern -> pattern.name().equalsIgnoreCase(name))
                .findFirst();
    }
}