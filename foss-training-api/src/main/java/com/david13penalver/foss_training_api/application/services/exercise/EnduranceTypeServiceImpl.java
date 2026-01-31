package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.EnduranceTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnduranceTypeServiceImpl implements EnduranceTypeService {
    
    private final EnduranceTypeRepository enduranceTypeRepository;
    
    @Override
    public List<EnduranceType> findAll() {
        log.debug("Finding all endurance types");
        return enduranceTypeRepository.findAll();
    }
    
    @Override
    public Optional<EnduranceType> findById(String name) {
        log.debug("Finding endurance type by name: {}", name);
        return enduranceTypeRepository.findById(name);
    }
    
    @Override
    public EnduranceType save(EnduranceType enduranceType) {
        log.debug("Saving endurance type: {}", enduranceType.getName());
        return enduranceTypeRepository.save(enduranceType);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting endurance type by name: {}", name);
        if (!enduranceTypeRepository.existsById(name)) {
            throw new IllegalArgumentException("Endurance type not found with name: " + name);
        }
        enduranceTypeRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if endurance type exists by name: {}", name);
        return enduranceTypeRepository.existsById(name);
    }
}