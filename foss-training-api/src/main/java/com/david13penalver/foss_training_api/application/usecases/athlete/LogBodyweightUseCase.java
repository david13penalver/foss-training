package com.david13penalver.foss_training_api.application.usecases.athlete;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;

public interface LogBodyweightUseCase {

    BodyweightEntry execute(BodyweightEntry entry);
}
