package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common;

import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Distance")
public class DistanceDto {

    private Double value;
    private DistanceUnit unit;

}
