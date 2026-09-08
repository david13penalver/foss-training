package com.david13penalver.foss_training_api.application.usecases.program.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.FindTrainingProgramByIdUseCase;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindTrainingProgramByIdService implements FindTrainingProgramByIdUseCase {

    private final TrainingProgramRepository trainingProgramRepository;

    @Override
    public Optional<TrainingProgram> execute(Integer id) {
        log.debug("Executing FindTrainingProgramByIdUseCase with id: {}", id);
        return trainingProgramRepository.findById(id);
    }
}
