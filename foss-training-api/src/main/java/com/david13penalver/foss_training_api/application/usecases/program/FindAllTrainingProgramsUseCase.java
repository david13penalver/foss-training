package com.david13penalver.foss_training_api.application.usecases.program;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;

public interface FindAllTrainingProgramsUseCase {
    List<TrainingProgram> execute();
}
