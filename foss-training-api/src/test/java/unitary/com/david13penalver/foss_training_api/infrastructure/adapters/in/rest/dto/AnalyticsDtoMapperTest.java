package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.CalculatedHeartRateZone;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZoneMethod;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZones;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.analytics.ProgressionDataPoint;
import com.david13penalver.foss_training_api.domain.model.analytics.ProgressionTrend;
import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.AnalyticsDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.CalculatedHeartRateZoneDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.ExerciseProgressionResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.HeartRateZonesResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.OneRepMaxResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.PersonalRecordResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.ProgressionDataPointDto;

class AnalyticsDtoMapperTest {

    private final AnalyticsDtoMapper mapper = new AnalyticsDtoMapper();

    @Test
    void toResponseDto_oneRepMaxEstimate() {
        assertNull(mapper.toResponseDto((OneRepMaxEstimate) null));

        OneRepMaxEstimate estimate = OneRepMaxEstimate.calculate(Weight.kg(100.0), 5, OneRepMaxFormula.EPLEY);
        OneRepMaxResponseDto dto = mapper.toResponseDto(estimate);

        assertNotNull(dto);
        assertEquals(100.0, dto.getWeight());
        assertEquals(WeightUnit.KG, dto.getUnit());
        assertEquals(5, dto.getRepetitions());
        assertEquals(OneRepMaxFormula.EPLEY, dto.getFormula());
        assertEquals(116.67, dto.getEstimated1Rm());
        assertNotNull(dto.getPercentages());

        // Test with null weight in estimate
        OneRepMaxEstimate emptyEstimate = new OneRepMaxEstimate();
        OneRepMaxResponseDto emptyDto = mapper.toResponseDto(emptyEstimate);
        assertNotNull(emptyDto);
        assertEquals(0.0, emptyDto.getWeight());
        assertNull(emptyDto.getUnit());
    }

    @Test
    void toResponseDto_personalRecord_withAllMilestones() {
        assertNull(mapper.toResponseDto((PersonalRecord) null));

        PersonalRecord pr = PersonalRecord.builder()
                .exerciseId(1)
                .exerciseName("Bench Press")
                .maxWeight(new PersonalRecord.MaxWeightRecord(120.0, WeightUnit.KG, 3, 10, LocalDate.of(2026, 9, 1)))
                .bestEstimated1Rm(new PersonalRecord.BestEstimated1RmRecord(130.0, WeightUnit.KG, 120.0, 3, OneRepMaxFormula.EPLEY, 10, LocalDate.of(2026, 9, 1)))
                .maxSessionVolume(new PersonalRecord.MaxSessionVolumeRecord(2500.0, WeightUnit.KG, 10, LocalDate.of(2026, 9, 1)))
                .maxReps(new PersonalRecord.MaxRepsRecord(15, 80.0, WeightUnit.KG, 10, LocalDate.of(2026, 9, 1)))
                .build();

        PersonalRecordResponseDto dto = mapper.toResponseDto(pr);

        assertNotNull(dto);
        assertEquals(1, dto.getExerciseId());
        assertEquals("Bench Press", dto.getExerciseName());

        assertNotNull(dto.getMaxWeight());
        assertEquals(120.0, dto.getMaxWeight().getValue());
        assertEquals(WeightUnit.KG, dto.getMaxWeight().getUnit());
        assertEquals(3, dto.getMaxWeight().getRepetitions());

        assertNotNull(dto.getBestEstimated1Rm());
        assertEquals(130.0, dto.getBestEstimated1Rm().getEstimated1Rm());
        assertEquals(120.0, dto.getBestEstimated1Rm().getSourceWeight());
        assertEquals(3, dto.getBestEstimated1Rm().getSourceReps());

        assertNotNull(dto.getMaxSessionVolume());
        assertEquals(2500.0, dto.getMaxSessionVolume().getVolume());

        assertNotNull(dto.getMaxReps());
        assertEquals(15, dto.getMaxReps().getRepetitions());
        assertEquals(80.0, dto.getMaxReps().getWeight());
    }

