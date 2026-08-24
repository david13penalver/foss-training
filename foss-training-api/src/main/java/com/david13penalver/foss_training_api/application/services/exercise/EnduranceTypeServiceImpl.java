package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnduranceTypeServiceImpl implements EnduranceTypeService {
    
    @Override
    public List<EnduranceType> findAll() {
        log.debug("Finding all endurance types");
        return List.of(EnduranceType.values());
    }
    
    @Override
    public Optional<EnduranceType> findById(String name) {
        log.debug("Finding endurance type by name: {}", name);
        return Arrays.stream(EnduranceType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }
}