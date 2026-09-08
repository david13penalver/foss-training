package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.StartTrainingUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartTrainingService implements StartTrainingUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer id) {
        log.debug("Executing StartTrainingUseCase with id: {}", id);
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + id));
        training.start();
        return trainingRepository.save(training);
    }
}
