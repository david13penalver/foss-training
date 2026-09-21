package com.david13penalver.foss_training_api.application.usecases.analytics;

import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZones;

public interface CalculateHeartRateZonesUseCase {

    HeartRateZones execute(Integer maxHr, Integer restingHr, Integer age);
}
