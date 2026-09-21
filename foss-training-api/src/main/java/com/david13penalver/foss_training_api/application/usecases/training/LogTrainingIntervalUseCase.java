package com.david13penalver.foss_training_api.application.usecases.training;

import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface LogTrainingIntervalUseCase {

    Training execute(Integer trainingId, Integer exerciseId, EnduranceInterval interval);
}