    @Test
    void toResponseDto_personalRecord_withNullMilestones() {
        PersonalRecord pr = new PersonalRecord();
        pr.setExerciseId(2);
        pr.setExerciseName("Squat");

        PersonalRecordResponseDto dto = mapper.toResponseDto(pr);

        assertNotNull(dto);
        assertEquals(2, dto.getExerciseId());
        assertEquals("Squat", dto.getExerciseName());
        assertNull(dto.getMaxWeight());
        assertNull(dto.getBestEstimated1Rm());
        assertNull(dto.getMaxSessionVolume());
        assertNull(dto.getMaxReps());
    }

    @Test
    void toResponseDtoList() {
        assertTrue(mapper.toResponseDtoList(null).isEmpty());

        PersonalRecord pr = PersonalRecord.builder().exerciseId(1).exerciseName("Bench").build();
        List<PersonalRecordResponseDto> dtos = mapper.toResponseDtoList(List.of(pr));

        assertEquals(1, dtos.size());
        assertEquals("Bench", dtos.get(0).getExerciseName());
    }

    @Test
    void toResponseDto_workloadRatio() {
        assertNull(mapper.toResponseDto((com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio) null));
        assertNull(mapper.toResponseDto((com.david13penalver.foss_training_api.domain.model.analytics.DailyWorkload) null));

        LocalDate date = LocalDate.of(2026, 9, 21);
        com.david13penalver.foss_training_api.domain.model.analytics.DailyWorkload daily =
                com.david13penalver.foss_training_api.domain.model.analytics.DailyWorkload.builder()
                        .date(date)
                        .workloadAu(350.0)
                        .totalVolumeKg(4000.0)
                        .completedSessions(1)
                        .build();

        com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio ratio =
                com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio.builder()
                        .targetDate(date)
                        .acuteWorkload(1200.0)
                        .acuteDailyAverage(171.43)
                        .chronicWorkload(4200.0)
                        .chronicWeeklyAverage(1050.0)
                        .chronicDailyAverage(150.0)
                        .acwr(1.14)
                        .riskZone(com.david13penalver.foss_training_api.domain.model.analytics.AcwrRiskZone.OPTIMAL)
                        .deloadRecommended(false)
                        .recommendation("Optimal progression")
                        .dailyWorkloads(List.of(daily))
                        .build();

        com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.AcwrResponseDto dto = mapper.toResponseDto(ratio);

        assertNotNull(dto);
        assertEquals(date, dto.getTargetDate());
        assertEquals(1200.0, dto.getAcuteWorkload());
        assertEquals(171.43, dto.getAcuteDailyAverage());
        assertEquals(4200.0, dto.getChronicWorkload());
        assertEquals(1050.0, dto.getChronicWeeklyAverage());
        assertEquals(150.0, dto.getChronicDailyAverage());
        assertEquals(1.14, dto.getAcwr());
        assertEquals(com.david13penalver.foss_training_api.domain.model.analytics.AcwrRiskZone.OPTIMAL, dto.getRiskZone());
        assertEquals("Optimal Zone", dto.getRiskZoneDisplayName());
        assertFalse(dto.isDeloadRecommended());
        assertEquals("Optimal progression", dto.getRecommendation());
        assertEquals(1, dto.getDailyWorkloads().size());

        com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.DailyWorkloadResponseDto dailyDto = dto.getDailyWorkloads().get(0);
        assertEquals(date, dailyDto.getDate());
        assertEquals(350.0, dailyDto.getWorkloadAu());
        assertEquals(4000.0, dailyDto.getTotalVolumeKg());
        assertEquals(1, dailyDto.getCompletedSessions());
    }

