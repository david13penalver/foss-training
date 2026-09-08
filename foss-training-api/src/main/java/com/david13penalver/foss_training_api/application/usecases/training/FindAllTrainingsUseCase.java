package com.david13penalver.foss_training_api.application.usecases.training;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface FindAllTrainingsUseCase {

    List<Training> execute();
}
