package com.david13penalver.foss_training_api.application.usecases.training;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface DeleteTrainingSetUseCase {

    Training execute(Integer trainingId, Integer exerciseId, Integer setNumber);
}
