package com.david13penalver.foss_training_api.application.usecases.program;

import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherence;

public interface GetProgramAdherenceUseCase {

    ProgramAdherence execute(Integer programId);
}
