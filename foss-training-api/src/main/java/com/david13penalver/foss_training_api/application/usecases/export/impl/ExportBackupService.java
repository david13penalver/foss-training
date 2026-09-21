package com.david13penalver.foss_training_api.application.usecases.export.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.export.ExportBackupUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.export.FullBackupData;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportBackupService implements ExportBackupUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SessionRepository sessionRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingRepository trainingRepository;
    private final BodyweightRepository bodyweightRepository;

    @Override
    public FullBackupData execute() {
        log.info("Generating full database backup snapshot");
        List<Exercise> exercises = exerciseRepository.findAll();
        List<Session> sessions = sessionRepository.findAll();
        List<TrainingProgram> programs = trainingProgramRepository.findAll();
        List<Training> trainings = trainingRepository.findAll();
        List<BodyweightEntry> bodyweightEntries = bodyweightRepository.findAll();

        return new FullBackupData(
                "1.0",
                LocalDateTime.now(),
                exercises,
                sessions,
                programs,
                trainings,
                bodyweightEntries
        );
    }
}
