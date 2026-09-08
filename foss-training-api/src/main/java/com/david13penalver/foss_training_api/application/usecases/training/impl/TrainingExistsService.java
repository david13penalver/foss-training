package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.TrainingExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExistsService implements TrainingExistsUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public boolean execute(Integer id) {
        log.debug("Executing TrainingExistsUseCase with id: {}", id);
        return trainingRepository.existsById(id);
    }
}
