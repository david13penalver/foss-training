package com.david13penalver.foss_training_api.domain.model.session;

import com.david13penalver.foss_training_api.domain.model.common.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilitySet {

    private Integer setNumber;
    private Duration holdDuration;
    private Integer repetitions;
    private boolean isBilateral;
}
