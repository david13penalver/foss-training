package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.CalculateOneRepMaxUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.common.Weight;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculateOneRepMaxService implements CalculateOneRepMaxUseCase {

    @Override
    public OneRepMaxEstimate execute(Weight weight, int reps, OneRepMaxFormula formula) {
        log.debug("Executing CalculateOneRepMaxUseCase with weight: {}, reps: {}, formula: {}", weight, reps, formula);
        return OneRepMaxEstimate.calculate(weight, reps, formula);
    }
}
