package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.CompleteTrainingUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteTrainingService implements CompleteTrainingUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer id) {
        log.debug("Executing CompleteTrainingUseCase with id: {}", id);
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + id));
        training.complete();
        return trainingRepository.save(training);
    }
}
