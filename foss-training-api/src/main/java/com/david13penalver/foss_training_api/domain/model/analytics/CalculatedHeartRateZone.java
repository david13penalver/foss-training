package com.david13penalver.foss_training_api.domain.model.analytics;

import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculatedHeartRateZone {

    private HeartRateZone zone;
    private int zoneNumber;
    private String displayName;
    private double minPercentage;
    private double maxPercentage;
    private int minBpm;
    private int maxBpm;
    private String description;
    private String trainingBenefit;
}
