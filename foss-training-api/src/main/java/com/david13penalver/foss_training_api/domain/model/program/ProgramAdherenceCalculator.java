package com.david13penalver.foss_training_api.domain.model.program;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

public final class ProgramAdherenceCalculator {

    private ProgramAdherenceCalculator() {
    }

    public static ProgramAdherence calculate(TrainingProgram program, List<Training> trainings) {
        return calculate(program, trainings, LocalDate.now());
    }

    public static ProgramAdherence calculate(TrainingProgram program, List<Training> trainings, LocalDate today) {
        if (program == null) {
            throw new IllegalArgumentException("Training program must not be null");
        }
        LocalDate referenceDate = (today != null) ? today : LocalDate.now();

        int workoutsPerWeek = (program.getWorkouts() != null) ? program.getWorkouts().size() : 0;
        int expectedTotal = program.getDurationWeeks() * workoutsPerWeek;

        if (trainings == null || trainings.isEmpty()) {
            return ProgramAdherence.builder()
                    .programId(program.getId())
                    .programName(program.getName())
                    .durationWeeks(program.getDurationWeeks())
                    .totalScheduledWorkouts(expectedTotal)
                    .completedWorkouts(0)
                    .inProgressWorkouts(0)
                    .plannedWorkouts(expectedTotal)
                    .missedWorkouts(0)
                    .cancelledWorkouts(0)
                    .overallCompletionRate(0.0)
                    .currentAdherenceRate(0.0)
                    .currentStreak(0)
                    .longestStreak(0)
                    .status(ProgramAdherenceStatus.NOT_STARTED)
                    .statusDescription(ProgramAdherenceStatus.NOT_STARTED.getDescription())
                    .weeklyBreakdowns(Collections.emptyList())
                    .workoutDetails(Collections.emptyList())
                    .build();
        }

        List<Training> sortedTrainings = new ArrayList<>(trainings);
        sortedTrainings.sort(Comparator.comparing(Training::getTrainingDate, Comparator.nullsLast(Comparator.naturalOrder())));

        int completedCount = 0;
        int inProgressCount = 0;
        int plannedCount = 0;
        int missedCount = 0;
        int cancelledCount = 0;

        List<WorkoutAdherenceItem> details = new ArrayList<>();

        for (Training t : sortedTrainings) {
            TrainingStatusEnum status = t.getStatus() != null ? t.getStatus() : TrainingStatusEnum.PLANNED;
            LocalDate schedDate = t.getTrainingDate();

            boolean isCompleted = (status == TrainingStatusEnum.COMPLETED || status == TrainingStatusEnum.PARTIALLY_COMPLETED);
            boolean isInProgress = (status == TrainingStatusEnum.IN_PROGRESS || status == TrainingStatusEnum.PAUSED);
            boolean isCancelled = (status == TrainingStatusEnum.CANCELLED);
            boolean isSkipped = (status == TrainingStatusEnum.SKIPPED);
            boolean isOverduePlanned = (status == TrainingStatusEnum.PLANNED && schedDate != null && schedDate.isBefore(referenceDate));

            LocalDate completedDate = null;
            if (isCompleted) {
                if (t.getEndTime() != null) {
                    completedDate = t.getEndTime().toLocalDate();
                } else if (t.getStartTime() != null) {
                    completedDate = t.getStartTime().toLocalDate();
                } else {
                    completedDate = schedDate;
                }
            }

            boolean onTime = false;
            if (isCompleted) {
                completedCount++;
                if (completedDate != null && schedDate != null) {
                    onTime = !completedDate.isAfter(schedDate.plusDays(1));
                } else {
                    onTime = true;
                }
            } else if (isInProgress) {
                inProgressCount++;
            } else if (isCancelled) {
                cancelledCount++;
            } else if (isSkipped || isOverduePlanned) {
                missedCount++;
            } else {
                plannedCount++;
            }

            details.add(WorkoutAdherenceItem.builder()
                    .trainingId(t.getId())
                    .workoutName(t.getName())
                    .scheduledDate(schedDate)
                    .completedDate(completedDate)
                    .status(status)
                    .sessionRpe(t.getRpe() != null ? t.getRpe().getValue() : null)
                    .volumeKg(t.calculateTotalVolume())
                    .onTime(onTime)
                    .build());
        }

        int totalScheduled = Math.max(sortedTrainings.size(), expectedTotal);

        double overallCompletionRate = (totalScheduled > 0)
                ? round((completedCount * 100.0) / totalScheduled)
                : 0.0;

        int expectedElapsed = completedCount + missedCount + cancelledCount;
        double currentAdherenceRate;
        if (expectedElapsed > 0) {
            currentAdherenceRate = round((completedCount * 100.0) / expectedElapsed);
        } else {
            currentAdherenceRate = (completedCount > 0) ? 100.0 : 0.0;
        }

        // Calculate streaks
        int currentStreak = 0;
        int longestStreak = 0;
        int tempStreak = 0;

        List<Training> elapsedTrainings = sortedTrainings.stream()
                .filter(t -> {
                    TrainingStatusEnum s = t.getStatus() != null ? t.getStatus() : TrainingStatusEnum.PLANNED;
                    boolean isDone = (s == TrainingStatusEnum.COMPLETED || s == TrainingStatusEnum.PARTIALLY_COMPLETED);
                    boolean isMissedOrCancelled = (s == TrainingStatusEnum.CANCELLED || s == TrainingStatusEnum.SKIPPED ||
                            (s == TrainingStatusEnum.PLANNED && t.getTrainingDate() != null && t.getTrainingDate().isBefore(referenceDate)));
                    return isDone || isMissedOrCancelled;
                })
                .toList();

        for (Training t : elapsedTrainings) {
            TrainingStatusEnum s = t.getStatus();
            boolean isDone = (s == TrainingStatusEnum.COMPLETED || s == TrainingStatusEnum.PARTIALLY_COMPLETED);
            if (isDone) {
                tempStreak++;
                if (tempStreak > longestStreak) {
                    longestStreak = tempStreak;
                }
            } else {
                tempStreak = 0;
            }
        }

        for (int i = elapsedTrainings.size() - 1; i >= 0; i--) {
            Training t = elapsedTrainings.get(i);
            TrainingStatusEnum s = t.getStatus();
            boolean isDone = (s == TrainingStatusEnum.COMPLETED || s == TrainingStatusEnum.PARTIALLY_COMPLETED);
            if (isDone) {
                currentStreak++;
            } else {
                break;
            }
        }

        // Determine status
        ProgramAdherenceStatus adherenceStatus;
        if (totalScheduled > 0 && completedCount >= totalScheduled) {
            adherenceStatus = ProgramAdherenceStatus.COMPLETED;
        } else if (completedCount == 0 && inProgressCount == 0 && expectedElapsed == 0) {
            adherenceStatus = ProgramAdherenceStatus.NOT_STARTED;
        } else if (currentAdherenceRate >= 80.0) {
            adherenceStatus = ProgramAdherenceStatus.ON_TRACK;
        } else if (currentAdherenceRate >= 50.0) {
            adherenceStatus = ProgramAdherenceStatus.BEHIND_SCHEDULE;
        } else {
            adherenceStatus = ProgramAdherenceStatus.AT_RISK;
        }

        // Weekly breakdowns
        List<WeeklyAdherence> weeklyBreakdowns = calculateWeeklyBreakdowns(program, sortedTrainings, referenceDate);

        return ProgramAdherence.builder()
                .programId(program.getId())
                .programName(program.getName())
                .durationWeeks(program.getDurationWeeks())
                .totalScheduledWorkouts(totalScheduled)
                .completedWorkouts(completedCount)
                .inProgressWorkouts(inProgressCount)
                .plannedWorkouts(plannedCount)
                .missedWorkouts(missedCount)
                .cancelledWorkouts(cancelledCount)
                .overallCompletionRate(overallCompletionRate)
                .currentAdherenceRate(currentAdherenceRate)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .status(adherenceStatus)
                .statusDescription(adherenceStatus.getDescription())
                .weeklyBreakdowns(weeklyBreakdowns)
                .workoutDetails(details)
                .build();
    }

