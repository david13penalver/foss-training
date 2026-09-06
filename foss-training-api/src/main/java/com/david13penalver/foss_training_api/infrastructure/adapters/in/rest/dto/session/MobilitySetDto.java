package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MobilitySet")
public class MobilitySetDto {

    private Integer setNumber;
    private DurationDto holdDuration;
    private Integer repetitions;
    private boolean bilateral;

}
