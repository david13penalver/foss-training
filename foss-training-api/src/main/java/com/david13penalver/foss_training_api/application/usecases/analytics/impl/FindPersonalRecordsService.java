package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.FindPersonalRecordsUseCase;
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
public class FindPersonalRecordsService implements FindPersonalRecordsUseCase {

    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public List<PersonalRecord> execute() {
        log.debug("Executing FindPersonalRecordsUseCase");
        List<Exercise> exercises = exerciseRepository.findAll();
        List<Training> trainings = trainingRepository.findAll();
        return PersonalRecordCalculator.computeAll(exercises, trainings);
    }
}
