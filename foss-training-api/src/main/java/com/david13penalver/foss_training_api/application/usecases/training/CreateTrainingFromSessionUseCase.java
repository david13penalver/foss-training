package com.david13penalver.foss_training_api.application.usecases.training;

import java.time.LocalDate;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface CreateTrainingFromSessionUseCase {

    Training execute(Integer sessionId, LocalDate date, String customName);
}