    @Test
    void toResponseDto_weeklyMuscleVolume() {
        assertNull(mapper.toResponseDto((com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume) null));
        assertNull(mapper.toResponseDto((com.david13penalver.foss_training_api.domain.model.analytics.MuscleGroupVolume) null));

        LocalDate start = LocalDate.of(2026, 9, 15);
        LocalDate end = LocalDate.of(2026, 9, 21);

        com.david13penalver.foss_training_api.domain.model.analytics.MuscleGroupVolume chest =
                com.david13penalver.foss_training_api.domain.model.analytics.MuscleGroupVolume.builder()
                        .muscleGroup(com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup.CHEST)
                        .muscleGroupName("Chest")
                        .category(com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory.UPPER_BODY)
                        .directSets(12)
                        .indirectSets(0)
                        .effectiveSets(12.0)
                        .totalVolumeKg(12000.0)
                        .status(com.david13penalver.foss_training_api.domain.model.analytics.HypertrophyVolumeStatus.OPTIMAL)
                        .build();

        com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume volume =
                com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume.builder()
                        .startDate(start)
                        .endDate(end)
                        .totalWorkingSets(12)
                        .totalVolumeKg(12000.0)
                        .muscleVolumes(List.of(chest))
                        .categoryVolumes(java.util.Map.of(com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory.UPPER_BODY, 12.0))
                        .pushPullRatio(1.2)
                        .upperLowerRatio(2.0)
                        .neglectedMuscleGroups(List.of(com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup.QUADRICEPS))
                        .optimalMuscleGroups(List.of(com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup.CHEST))
                        .overtrainedMuscleGroups(List.of())
                        .recommendations(List.of("Great balance"))
                        .build();

        com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.WeeklyMuscleVolumeResponseDto dto = mapper.toResponseDto(volume);

        assertNotNull(dto);
        assertEquals(start, dto.getStartDate());
        assertEquals(end, dto.getEndDate());
        assertEquals(12, dto.getTotalWorkingSets());
        assertEquals(12000.0, dto.getTotalVolumeKg());
        assertEquals(1.2, dto.getPushPullRatio());
        assertEquals(2.0, dto.getUpperLowerRatio());
        assertEquals(1, dto.getMuscleVolumes().size());

        com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.MuscleGroupVolumeDto chestDto = dto.getMuscleVolumes().get(0);
        assertEquals(com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup.CHEST, chestDto.getMuscleGroup());
        assertEquals("Chest", chestDto.getMuscleGroupName());
        assertEquals(12, chestDto.getDirectSets());
        assertEquals(12.0, chestDto.getEffectiveSets());
        assertEquals(com.david13penalver.foss_training_api.domain.model.analytics.HypertrophyVolumeStatus.OPTIMAL, chestDto.getStatus());
        assertEquals("Optimal Hypertrophy", chestDto.getStatusDisplayName());
    }

