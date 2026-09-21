package com.david13penalver.foss_training_api.domain.model.export;

import java.time.LocalDateTime;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;

public record FullBackupData(
        String exportVersion,
        LocalDateTime exportedAt,
        List<Exercise> exercises,
        List<Session> sessions,
        List<TrainingProgram> programs,
        List<Training> trainings,
        List<BodyweightEntry> bodyweightEntries
) {

    public FullBackupData {
        if (exportVersion == null || exportVersion.isBlank()) {
            exportVersion = "1.0";
        }
        if (exportedAt == null) {
            exportedAt = LocalDateTime.now();
        }
        if (exercises == null) {
            exercises = List.of();
        }
        if (sessions == null) {
            sessions = List.of();
        }
        if (programs == null) {
            programs = List.of();
        }
        if (trainings == null) {
            trainings = List.of();
        }
        if (bodyweightEntries == null) {
            bodyweightEntries = List.of();
        }
    }
}
