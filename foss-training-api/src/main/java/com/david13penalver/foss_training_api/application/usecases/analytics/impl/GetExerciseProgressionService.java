package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.GetExerciseProgressionUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgressionCalculator;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetExerciseProgressionService implements GetExerciseProgressionUseCase {

    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public Optional<ExerciseProgression> execute(
            Integer exerciseId, LocalDate startDate, LocalDate endDate, OneRepMaxFormula formula) {

        log.debug("Calculating exercise strength progression for exerciseId: {}, startDate: {}, endDate: {}, formula: {}",
                exerciseId, startDate, endDate, formula);

        if (exerciseId == null) {
            return Optional.empty();
        }

        Optional<Exercise> exerciseOpt = exerciseRepository.findById(exerciseId);
        if (exerciseOpt.isEmpty()) {
            return Optional.empty();
        }

        List<Training> trainings = trainingRepository.findAll();
        OneRepMaxFormula effectiveFormula = (formula != null) ? formula : OneRepMaxFormula.EPLEY;

        return Optional.of(ExerciseProgressionCalculator.compute(
                exerciseOpt.get(), trainings, startDate, endDate, effectiveFormula));
    }
}
