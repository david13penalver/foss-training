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
public class WorkloadRatio {

    private LocalDate targetDate;
    private double acuteWorkload;
    private double acuteDailyAverage;
    private double chronicWorkload;
    private double chronicWeeklyAverage;
    private double chronicDailyAverage;
    private double acwr;
    private AcwrRiskZone riskZone;
    private boolean deloadRecommended;
    private String recommendation;
    @Builder.Default
    private List<DailyWorkload> dailyWorkloads = new ArrayList<>();

    public List<DailyWorkload> getDailyWorkloads() {
        return dailyWorkloads != null ? Collections.unmodifiableList(dailyWorkloads) : Collections.emptyList();
    }
}
