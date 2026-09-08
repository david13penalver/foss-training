package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.DeleteTrainingUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteTrainingService implements DeleteTrainingUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public void execute(Integer id) {
        log.debug("Executing DeleteTrainingUseCase with id: {}", id);
        if (!trainingRepository.existsById(id)) {
            throw new IllegalArgumentException("Training not found with id: " + id);
        }
        trainingRepository.deleteById(id);
    }
}
