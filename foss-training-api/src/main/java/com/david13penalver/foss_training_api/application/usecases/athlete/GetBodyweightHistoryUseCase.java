package com.david13penalver.foss_training_api.application.usecases.athlete;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;

public interface GetBodyweightHistoryUseCase {

    List<BodyweightEntry> execute();
}
