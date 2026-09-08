package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.FindPersonalRecordsByExerciseUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecordCalculator;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindPersonalRecordsByExerciseService implements FindPersonalRecordsByExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public Optional<PersonalRecord> execute(Integer exerciseId) {
        log.debug("Executing FindPersonalRecordsByExerciseUseCase with exerciseId: {}", exerciseId);
        Optional<Exercise> exercise = exerciseRepository.findById(exerciseId);
        if (exercise.isEmpty()) {
            return Optional.empty();
        }
        List<Training> trainings = trainingRepository.findAll();
        return PersonalRecordCalculator.computeForExercise(exercise.get(), trainings);
    }
}
