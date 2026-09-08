package com.david13penalver.foss_training_api.application.usecases.program.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.FindAllTrainingProgramsUseCase;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindAllTrainingProgramsService implements FindAllTrainingProgramsUseCase {

    private final TrainingProgramRepository trainingProgramRepository;

    @Override
    public List<TrainingProgram> execute() {
        log.debug("Executing FindAllTrainingProgramsUseCase");
        return trainingProgramRepository.findAll();
    }
}
