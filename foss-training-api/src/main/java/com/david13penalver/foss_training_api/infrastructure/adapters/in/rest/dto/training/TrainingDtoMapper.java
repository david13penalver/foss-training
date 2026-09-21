package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.model.training.WorkoutExerciseSummary;
import com.david13penalver.foss_training_api.domain.model.training.WorkoutSummary;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrainingDtoMapper {

    private final SessionDtoMapper sessionDtoMapper;

    public Training toEntity(TrainingRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Training training = new Training();
        training.setId(dto.getId());
        training.setName(dto.getName());
        training.setDescription(dto.getDescription());
        if (dto.getSession() != null) {
            training.setSession(sessionDtoMapper.toEntity(dto.getSession()));
        }
        training.setTrainingDate(dto.getTrainingDate());
        training.setStartTime(dto.getStartTime());
        training.setEndTime(dto.getEndTime());
        training.setStatus(dto.getStatus() != null ? dto.getStatus() : TrainingStatusEnum.PLANNED);
        training.setNotes(dto.getNotes());
        if (dto.getRpe() != null && dto.getRpe().getValue() != null) {
            training.setRpe(new Rpe(dto.getRpe().getValue()));
        }
        return training;
    }

    public TrainingResponseDto toResponseDto(Training training) {
        if (training == null) {
            return null;
        }
        TrainingResponseDto dto = new TrainingResponseDto();
        dto.setId(training.getId());
        dto.setName(training.getName());
        dto.setDescription(training.getDescription());
        if (training.getSession() != null) {
            dto.setSession(sessionDtoMapper.toResponseDto(training.getSession()));
        }
        dto.setTrainingDate(training.getTrainingDate());
        dto.setStartTime(training.getStartTime());
        dto.setEndTime(training.getEndTime());
        dto.setStatus(training.getStatus());
        dto.setNotes(training.getNotes());

        Duration duration = training.calculateDuration();
        if (duration != null) {
            dto.setDuration(new DurationDto(duration.getTotalSeconds()));
        }

        dto.setTotalVolume(training.calculateTotalVolume());

        if (training.getRpe() != null) {
            dto.setRpe(new RpeDto(training.getRpe().getValue()));
        }

        return dto;
    }

    public List<TrainingResponseDto> toResponseDtoList(List<Training> trainings) {
        if (trainings == null) {
            return List.of();
        }
        return trainings.stream().map(this::toResponseDto).toList();
    }

    public WorkoutSummaryResponseDto toSummaryDto(com.david13penalver.foss_training_api.domain.model.training.WorkoutSummary summary) {
        if (summary == null) {
            return null;
        }
        List<WorkoutExerciseSummaryDto> exercises = summary.getExerciseSummaries() != null
                ? summary.getExerciseSummaries().stream().map(this::toExerciseSummaryDto).toList()
                : List.of();

        return WorkoutSummaryResponseDto.builder()
                .trainingId(summary.getTrainingId())
                .trainingName(summary.getTrainingName())
                .status(summary.getStatus())
                .startTime(summary.getStartTime())
                .endTime(summary.getEndTime())
                .durationSeconds(summary.getDurationSeconds())
                .formattedDuration(summary.getFormattedDuration())
                .totalVolumeKg(summary.getTotalVolumeKg())
                .totalWorkingSets(summary.getTotalWorkingSets())
                .totalReps(summary.getTotalReps())
                .sessionRpe(summary.getSessionRpe())
                .notes(summary.getNotes())
                .exerciseSummaries(exercises)
                .build();
    }

    public WorkoutExerciseSummaryDto toExerciseSummaryDto(WorkoutExerciseSummary exerciseSummary) {
        if (exerciseSummary == null) {
            return null;
        }
        return WorkoutExerciseSummaryDto.builder()
                .exerciseId(exerciseSummary.getExerciseId())
                .exerciseName(exerciseSummary.getExerciseName())
                .category(exerciseSummary.getCategory())
                .completedSets(exerciseSummary.getCompletedSets())
                .totalReps(exerciseSummary.getTotalReps())
                .topWeightKg(exerciseSummary.getTopWeightKg())
                .volumeKg(exerciseSummary.getVolumeKg())
                .estimated1RmKg(exerciseSummary.getEstimated1RmKg())
                .build();
    }

    public ResistanceSet toSetEntity(LogSetRequestDto dto) {
        if (dto == null) {
            return null;
        }
        ResistanceSet set = new ResistanceSet();
        set.setSetNumber(dto.getSetNumber());
        set.setSetType(dto.getSetType());
        if (dto.getWeight() != null && dto.getWeight().getValue() != null) {
            WeightUnit unit = dto.getWeight().getUnit() != null ? dto.getWeight().getUnit() : WeightUnit.KG;
            set.setWeight(new Weight(dto.getWeight().getValue(), unit));
        }
        set.setRepetitions(dto.getRepetitions());
        if (dto.getRpe() != null && dto.getRpe().getValue() != null) {
            set.setRpe(Rpe.of(dto.getRpe().getValue()));
        }
        set.setRestSeconds(dto.getRestSeconds());
        set.setCompleted(dto.getCompleted() != null ? dto.getCompleted() : true);
        return set;
    }

    public EnduranceInterval toIntervalEntity(LogIntervalRequestDto dto) {
        if (dto == null) {
            return null;
        }
        EnduranceInterval interval = new EnduranceInterval();
        interval.setIntervalNumber(dto.getIntervalNumber());
        if (dto.getDistance() != null && dto.getDistance().getValue() != null) {
            DistanceUnit unit = dto.getDistance().getUnit() != null ? dto.getDistance().getUnit() : DistanceUnit.KILOMETERS;
            interval.setDistance(new Distance(dto.getDistance().getValue(), unit));
        }
        if (dto.getDuration() != null && dto.getDuration().getTotalSeconds() != null) {
            interval.setDuration(Duration.seconds(dto.getDuration().getTotalSeconds()));
        }
        if (dto.getPace() != null && dto.getPace().getSecondsPerUnit() != null) {
            DistanceUnit unit = dto.getPace().getUnit() != null ? dto.getPace().getUnit() : DistanceUnit.KILOMETERS;
            interval.setPace(new Pace(dto.getPace().getSecondsPerUnit(), unit));
        }
        interval.setAvgHeartRate(dto.getAvgHeartRate());
        interval.setMaxHeartRate(dto.getMaxHeartRate());
        interval.setAvgPower(dto.getAvgPower());
        interval.setCadence(dto.getCadence());
        interval.setRestSeconds(dto.getRestSeconds());
        return interval;
    }
}
