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
public class ProgressionDataPoint {

    private Integer trainingId;
    private LocalDate date;
    private int totalSets;
    private int workingSets;
    private int totalReps;
    private double totalVolumeKg;
    private double topWeightKg;
    private int topWeightReps;
    private Double topWeightRpe;
    private double estimated1RmKg;
    private double averageIntensityKg;
}
