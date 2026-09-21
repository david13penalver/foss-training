package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.analytics.DailyWorkload;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio;

@Component
public class AnalyticsDtoMapper {

    public OneRepMaxResponseDto toResponseDto(OneRepMaxEstimate estimate) {
        if (estimate == null) {
            return null;
        }
        return OneRepMaxResponseDto.builder()
                .weight(estimate.getWeight() != null ? estimate.getWeight().getValue() : 0.0)
                .unit(estimate.getWeight() != null ? estimate.getWeight().getUnit() : null)
                .repetitions(estimate.getReps())
                .formula(estimate.getFormula())
                .estimated1Rm(estimate.getEstimated1Rm())
                .percentages(estimate.getPercentages())
                .build();
    }

    public PersonalRecordResponseDto toResponseDto(PersonalRecord record) {
        if (record == null) {
            return null;
        }
        PersonalRecordResponseDto.MaxWeightRecordDto maxWeightDto = null;
        if (record.getMaxWeight() != null) {
            maxWeightDto = PersonalRecordResponseDto.MaxWeightRecordDto.builder()
                    .value(record.getMaxWeight().getValue())
                    .unit(record.getMaxWeight().getUnit())
                    .repetitions(record.getMaxWeight().getRepetitions())
                    .trainingId(record.getMaxWeight().getTrainingId())
                    .trainingDate(record.getMaxWeight().getTrainingDate())
                    .build();
        }

        PersonalRecordResponseDto.BestEstimated1RmRecordDto best1RmDto = null;
        if (record.getBestEstimated1Rm() != null) {
            best1RmDto = PersonalRecordResponseDto.BestEstimated1RmRecordDto.builder()
                    .estimated1Rm(record.getBestEstimated1Rm().getEstimated1Rm())
                    .unit(record.getBestEstimated1Rm().getUnit())
                    .sourceWeight(record.getBestEstimated1Rm().getSourceWeight())
                    .sourceReps(record.getBestEstimated1Rm().getSourceReps())
                    .formula(record.getBestEstimated1Rm().getFormula())
                    .trainingId(record.getBestEstimated1Rm().getTrainingId())
                    .trainingDate(record.getBestEstimated1Rm().getTrainingDate())
                    .build();
        }

        PersonalRecordResponseDto.MaxSessionVolumeRecordDto maxVolumeDto = null;
        if (record.getMaxSessionVolume() != null) {
            maxVolumeDto = PersonalRecordResponseDto.MaxSessionVolumeRecordDto.builder()
                    .volume(record.getMaxSessionVolume().getVolume())
                    .unit(record.getMaxSessionVolume().getUnit())
                    .trainingId(record.getMaxSessionVolume().getTrainingId())
                    .trainingDate(record.getMaxSessionVolume().getTrainingDate())
                    .build();
        }

        PersonalRecordResponseDto.MaxRepsRecordDto maxRepsDto = null;
        if (record.getMaxReps() != null) {
            maxRepsDto = PersonalRecordResponseDto.MaxRepsRecordDto.builder()
                    .repetitions(record.getMaxReps().getRepetitions())
                    .weight(record.getMaxReps().getWeight())
                    .unit(record.getMaxReps().getUnit())
                    .trainingId(record.getMaxReps().getTrainingId())
                    .trainingDate(record.getMaxReps().getTrainingDate())
                    .build();
        }

        return PersonalRecordResponseDto.builder()
                .exerciseId(record.getExerciseId())
                .exerciseName(record.getExerciseName())
                .maxWeight(maxWeightDto)
                .bestEstimated1Rm(best1RmDto)
                .maxSessionVolume(maxVolumeDto)
                .maxReps(maxRepsDto)
                .build();
    }

    public List<PersonalRecordResponseDto> toResponseDtoList(List<PersonalRecord> records) {
        if (records == null) {
            return Collections.emptyList();
        }
        return records.stream().map(this::toResponseDto).toList();
    }

    public AcwrResponseDto toResponseDto(WorkloadRatio ratio) {
        if (ratio == null) {
            return null;
        }
        List<DailyWorkloadResponseDto> dailyDtos = Collections.emptyList();
        if (ratio.getDailyWorkloads() != null) {
            dailyDtos = ratio.getDailyWorkloads().stream()
                    .map(this::toResponseDto)
                    .toList();
        }

        return AcwrResponseDto.builder()
                .targetDate(ratio.getTargetDate())
                .acuteWorkload(ratio.getAcuteWorkload())
                .acuteDailyAverage(ratio.getAcuteDailyAverage())
                .chronicWorkload(ratio.getChronicWorkload())
                .chronicWeeklyAverage(ratio.getChronicWeeklyAverage())
                .chronicDailyAverage(ratio.getChronicDailyAverage())
                .acwr(ratio.getAcwr())
                .riskZone(ratio.getRiskZone())
                .riskZoneDisplayName(ratio.getRiskZone() != null ? ratio.getRiskZone().getDisplayName() : null)
                .statusDescription(ratio.getRiskZone() != null ? ratio.getRiskZone().getDescription() : null)
                .deloadRecommended(ratio.isDeloadRecommended())
                .recommendation(ratio.getRecommendation())
                .dailyWorkloads(dailyDtos)
                .build();
    }

    public DailyWorkloadResponseDto toResponseDto(DailyWorkload daily) {
        if (daily == null) {
            return null;
        }
        return DailyWorkloadResponseDto.builder()
                .date(daily.getDate())
                .workloadAu(daily.getWorkloadAu())
                .totalVolumeKg(daily.getTotalVolumeKg())
                .completedSessions(daily.getCompletedSessions())
                .build();
    }
}
