package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DistanceDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.PaceDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EnduranceInterval")
public class EnduranceIntervalDto {

    private Integer intervalNumber;
    private DistanceDto distance;
    private DurationDto duration;
    private PaceDto pace;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Double avgPower;
    private Double cadence;
    private Integer restSeconds;

}
