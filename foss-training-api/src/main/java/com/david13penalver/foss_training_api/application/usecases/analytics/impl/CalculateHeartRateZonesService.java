package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.CalculateHeartRateZonesUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZoneCalculator;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZones;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculateHeartRateZonesService implements CalculateHeartRateZonesUseCase {

    @Override
    public HeartRateZones execute(Integer maxHr, Integer restingHr, Integer age) {
        log.debug("Executing CalculateHeartRateZonesUseCase with maxHr: {}, restingHr: {}, age: {}", maxHr, restingHr, age);
        return HeartRateZoneCalculator.compute(maxHr, restingHr, age);
    }
}
