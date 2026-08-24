package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MobilityTypeServiceImpl implements MobilityTypeService {
    
    @Override
    public List<MobilityType> findAll() {
        log.debug("Finding all mobility types");
        return List.of(MobilityType.values());
    }
    
    @Override
    public Optional<MobilityType> findById(String name) {
        log.debug("Finding mobility type by name: {}", name);
        return Arrays.stream(MobilityType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }
}