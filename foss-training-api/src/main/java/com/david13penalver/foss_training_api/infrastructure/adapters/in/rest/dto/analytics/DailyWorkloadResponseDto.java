package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DailyWorkloadResponse", description = "Workload metrics accumulated for a single day")
public class DailyWorkloadResponseDto {

    @Schema(description = "Calendar date", example = "2026-09-21")
    private LocalDate date;

    @Schema(description = "Session RPE workload in arbitrary units (AU)", example = "360.0")
    private double workloadAu;

    @Schema(description = "Total volume moved in kilograms", example = "4500.0")
    private double totalVolumeKg;

    @Schema(description = "Number of completed workouts on this day", example = "1")
    private int completedSessions;
}
