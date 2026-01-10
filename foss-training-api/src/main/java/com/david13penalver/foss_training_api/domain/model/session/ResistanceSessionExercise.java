package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResistanceSessionExercise extends SessionExercise {

    private int id;
    private double weight;
    private int repetitions;
    private double rpe; // 1-10 rate of perceived exertion
    private int restSeconds; // Rest taken AFTER this set
    private boolean isWarmup;
    private boolean isFailure;
    private String notes;

}
