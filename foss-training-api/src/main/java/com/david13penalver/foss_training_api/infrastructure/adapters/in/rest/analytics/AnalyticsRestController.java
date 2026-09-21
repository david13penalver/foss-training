package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.analytics.CalculateOneRepMaxUseCase;
import com.david13penalver.foss_training_api.application.usecases.analytics.CalculateWorkloadRatioUseCase;
import com.david13penalver.foss_training_api.application.usecases.analytics.FindPersonalRecordsByExerciseUseCase;
import com.david13penalver.foss_training_api.application.usecases.analytics.FindPersonalRecordsUseCase;
import com.david13penalver.foss_training_api.application.usecases.analytics.GetExerciseProgressionUseCase;
import com.david13penalver.foss_training_api.application.usecases.analytics.GetWeeklyMuscleVolumeUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.ExerciseExistsUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.AcwrResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.AnalyticsDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.ExerciseProgressionResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.OneRepMaxResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.PersonalRecordResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics.WeeklyMuscleVolumeResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Sports science analytics, 1RM calculator, and personal records detection")
public class AnalyticsRestController {

    private final CalculateOneRepMaxUseCase calculateOneRepMaxUseCase;
    private final FindPersonalRecordsUseCase findPersonalRecordsUseCase;
    private final FindPersonalRecordsByExerciseUseCase findPersonalRecordsByExerciseUseCase;
    private final CalculateWorkloadRatioUseCase calculateWorkloadRatioUseCase;
    private final GetWeeklyMuscleVolumeUseCase getWeeklyMuscleVolumeUseCase;
    private final GetExerciseProgressionUseCase getExerciseProgressionUseCase;
    private final ExerciseExistsUseCase exerciseExistsUseCase;
    private final AnalyticsDtoMapper analyticsDtoMapper;

    @GetMapping("/acwr")
    @Operation(summary = "Calculate Acute:Chronic Workload Ratio (ACWR) and fatigue status")
    public ResponseEntity<AcwrResponseDto> calculateAcwr(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        WorkloadRatio ratio = calculateWorkloadRatioUseCase.execute(targetDate);
        return ResponseEntity.ok(analyticsDtoMapper.toResponseDto(ratio));
    }

    @GetMapping("/muscle-volume")
    @Operation(summary = "Get weekly muscle group volume and hypertrophy balance analysis")
    public ResponseEntity<WeeklyMuscleVolumeResponseDto> getMuscleVolume(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        WeeklyMuscleVolume volume = getWeeklyMuscleVolumeUseCase.execute(startDate, endDate);
        return ResponseEntity.ok(analyticsDtoMapper.toResponseDto(volume));
    }

    @GetMapping("/1rm")
    @Operation(summary = "Calculate estimated 1RM and percentage load breakdown")
    public ResponseEntity<OneRepMaxResponseDto> calculate1Rm(
            @RequestParam double weight,
            @RequestParam(required = false, defaultValue = "KG") WeightUnit unit,
            @RequestParam int reps,
            @RequestParam(required = false, defaultValue = "EPLEY") OneRepMaxFormula formula) {

        Weight w = new Weight(weight, unit);
        OneRepMaxEstimate estimate = calculateOneRepMaxUseCase.execute(w, reps, formula);
        return ResponseEntity.ok(analyticsDtoMapper.toResponseDto(estimate));
    }

    @GetMapping("/personal-records")
    @Operation(summary = "Get personal records across all exercises from completed workouts")
    public ResponseEntity<List<PersonalRecordResponseDto>> getPersonalRecords() {
        List<PersonalRecord> records = findPersonalRecordsUseCase.execute();
        return ResponseEntity.ok(analyticsDtoMapper.toResponseDtoList(records));
    }

    @GetMapping("/personal-records/exercise/{exerciseId}")
    @Operation(summary = "Get personal records for a specific exercise from completed workouts")
    public ResponseEntity<PersonalRecordResponseDto> getPersonalRecordsByExercise(@PathVariable Integer exerciseId) {
        if (!exerciseExistsUseCase.execute(exerciseId)) {
            return ResponseEntity.notFound().build();
        }
        Optional<PersonalRecord> record = findPersonalRecordsByExerciseUseCase.execute(exerciseId);
        return record.map(analyticsDtoMapper::toResponseDto)
                     .map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/progression/{exerciseId}")
    @Operation(summary = "Get exercise strength progression time-series and trend analytics")
    public ResponseEntity<ExerciseProgressionResponseDto> getProgression(
            @PathVariable Integer exerciseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "EPLEY") OneRepMaxFormula formula) {
        if (!exerciseExistsUseCase.execute(exerciseId)) {
            return ResponseEntity.notFound().build();
        }
        Optional<ExerciseProgression> progression = getExerciseProgressionUseCase.execute(exerciseId, startDate, endDate, formula);
        return progression.map(analyticsDtoMapper::toResponseDto)
                     .map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
}
