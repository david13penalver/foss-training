package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.GetWeeklyMuscleVolumeUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.MuscleVolumeCalculator;
import com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetWeeklyMuscleVolumeService implements GetWeeklyMuscleVolumeUseCase {

    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public WeeklyMuscleVolume execute(LocalDate startDate, LocalDate endDate) {
        LocalDate effectiveEnd = (endDate != null) ? endDate : LocalDate.now();
        LocalDate effectiveStart = (startDate != null) ? startDate : effectiveEnd.minusDays(6);

        log.debug("Calculating weekly muscle group volume from {} to {}", effectiveStart, effectiveEnd);

        List<Exercise> exercises = exerciseRepository.findAll();
        List<Training> trainings = trainingRepository.findAll();

        return MuscleVolumeCalculator.compute(exercises, trainings, effectiveStart, effectiveEnd);
    }
}
