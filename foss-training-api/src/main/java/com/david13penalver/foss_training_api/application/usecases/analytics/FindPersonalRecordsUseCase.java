package com.david13penalver.foss_training_api.application.usecases.analytics;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;

public interface FindPersonalRecordsUseCase {
    List<PersonalRecord> execute();
}
