package com.david13penalver.foss_training_api.application.usecases.analytics;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;

public interface FindPersonalRecordsByExerciseUseCase {
    Optional<PersonalRecord> execute(Integer exerciseId);
}
