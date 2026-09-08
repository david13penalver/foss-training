package com.david13penalver.foss_training_api.application.usecases.training;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface FindTrainingByIdUseCase {

    Optional<Training> execute(Integer id);
}
