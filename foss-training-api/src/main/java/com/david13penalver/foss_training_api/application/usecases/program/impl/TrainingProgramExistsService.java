package com.david13penalver.foss_training_api.application.usecases.program.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.TrainingProgramExistsUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingProgramExistsService implements TrainingProgramExistsUseCase {

    private final TrainingProgramRepository trainingProgramRepository;

    @Override
    public boolean execute(Integer id) {
        log.debug("Executing TrainingProgramExistsUseCase with id: {}", id);
        return trainingProgramRepository.existsById(id);
    }
}
