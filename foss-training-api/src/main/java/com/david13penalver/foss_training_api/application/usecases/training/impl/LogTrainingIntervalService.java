package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.LogTrainingIntervalUseCase;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogTrainingIntervalService implements LogTrainingIntervalUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer trainingId, Integer exerciseId, EnduranceInterval interval) {
        log.debug("Executing LogTrainingIntervalUseCase for training: {}, exercise: {}, interval: {}", trainingId, exerciseId, interval);
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + trainingId));
        training.logInterval(exerciseId, interval);
        return trainingRepository.save(training);
    }
}
