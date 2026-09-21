package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.export;

import java.time.LocalDateTime;
import java.util.List;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.BodyweightResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "FullBackupData", description = "Complete database snapshot for FOSS data portability and backup migration")
public class FullBackupDataDto {

    @Schema(description = "Backup export format version", example = "1.0")
    private String exportVersion;

    @Schema(description = "Export creation timestamp")
    private LocalDateTime exportedAt;

    @Schema(description = "Exercise catalog snapshot")
    private List<ExerciseResponseDto> exercises;

    @Schema(description = "Workout session templates snapshot")
    private List<SessionResponseDto> sessions;

    @Schema(description = "Training programs snapshot")
    private List<TrainingProgramResponseDto> programs;

    @Schema(description = "Executed and planned workouts snapshot")
    private List<TrainingResponseDto> trainings;

    @Schema(description = "Athlete bodyweight tracking entries snapshot")
    private List<BodyweightResponseDto> bodyweightEntries;
}
