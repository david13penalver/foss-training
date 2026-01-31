package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResistanceSessionExercise {

    private Integer id;
    private Double weight;
    private Integer repetitions;
    private Double rpe; // 1-10 rate of perceived exertion
    private Integer restSeconds; // Rest taken AFTER this set
    private boolean isWarmup;
    private boolean isFailure;
    private String notes;

}
