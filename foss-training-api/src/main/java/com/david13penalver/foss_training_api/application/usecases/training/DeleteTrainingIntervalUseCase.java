package com.david13penalver.foss_training_api.application.usecases.training;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface DeleteTrainingIntervalUseCase {

    Training execute(Integer trainingId, Integer exerciseId, Integer intervalNumber);
}
