package com.david13penalver.foss_training_api.application.usecases.training.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.FindTrainingByIdUseCase;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindTrainingByIdService implements FindTrainingByIdUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public Optional<Training> execute(Integer id) {
        log.debug("Executing FindTrainingByIdUseCase with id: {}", id);
        return trainingRepository.findById(id);
    }
}
