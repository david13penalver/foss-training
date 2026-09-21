package com.david13penalver.foss_training_api.domain.model.analytics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseProgression {

    private Integer exerciseId;
    private String exerciseName;
    private OneRepMaxFormula formula;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalSessions;
    private double initial1RmKg;
    private double latest1RmKg;
    private double absolute1RmGainKg;
    private double relative1RmGainPercentage;
    private double allTimeBest1RmKg;
    private double allTimeBestTopWeightKg;
    private double allTimeMaxVolumeKg;
    private ProgressionTrend trend;
    @Builder.Default
    private List<ProgressionDataPoint> dataPoints = new ArrayList<>();

    public List<ProgressionDataPoint> getDataPoints() {
        return dataPoints != null ? Collections.unmodifiableList(dataPoints) : Collections.emptyList();
    }
}
