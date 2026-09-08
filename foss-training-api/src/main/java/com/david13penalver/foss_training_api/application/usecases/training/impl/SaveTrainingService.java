package com.david13penalver.foss_training_api.application.usecases.training.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.SaveTrainingUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveTrainingService implements SaveTrainingUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Training training) {
        log.debug("Executing SaveTrainingUseCase with training: {}", training.getName());
        return trainingRepository.save(training);
    }
}
