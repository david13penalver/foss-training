package com.david13penalver.foss_training_api.application.usecases.export;

import com.david13penalver.foss_training_api.domain.model.export.FullBackupData;

public interface ExportBackupUseCase {

    FullBackupData execute();
}
