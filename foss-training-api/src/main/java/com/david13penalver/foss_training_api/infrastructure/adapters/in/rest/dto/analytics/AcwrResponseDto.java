package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.time.LocalDate;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.analytics.AcwrRiskZone;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AcwrResponse", description = "Acute:Chronic Workload Ratio (ACWR) and fatigue status")
public class AcwrResponseDto {

    @Schema(description = "Reference evaluation date", example = "2026-09-21")
    private LocalDate targetDate;

    @Schema(description = "Acute workload (fatigue over the last 7 days) in AU", example = "1250.0")
    private double acuteWorkload;

    @Schema(description = "Acute daily average load in AU/day", example = "178.57")
    private double acuteDailyAverage;

    @Schema(description = "Chronic workload (accumulated over the last 28 days) in AU", example = "4200.0")
    private double chronicWorkload;

    @Schema(description = "Chronic weekly average load in AU/week", example = "1050.0")
    private double chronicWeeklyAverage;

    @Schema(description = "Chronic daily average load in AU/day", example = "150.0")
    private double chronicDailyAverage;

    @Schema(description = "Acute to Chronic Workload Ratio (ACWR)", example = "1.19")
    private double acwr;

    @Schema(description = "Injury risk classification category", example = "OPTIMAL")
    private AcwrRiskZone riskZone;

    @Schema(description = "Human-readable label of the risk zone", example = "Optimal Zone")
    private String riskZoneDisplayName;

    @Schema(description = "Scientific explanation of the risk zone")
    private String statusDescription;

    @Schema(description = "Whether a deload week or active recovery is recommended", example = "false")
    private boolean deloadRecommended;

    @Schema(description = "Actionable sports science training and recovery advice")
    private String recommendation;

    @Schema(description = "Chronological 28-day daily workload breakdown")
    private List<DailyWorkloadResponseDto> dailyWorkloads;
}
