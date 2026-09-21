package com.david13penalver.foss_training_api.application.usecases.export;

import com.david13penalver.foss_training_api.domain.model.export.ImportSummary;

public interface ImportWorkoutsCsvUseCase {

    ImportSummary execute(String csvContent);
}
