package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MobilitySessionExercise extends SessionExercise {

    private int durationSeconds;
    private int repetitions; // For dynamic movements
    private boolean isBilateral;

}
