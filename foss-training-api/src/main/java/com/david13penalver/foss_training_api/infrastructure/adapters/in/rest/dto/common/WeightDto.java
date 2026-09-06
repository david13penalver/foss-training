package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common;

import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Weight")
public class WeightDto {

    private Double value;
    private WeightUnit unit;

}
