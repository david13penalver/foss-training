package com.david13penalver.foss_training_api.application.usecases.program.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.DeleteTrainingProgramUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteTrainingProgramService implements DeleteTrainingProgramUseCase {

    private final TrainingProgramRepository trainingProgramRepository;

    @Override
    public void execute(Integer id) {
        log.debug("Executing DeleteTrainingProgramUseCase with id: {}", id);
        if (!trainingProgramRepository.existsById(id)) {
            throw new IllegalArgumentException("Training program not found with id: " + id);
        }
        trainingProgramRepository.deleteById(id);
    }
}