    @Test
    void toResponseDto_exerciseProgression() {
        assertNull(mapper.toResponseDto((ExerciseProgression) null));

        ProgressionDataPoint point = ProgressionDataPoint.builder()
                .trainingId(10)
                .date(LocalDate.of(2026, 9, 1))
                .totalSets(3)
                .workingSets(2)
                .totalReps(10)
                .totalVolumeKg(1000.0)
                .topWeightKg(100.0)
                .topWeightReps(5)
                .topWeightRpe(8.5)
                .estimated1RmKg(116.67)
                .averageIntensityKg(100.0)
                .build();

        ExerciseProgression progression = ExerciseProgression.builder()
                .exerciseId(1)
                .exerciseName("Bench Press")
                .formula(OneRepMaxFormula.EPLEY)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 1))
                .totalSessions(1)
                .initial1RmKg(116.67)
                .latest1RmKg(116.67)
                .absolute1RmGainKg(0.0)
                .relative1RmGainPercentage(0.0)
                .allTimeBest1RmKg(116.67)
                .allTimeBestTopWeightKg(100.0)
                .allTimeMaxVolumeKg(1000.0)
                .trend(ProgressionTrend.INSUFFICIENT_DATA)
                .dataPoints(List.of(point))
                .build();

        ExerciseProgressionResponseDto dto = mapper.toResponseDto(progression);

        assertNotNull(dto);
        assertEquals(1, dto.getExerciseId());
        assertEquals("Bench Press", dto.getExerciseName());
        assertEquals(OneRepMaxFormula.EPLEY, dto.getFormula());
        assertEquals(1, dto.getTotalSessions());
        assertEquals(116.67, dto.getInitial1RmKg());
        assertEquals(ProgressionTrend.INSUFFICIENT_DATA, dto.getTrend());
        assertEquals("Insufficient Data", dto.getTrendDisplayName());
        assertNotNull(dto.getTrendDescription());
        assertEquals(1, dto.getDataPoints().size());

        ProgressionDataPointDto pointDto = dto.getDataPoints().get(0);
        assertEquals(10, pointDto.getTrainingId());
        assertEquals(3, pointDto.getTotalSets());
        assertEquals(2, pointDto.getWorkingSets());
        assertEquals(10, pointDto.getTotalReps());
        assertEquals(1000.0, pointDto.getTotalVolumeKg());
        assertEquals(100.0, pointDto.getTopWeightKg());
        assertEquals(5, pointDto.getTopWeightReps());
        assertEquals(8.5, pointDto.getTopWeightRpe());
        assertEquals(116.67, pointDto.getEstimated1RmKg());
        assertEquals(100.0, pointDto.getAverageIntensityKg());
    }

    @Test
    void toResponseDto_progressionDataPoint_null() {
        assertNull(mapper.toResponseDto((ProgressionDataPoint) null));
    }

    @Test
    void toResponseDto_heartRateZones() {
        assertNull(mapper.toResponseDto((HeartRateZones) null));

        CalculatedHeartRateZone zone = CalculatedHeartRateZone.builder()
                .zone(HeartRateZone.ZONE_2)
                .zoneNumber(2)
                .displayName("Aerobic Base")
                .minPercentage(60.0)
                .maxPercentage(70.0)
                .minBpm(138)
                .maxBpm(151)
                .description("Builds endurance base")
                .trainingBenefit("Maximizes fat oxidation")
                .build();

        HeartRateZones hrz = HeartRateZones.builder()
                .maxHr(190)
                .restingHr(60)
                .age(30)
                .method(HeartRateZoneMethod.KARVONEN)
                .heartRateReserve(130)
                .zones(List.of(zone))
                .build();

        HeartRateZonesResponseDto dto = mapper.toResponseDto(hrz);

        assertNotNull(dto);
        assertEquals(190, dto.getMaxHr());
        assertEquals(60, dto.getRestingHr());
        assertEquals(30, dto.getAge());
        assertEquals(HeartRateZoneMethod.KARVONEN, dto.getMethod());
        assertEquals("Karvonen Heart Rate Reserve", dto.getMethodDisplayName());
        assertNotNull(dto.getMethodDescription());
        assertEquals(130, dto.getHeartRateReserve());
        assertEquals(1, dto.getZones().size());

        CalculatedHeartRateZoneDto zoneDto = dto.getZones().get(0);
        assertEquals(HeartRateZone.ZONE_2, zoneDto.getZone());
        assertEquals(2, zoneDto.getZoneNumber());
        assertEquals("Aerobic Base", zoneDto.getDisplayName());
        assertEquals(60.0, zoneDto.getMinPercentage());
        assertEquals(70.0, zoneDto.getMaxPercentage());
        assertEquals(138, zoneDto.getMinBpm());
        assertEquals(151, zoneDto.getMaxBpm());
    }

    @Test
    void toResponseDto_calculatedHeartRateZone_null() {
        assertNull(mapper.toResponseDto((CalculatedHeartRateZone) null));
    }
}
