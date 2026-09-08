package com.david13penalver.foss_training_api.domain.model.training;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.session.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Training {

    private Integer id;
    private String name;
    private String description;
    private Session session;
    private LocalDate trainingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TrainingStatusEnum status;
    private String notes;
    private Rpe rpe;

    public void start() {
        if (status == null) {
            status = TrainingStatusEnum.PLANNED;
        }
        if (!status.canStart()) {
            throw new IllegalStateException("Training cannot be started from status: " + status);
        }
        this.startTime = LocalDateTime.now();
        if (this.trainingDate == null) {
            this.trainingDate = LocalDate.now();
        }
        this.status = TrainingStatusEnum.IN_PROGRESS;
    }

    public void complete() {
        if (status == null || !status.canComplete()) {
            throw new IllegalStateException("Training cannot be completed from status: " + status);
        }
        this.endTime = LocalDateTime.now();
        this.status = TrainingStatusEnum.COMPLETED;
    }

    public void cancel() {
        if (status == null || !status.canCancel()) {
            throw new IllegalStateException("Training cannot be cancelled from status: " + status);
        }
        this.status = TrainingStatusEnum.CANCELLED;
    }

    public Duration calculateDuration() {
        if (startTime == null || endTime == null) {
            return Duration.zero();
        }
        return Duration.seconds((int) java.time.Duration.between(startTime, endTime).getSeconds());
    }

    public double calculateTotalVolume() {
        if (session == null) {
            return 0.0;
        }
        return session.calculateTotalVolume();
    }
}