    private static List<WeeklyAdherence> calculateWeeklyBreakdowns(
            TrainingProgram program, List<Training> sortedTrainings, LocalDate referenceDate) {

        if (program.getDurationWeeks() <= 0) {
            return Collections.emptyList();
        }

        LocalDate baseDate = sortedTrainings.stream()
                .map(Training::getTrainingDate)
                .filter(d -> d != null)
                .findFirst()
                .orElse(referenceDate);

        List<WeeklyAdherence> weeks = new ArrayList<>();

        for (int w = 1; w <= program.getDurationWeeks(); w++) {
            LocalDate weekStart = baseDate.plusWeeks(w - 1);
            LocalDate weekEnd = weekStart.plusDays(6);

            List<Training> weekTrainings = sortedTrainings.stream()
                    .filter(t -> t.getTrainingDate() != null
                            && !t.getTrainingDate().isBefore(weekStart)
                            && !t.getTrainingDate().isAfter(weekEnd))
                    .toList();

            int scheduled = weekTrainings.size();
            int completed = 0;
            int missed = 0;

            for (Training t : weekTrainings) {
                TrainingStatusEnum s = t.getStatus() != null ? t.getStatus() : TrainingStatusEnum.PLANNED;
                if (s == TrainingStatusEnum.COMPLETED || s == TrainingStatusEnum.PARTIALLY_COMPLETED) {
                    completed++;
                } else if (s == TrainingStatusEnum.CANCELLED || s == TrainingStatusEnum.SKIPPED ||
                        (s == TrainingStatusEnum.PLANNED && t.getTrainingDate() != null && t.getTrainingDate().isBefore(referenceDate))) {
                    missed++;
                }
            }

            double rate = (scheduled > 0) ? round((completed * 100.0) / scheduled) : 0.0;
            boolean weekDone = (scheduled > 0 && completed == scheduled);

            weeks.add(WeeklyAdherence.builder()
                    .weekNumber(w)
                    .weekStartDate(weekStart)
                    .weekEndDate(weekEnd)
                    .scheduledWorkouts(scheduled)
                    .completedWorkouts(completed)
                    .missedWorkouts(missed)
                    .adherenceRate(rate)
                    .completed(weekDone)
                    .build());
        }

        return weeks;
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
