package com.david13penalver.foss_training_api.application.usecases.export.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david13penalver.foss_training_api.application.usecases.export.ImportBackupUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.export.FullBackupData;
import com.david13penalver.foss_training_api.domain.model.export.ImportSummary;
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
public class ImportBackupService implements ImportBackupUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SessionRepository sessionRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingRepository trainingRepository;
    private final BodyweightRepository bodyweightRepository;

    @Override
    @Transactional
    public ImportSummary execute(FullBackupData backupData) {
        log.info("Starting database restore from full backup data");
        if (backupData == null) {
            throw new IllegalArgumentException("Backup data cannot be null");
        }

        int exercisesImported = 0;
        if (backupData.exercises() != null) {
            for (Exercise ex : backupData.exercises()) {
                if (ex.getId() != null && !exerciseRepository.existsById(ex.getId())) {
                    ex.setId(null);
                }
                exerciseRepository.save(ex);
                exercisesImported++;
            }
        }

        int sessionsImported = 0;
        if (backupData.sessions() != null) {
            for (Session s : backupData.sessions()) {
                if (s.getId() != null && !sessionRepository.existsById(s.getId())) {
                    s.setId(null);
                }
                sessionRepository.save(s);
                sessionsImported++;
            }
        }

        int programsImported = 0;
        if (backupData.programs() != null) {
            for (TrainingProgram p : backupData.programs()) {
                if (p.getId() != null && !trainingProgramRepository.existsById(p.getId())) {
                    p.setId(null);
                }
                trainingProgramRepository.save(p);
                programsImported++;
            }
        }

        int trainingsImported = 0;
        if (backupData.trainings() != null) {
            for (Training t : backupData.trainings()) {
                if (t.getId() != null && !trainingRepository.existsById(t.getId())) {
                    t.setId(null);
                }
                trainingRepository.save(t);
                trainingsImported++;
            }
        }

        int bodyweightImported = 0;
        if (backupData.bodyweightEntries() != null) {
            for (BodyweightEntry bw : backupData.bodyweightEntries()) {
                if (bw.getId() != null && !bodyweightRepository.existsById(bw.getId())) {
                    bw.setId(null);
                }
                bodyweightRepository.save(bw);
                bodyweightImported++;
            }
        }

        log.info("Restore complete. Exercises: {}, Sessions: {}, Programs: {}, Trainings: {}, Bodyweight: {}",
                exercisesImported, sessionsImported, programsImported, trainingsImported, bodyweightImported);

        return new ImportSummary(
                exercisesImported,
                sessionsImported,
                programsImported,
                trainingsImported,
                bodyweightImported
        );
    }
}
