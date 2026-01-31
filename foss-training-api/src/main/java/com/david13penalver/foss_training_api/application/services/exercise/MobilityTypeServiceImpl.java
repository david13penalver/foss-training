package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MobilityTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MobilityTypeServiceImpl implements MobilityTypeService {
    
    private final MobilityTypeRepository mobilityTypeRepository;
    
    @Override
    public List<MobilityType> findAll() {
        log.debug("Finding all mobility types");
        return mobilityTypeRepository.findAll();
    }
    
    @Override
    public Optional<MobilityType> findById(String name) {
        log.debug("Finding mobility type by name: {}", name);
        return mobilityTypeRepository.findById(name);
    }
    
    @Override
    public MobilityType save(MobilityType mobilityType) {
        log.debug("Saving mobility type: {}", mobilityType.getName());
        return mobilityTypeRepository.save(mobilityType);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting mobility type by name: {}", name);
        if (!mobilityTypeRepository.existsById(name)) {
            throw new IllegalArgumentException("Mobility type not found with name: " + name);
        }
        mobilityTypeRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if mobility type exists by name: {}", name);
        return mobilityTypeRepository.existsById(name);
    }
}