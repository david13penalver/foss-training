package com.david13penalver.foss_training_api.application.usecases.analytics;

import java.time.LocalDate;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;

public interface GetExerciseProgressionUseCase {

    Optional<ExerciseProgression> execute(
            Integer exerciseId,
            LocalDate startDate,
            LocalDate endDate,
            OneRepMaxFormula formula);
}
