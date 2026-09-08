package com.david13penalver.foss_training_api.application.usecases.analytics;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.common.Weight;

public interface CalculateOneRepMaxUseCase {
    OneRepMaxEstimate execute(Weight weight, int reps, OneRepMaxFormula formula);
}
