package com.david13penalver.foss_training_api.domain.model.analytics;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyWorkload {

    private LocalDate date;
    private double workloadAu;
    private double totalVolumeKg;
    private int completedSessions;
}
