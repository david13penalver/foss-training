package com.david13penalver.foss_training_api.application.usecases.program;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;

public interface CloneTrainingProgramUseCase {

    TrainingProgram execute(Integer programId, String newName);
}
