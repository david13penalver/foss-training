package com.david13penalver.foss_training_api.application.usecases.program;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;

public interface FindTrainingProgramByIdUseCase {
    Optional<TrainingProgram> execute(Integer id);
}
