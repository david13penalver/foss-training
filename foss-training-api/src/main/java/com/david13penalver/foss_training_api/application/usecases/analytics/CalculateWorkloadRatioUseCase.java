package com.david13penalver.foss_training_api.application.usecases.analytics;

import java.time.LocalDate;

import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio;

public interface CalculateWorkloadRatioUseCase {

    WorkloadRatio execute(LocalDate targetDate);
}
