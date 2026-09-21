package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.DeleteTrainingSetUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteTrainingSetService implements DeleteTrainingSetUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer trainingId, Integer exerciseId, Integer setNumber) {
        log.debug("Executing DeleteTrainingSetUseCase for training: {}, exercise: {}, set: {}", trainingId, exerciseId, setNumber);
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + trainingId));
        training.removeSet(exerciseId, setNumber);
        return trainingRepository.save(training);
    }
}
