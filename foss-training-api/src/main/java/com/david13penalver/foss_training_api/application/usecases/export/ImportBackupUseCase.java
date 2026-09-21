package com.david13penalver.foss_training_api.application.usecases.export;

import com.david13penalver.foss_training_api.domain.model.export.FullBackupData;
import com.david13penalver.foss_training_api.domain.model.export.ImportSummary;

public interface ImportBackupUseCase {

    ImportSummary execute(FullBackupData backupData);
}
