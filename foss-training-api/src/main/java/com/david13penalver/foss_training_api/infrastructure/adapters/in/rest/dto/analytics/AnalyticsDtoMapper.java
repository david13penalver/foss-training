package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.analytics.CalculatedHeartRateZone;
import com.david13penalver.foss_training_api.domain.model.analytics.DailyWorkload;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZones;
import com.david13penalver.foss_training_api.domain.model.analytics.MuscleGroupVolume;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.analytics.ProgressionDataPoint;
import com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume;
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

    public WeeklyMuscleVolumeResponseDto toResponseDto(WeeklyMuscleVolume weekly) {
        if (weekly == null) {
            return null;
        }
        List<MuscleGroupVolumeDto> volumeDtos = Collections.emptyList();
        if (weekly.getMuscleVolumes() != null) {
            volumeDtos = weekly.getMuscleVolumes().stream()
                    .map(this::toResponseDto)
                    .toList();
        }

        return WeeklyMuscleVolumeResponseDto.builder()
                .startDate(weekly.getStartDate())
                .endDate(weekly.getEndDate())
                .totalWorkingSets(weekly.getTotalWorkingSets())
                .totalVolumeKg(weekly.getTotalVolumeKg())
                .muscleVolumes(volumeDtos)
                .categoryVolumes(weekly.getCategoryVolumes())
                .pushPullRatio(weekly.getPushPullRatio())
                .upperLowerRatio(weekly.getUpperLowerRatio())
                .neglectedMuscleGroups(weekly.getNeglectedMuscleGroups())
                .optimalMuscleGroups(weekly.getOptimalMuscleGroups())
                .overtrainedMuscleGroups(weekly.getOvertrainedMuscleGroups())
                .recommendations(weekly.getRecommendations())
                .build();
    }

    public MuscleGroupVolumeDto toResponseDto(MuscleGroupVolume mv) {
        if (mv == null) {
            return null;
        }
        return MuscleGroupVolumeDto.builder()
                .muscleGroup(mv.getMuscleGroup())
                .muscleGroupName(mv.getMuscleGroupName())
                .category(mv.getCategory())
                .directSets(mv.getDirectSets())
                .indirectSets(mv.getIndirectSets())
                .effectiveSets(mv.getEffectiveSets())
                .totalVolumeKg(mv.getTotalVolumeKg())
                .status(mv.getStatus())
                .statusDisplayName(mv.getStatus() != null ? mv.getStatus().getDisplayName() : null)
                .statusDescription(mv.getStatus() != null ? mv.getStatus().getDescription() : null)
                .build();
    }

    public ExerciseProgressionResponseDto toResponseDto(ExerciseProgression progression) {
        if (progression == null) {
            return null;
        }
        List<ProgressionDataPointDto> pointDtos = Collections.emptyList();
        if (progression.getDataPoints() != null) {
            pointDtos = progression.getDataPoints().stream()
                    .map(this::toResponseDto)
                    .toList();
        }

        return ExerciseProgressionResponseDto.builder()
                .exerciseId(progression.getExerciseId())
                .exerciseName(progression.getExerciseName())
                .formula(progression.getFormula())
                .startDate(progression.getStartDate())
                .endDate(progression.getEndDate())
                .totalSessions(progression.getTotalSessions())
                .initial1RmKg(progression.getInitial1RmKg())
                .latest1RmKg(progression.getLatest1RmKg())
                .absolute1RmGainKg(progression.getAbsolute1RmGainKg())
                .relative1RmGainPercentage(progression.getRelative1RmGainPercentage())
                .allTimeBest1RmKg(progression.getAllTimeBest1RmKg())
                .allTimeBestTopWeightKg(progression.getAllTimeBestTopWeightKg())
                .allTimeMaxVolumeKg(progression.getAllTimeMaxVolumeKg())
                .trend(progression.getTrend())
                .trendDisplayName(progression.getTrend() != null ? progression.getTrend().getDisplayName() : null)
                .trendDescription(progression.getTrend() != null ? progression.getTrend().getDescription() : null)
                .dataPoints(pointDtos)
                .build();
    }

    public ProgressionDataPointDto toResponseDto(ProgressionDataPoint dp) {
        if (dp == null) {
            return null;
        }
        return ProgressionDataPointDto.builder()
                .trainingId(dp.getTrainingId())
                .date(dp.getDate())
                .totalSets(dp.getTotalSets())
                .workingSets(dp.getWorkingSets())
                .totalReps(dp.getTotalReps())
                .totalVolumeKg(dp.getTotalVolumeKg())
                .topWeightKg(dp.getTopWeightKg())
                .topWeightReps(dp.getTopWeightReps())
                .topWeightRpe(dp.getTopWeightRpe())
                .estimated1RmKg(dp.getEstimated1RmKg())
                .averageIntensityKg(dp.getAverageIntensityKg())
                .build();
    }

    public HeartRateZonesResponseDto toResponseDto(HeartRateZones heartRateZones) {
        if (heartRateZones == null) {
            return null;
        }
        List<CalculatedHeartRateZoneDto> zoneDtos = Collections.emptyList();
        if (heartRateZones.getZones() != null) {
            zoneDtos = heartRateZones.getZones().stream()
                    .map(this::toResponseDto)
                    .toList();
        }

        return HeartRateZonesResponseDto.builder()
                .maxHr(heartRateZones.getMaxHr())
                .restingHr(heartRateZones.getRestingHr())
                .age(heartRateZones.getAge())
                .method(heartRateZones.getMethod())
                .methodDisplayName(heartRateZones.getMethod() != null ? heartRateZones.getMethod().getDisplayName() : null)
                .methodDescription(heartRateZones.getMethod() != null ? heartRateZones.getMethod().getDescription() : null)
                .heartRateReserve(heartRateZones.getHeartRateReserve())
                .zones(zoneDtos)
                .build();
    }

    public CalculatedHeartRateZoneDto toResponseDto(CalculatedHeartRateZone zone) {
        if (zone == null) {
            return null;
        }
        return CalculatedHeartRateZoneDto.builder()
                .zone(zone.getZone())
                .zoneNumber(zone.getZoneNumber())
                .displayName(zone.getDisplayName())
                .minPercentage(zone.getMinPercentage())
                .maxPercentage(zone.getMaxPercentage())
                .minBpm(zone.getMinBpm())
                .maxBpm(zone.getMaxBpm())
                .description(zone.getDescription())
                .trainingBenefit(zone.getTrainingBenefit())
                .build();
    }
}
