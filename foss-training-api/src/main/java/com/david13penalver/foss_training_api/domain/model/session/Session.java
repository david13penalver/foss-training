package com.david13penalver.foss_training_api.domain.model.session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    private Integer id;
    private String name;
    private String description;
    private SessionStatusEnum sessionStatus;
    private List<SessionExercise> sessionExercises;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private Rpe rpe;

    public void start() {
        if (!sessionStatus.canStart()) {
            throw new IllegalStateException("Session cannot be started from status: " + sessionStatus);
        }
        this.startTime = LocalDateTime.now();
        this.sessionStatus = SessionStatusEnum.IN_PROGRESS;
    }

    public void complete() {
        if (!sessionStatus.canComplete()) {
            throw new IllegalStateException("Session cannot be completed from status: " + sessionStatus);
        }
        this.endTime = LocalDateTime.now();
        this.sessionStatus = SessionStatusEnum.COMPLETED;
    }

    public void cancel() {
        if (!sessionStatus.canCancel()) {
            throw new IllegalStateException("Session cannot be cancelled from status: " + sessionStatus);
        }
        this.sessionStatus = SessionStatusEnum.CANCELLED;
    }

    public Duration calculateDuration() {
        if (startTime == null || endTime == null) {
            return Duration.zero();
        }
        return Duration.seconds((int) java.time.Duration.between(startTime, endTime).getSeconds());
    }

    public double calculateTotalVolume() {
        if (sessionExercises == null) {
            return 0.0;
        }
        return sessionExercises.stream()
                .filter(e -> e instanceof ResistanceSessionExercise)
                .mapToDouble(e -> ((ResistanceSessionExercise) e).calculateVolume())
                .sum();
    }

    public void addExercise(SessionExercise exercise) {
        if (sessionExercises == null) {
            sessionExercises = new ArrayList<>();
        }
        if (exercise.getOrderIndex() == null) {
            exercise.setOrderIndex(sessionExercises.size() + 1);
        }
        sessionExercises.add(exercise);
    }

}
