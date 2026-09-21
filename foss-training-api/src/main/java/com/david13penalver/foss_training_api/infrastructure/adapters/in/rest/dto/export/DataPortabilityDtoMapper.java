package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.export;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.export.FullBackupData;
import com.david13penalver.foss_training_api.domain.model.export.ImportSummary;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.AthleteDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.BodyweightResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataPortabilityDtoMapper {

    private final ExerciseDtoMapper exerciseDtoMapper;
    private final SessionDtoMapper sessionDtoMapper;
    private final TrainingProgramDtoMapper trainingProgramDtoMapper;
    private final TrainingDtoMapper trainingDtoMapper;
    private final AthleteDtoMapper athleteDtoMapper;

    public FullBackupDataDto toResponseDto(FullBackupData domain) {
        if (domain == null) {
            return null;
        }
        List<ExerciseResponseDto> exercises = domain.exercises() != null
                ? domain.exercises().stream().map(exerciseDtoMapper::toResponseDto).toList()
                : List.of();
        List<SessionResponseDto> sessions = domain.sessions() != null
                ? domain.sessions().stream().map(sessionDtoMapper::toResponseDto).toList()
                : List.of();
        List<TrainingProgramResponseDto> programs = domain.programs() != null
                ? domain.programs().stream().map(trainingProgramDtoMapper::toResponseDto).toList()
                : List.of();
        List<TrainingResponseDto> trainings = domain.trainings() != null
                ? domain.trainings().stream().map(trainingDtoMapper::toResponseDto).toList()
                : List.of();
        List<BodyweightResponseDto> bodyweights = domain.bodyweightEntries() != null
                ? domain.bodyweightEntries().stream().map(athleteDtoMapper::toResponseDto).toList()
                : List.of();

        return FullBackupDataDto.builder()
                .exportVersion(domain.exportVersion())
                .exportedAt(domain.exportedAt())
                .exercises(exercises)
                .sessions(sessions)
                .programs(programs)
                .trainings(trainings)
                .bodyweightEntries(bodyweights)
                .build();
    }

    public FullBackupData toDomain(FullBackupDataDto dto) {
        if (dto == null) {
            return null;
        }
        List<Exercise> exercises = dto.getExercises() != null
                ? dto.getExercises().stream().map(exerciseDtoMapper::toDomain).toList()
                : List.of();
        List<Session> sessions = dto.getSessions() != null
                ? dto.getSessions().stream().map(sessionDtoMapper::toDomain).toList()
                : List.of();
        List<TrainingProgram> programs = dto.getPrograms() != null
                ? dto.getPrograms().stream().map(trainingProgramDtoMapper::toDomain).toList()
                : List.of();
        List<Training> trainings = dto.getTrainings() != null
                ? dto.getTrainings().stream().map(trainingDtoMapper::toDomain).toList()
                : List.of();
        List<BodyweightEntry> bodyweights = dto.getBodyweightEntries() != null
                ? dto.getBodyweightEntries().stream().map(athleteDtoMapper::toDomain).toList()
                : List.of();

        return new FullBackupData(
                dto.getExportVersion(),
                dto.getExportedAt(),
                exercises,
                sessions,
                programs,
                trainings,
                bodyweights
        );
    }

    public ImportSummaryDto toSummaryDto(ImportSummary summary) {
        if (summary == null) {
            return null;
        }
        return ImportSummaryDto.builder()
                .exercisesImported(summary.exercisesImported())
                .sessionsImported(summary.sessionsImported())
                .programsImported(summary.programsImported())
                .trainingsImported(summary.trainingsImported())
                .bodyweightImported(summary.bodyweightImported())
                .totalImported(summary.totalImported())
                .build();
    }
}
