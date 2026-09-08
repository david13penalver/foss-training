package com.david13penalver.foss_training_api.application.usecases.program.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.SaveTrainingProgramUseCase;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveTrainingProgramService implements SaveTrainingProgramUseCase {

    private final TrainingProgramRepository trainingProgramRepository;

    @Override
    public TrainingProgram execute(TrainingProgram program) {
        log.debug("Executing SaveTrainingProgramUseCase for program: {}", program.getName());
        program.validate();
        return trainingProgramRepository.save(program);
    }
}
