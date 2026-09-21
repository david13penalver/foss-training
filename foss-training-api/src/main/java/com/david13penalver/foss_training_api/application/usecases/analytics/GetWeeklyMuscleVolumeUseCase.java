package com.david13penalver.foss_training_api.application.usecases.analytics;

import java.time.LocalDate;

import com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume;

public interface GetWeeklyMuscleVolumeUseCase {

    WeeklyMuscleVolume execute(LocalDate startDate, LocalDate endDate);
}
