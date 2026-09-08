package com.david13penalver.foss_training_api.application.usecases.training.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.FindAllTrainingsUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindAllTrainingsService implements FindAllTrainingsUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public List<Training> execute() {
        log.debug("Executing FindAllTrainingsUseCase");
        return trainingRepository.findAll();
    }
}
