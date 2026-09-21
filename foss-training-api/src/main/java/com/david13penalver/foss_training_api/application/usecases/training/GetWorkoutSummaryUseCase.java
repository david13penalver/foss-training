package com.david13penalver.foss_training_api.application.usecases.training;

import com.david13penalver.foss_training_api.domain.model.training.WorkoutSummary;

public interface GetWorkoutSummaryUseCase {

    WorkoutSummary execute(Integer id);
}
