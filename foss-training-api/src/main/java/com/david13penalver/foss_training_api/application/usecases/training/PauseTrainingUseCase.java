package com.david13penalver.foss_training_api.application.usecases.training;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface PauseTrainingUseCase {

    Training execute(Integer id);
}
