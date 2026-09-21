package com.david13penalver.foss_training_api.domain.model.training;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;

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

    public void complete(Rpe rpe, String notes) {
        complete();
        if (rpe != null) {
            this.rpe = rpe;
        }
        if (notes != null) {
            this.notes = notes;
        }
    }

    public void pause() {
        if (status == null || !status.canPause()) {
            throw new IllegalStateException("Training cannot be paused from status: " + status);
        }
        this.status = TrainingStatusEnum.PAUSED;
    }

    public void resume() {
        if (status == null || !status.canResume()) {
            throw new IllegalStateException("Training cannot be resumed from status: " + status);
        }
        this.status = TrainingStatusEnum.IN_PROGRESS;
    }

    public void cancel() {
        if (status == null || !status.canCancel()) {
            throw new IllegalStateException("Training cannot be cancelled from status: " + status);
        }
        this.status = TrainingStatusEnum.CANCELLED;
    }

    public void logSet(Integer exerciseId, ResistanceSet set) {
        validateActiveWorkout();
        if (session == null || session.getSessionExercises() == null) {
            throw new IllegalStateException("Training session has no exercises defined.");
        }
        SessionExercise exercise = session.findExercise(exerciseId);
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " not found in this training session.");
        }
        if (!(exercise instanceof ResistanceSessionExercise resistanceExercise)) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " is not a resistance exercise.");
        }
        resistanceExercise.logSet(set);
    }

    public void removeSet(Integer exerciseId, int setNumber) {
        validateActiveWorkout();
        if (session == null || session.getSessionExercises() == null) {
            throw new IllegalStateException("Training session has no exercises defined.");
        }
        SessionExercise exercise = session.findExercise(exerciseId);
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " not found in this training session.");
        }
        if (!(exercise instanceof ResistanceSessionExercise resistanceExercise)) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " is not a resistance exercise.");
        }
        boolean removed = resistanceExercise.removeSet(setNumber);
        if (!removed) {
            throw new IllegalArgumentException("Set number " + setNumber + " not found for exercise ID " + exerciseId);
        }
    }

    public void logInterval(Integer exerciseId, EnduranceInterval interval) {
        validateActiveWorkout();
        if (session == null || session.getSessionExercises() == null) {
            throw new IllegalStateException("Training session has no exercises defined.");
        }
        SessionExercise exercise = session.findExercise(exerciseId);
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " not found in this training session.");
        }
        if (!(exercise instanceof EnduranceSessionExercise enduranceExercise)) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " is not an endurance exercise.");
        }
        enduranceExercise.logInterval(interval);
    }

    public void removeInterval(Integer exerciseId, int intervalNumber) {
        validateActiveWorkout();
        if (session == null || session.getSessionExercises() == null) {
            throw new IllegalStateException("Training session has no exercises defined.");
        }
        SessionExercise exercise = session.findExercise(exerciseId);
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " not found in this training session.");
        }
        if (!(exercise instanceof EnduranceSessionExercise enduranceExercise)) {
            throw new IllegalArgumentException("Exercise with ID " + exerciseId + " is not an endurance exercise.");
        }
        boolean removed = enduranceExercise.removeInterval(intervalNumber);
        if (!removed) {
            throw new IllegalArgumentException("Interval number " + intervalNumber + " not found for exercise ID " + exerciseId);
        }
    }

    private void validateActiveWorkout() {
        if (status != TrainingStatusEnum.IN_PROGRESS && status != TrainingStatusEnum.PAUSED) {
            throw new IllegalStateException("Cannot modify workout performance while in status: " + status + ". Workout must be IN_PROGRESS or PAUSED.");
        }
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
