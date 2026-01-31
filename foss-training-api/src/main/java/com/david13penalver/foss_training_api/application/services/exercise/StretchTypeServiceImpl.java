package com.david13penalver.foss_training_api.application.services.exercise;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.StretchTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StretchTypeServiceImpl implements StretchTypeService {
    
    private final StretchTypeRepository stretchTypeRepository;
    
    @Override
    public List<StretchType> findAll() {
        log.debug("Finding all stretch types");
        return stretchTypeRepository.findAll();
    }
    
    @Override
    public Optional<StretchType> findById(String name) {
        log.debug("Finding stretch type by name: {}", name);
        return stretchTypeRepository.findById(name);
    }
    
    @Override
    public StretchType save(StretchType stretchType) {
        log.debug("Saving stretch type: {}", stretchType.getName());
        return stretchTypeRepository.save(stretchType);
    }
    
    @Override
    public void deleteById(String name) {
        log.debug("Deleting stretch type by name: {}", name);
        if (!stretchTypeRepository.existsById(name)) {
            throw new IllegalArgumentException("Stretch type not found with name: " + name);
        }
        stretchTypeRepository.deleteById(name);
    }
    
    @Override
    public boolean existsById(String name) {
        log.debug("Checking if stretch type exists by name: {}", name);
        return stretchTypeRepository.existsById(name);
    }
}