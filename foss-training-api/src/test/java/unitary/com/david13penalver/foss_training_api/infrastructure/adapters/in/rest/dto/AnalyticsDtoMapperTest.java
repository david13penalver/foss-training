package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.AnalyticsDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.OneRepMaxResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.PersonalRecordResponseDto;

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
}
