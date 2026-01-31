package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilitySessionExercise {

    private Integer durationSeconds;
    private Integer repetitions; // For dynamic movements
    private boolean isBilateral;

}
