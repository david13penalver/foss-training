package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StretchTypeServiceImpl implements StretchTypeService {
    
    @Override
    public List<StretchType> findAll() {
        log.debug("Finding all stretch types");
        return List.of(StretchType.values());
    }
    
    @Override
    public Optional<StretchType> findById(String name) {
        log.debug("Finding stretch type by name: {}", name);
        return Arrays.stream(StretchType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }
}