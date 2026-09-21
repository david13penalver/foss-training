package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.DeleteTrainingIntervalUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteTrainingIntervalService implements DeleteTrainingIntervalUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer trainingId, Integer exerciseId, Integer intervalNumber) {
        log.debug("Executing DeleteTrainingIntervalUseCase for training: {}, exercise: {}, interval: {}", trainingId, exerciseId, intervalNumber);
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + trainingId));
        training.removeInterval(exerciseId, intervalNumber);
        return trainingRepository.save(training);
    }
}
