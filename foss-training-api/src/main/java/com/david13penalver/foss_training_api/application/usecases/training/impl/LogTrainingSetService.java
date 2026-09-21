package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.LogTrainingSetUseCase;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogTrainingSetService implements LogTrainingSetUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer trainingId, Integer exerciseId, ResistanceSet set) {
        log.debug("Executing LogTrainingSetUseCase for training: {}, exercise: {}, set: {}", trainingId, exerciseId, set);
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with id: " + trainingId));
        training.logSet(exerciseId, set);
        return trainingRepository.save(training);
    }
}
