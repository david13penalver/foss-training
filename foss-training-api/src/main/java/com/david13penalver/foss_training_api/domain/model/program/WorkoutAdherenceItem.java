package com.david13penalver.foss_training_api.domain.model.program;

import java.time.LocalDate;

import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutAdherenceItem {

    private Integer trainingId;
    private String workoutName;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private TrainingStatusEnum status;
    private Double sessionRpe;
    private Double volumeKg;
    private boolean onTime; // completed on or before scheduled date + 1 day grace period
}
