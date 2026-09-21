package com.david13penalver.foss_training_api.domain.model.training;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSummary {

    private Integer trainingId;
    private String trainingName;
    private TrainingStatusEnum status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationSeconds;
    private String formattedDuration;
    private Double totalVolumeKg;
    private Integer totalWorkingSets;
    private Integer totalReps;
    private Double sessionRpe;
    private String notes;
    private List<WorkoutExerciseSummary> exerciseSummaries;

    public static WorkoutSummary fromTraining(Training training) {
        if (training == null) {
            return null;
        }

        int durationSecs = 0;
        if (training.getStartTime() != null) {
            LocalDateTime refEnd = training.getEndTime() != null ? training.getEndTime() : LocalDateTime.now();
            durationSecs = Math.max(0, (int) java.time.Duration.between(training.getStartTime(), refEnd).getSeconds());
        }

        List<WorkoutExerciseSummary> exerciseList = new ArrayList<>();
        double sumVolume = 0.0;
        int sumWorkingSets = 0;
        int sumReps = 0;

        Session session = training.getSession();
        if (session != null && session.getSessionExercises() != null) {
            for (SessionExercise se : session.getSessionExercises()) {
                if (se == null) {
                    continue;
                }

                Integer exId = se.getExercise() != null ? se.getExercise().getId() : se.getId();
                String exName = se.getExercise() != null ? se.getExercise().getName() : "Exercise";

                if (se instanceof ResistanceSessionExercise rse) {
                    List<ResistanceSet> sets = rse.getSets() != null ? rse.getSets() : List.of();
                    int completedSets = 0;
                    int reps = 0;
                    double vol = 0.0;
                    double topWeight = 0.0;
                    double best1Rm = 0.0;

                    for (ResistanceSet s : sets) {
                        boolean isDone = Boolean.TRUE.equals(s.getCompleted())
                                || (training.getStatus() == TrainingStatusEnum.COMPLETED && s.getCompleted() == null);
                        if (isDone && s.isWorkingVolume()) {
                            completedSets++;
                            if (s.getRepetitions() != null) {
                                reps += s.getRepetitions();
                            }
                            vol += s.calculateVolume();
                        }

                        if (s.getWeight() != null && s.getRepetitions() != null && s.getRepetitions() > 0) {
                            double wKg = s.getWeight().toKg().getValue();
                            if (wKg > topWeight) {
                                topWeight = wKg;
                            }
                            if (wKg > 0) {
                                double est = OneRepMaxFormula.EPLEY.calculate(wKg, s.getRepetitions());
                                if (est > best1Rm) {
                                    best1Rm = est;
                                }
                            }
                        }
                    }

                    vol = Math.round(vol * 100.0) / 100.0;
                    topWeight = Math.round(topWeight * 100.0) / 100.0;
                    best1Rm = Math.round(best1Rm * 100.0) / 100.0;

                    sumVolume += vol;
                    sumWorkingSets += completedSets;
                    sumReps += reps;

                    exerciseList.add(WorkoutExerciseSummary.builder()
                            .exerciseId(exId)
                            .exerciseName(exName)
                            .category("RESISTANCE")
                            .completedSets(completedSets)
                            .totalReps(reps)
                            .topWeightKg(topWeight)
                            .volumeKg(vol)
                            .estimated1RmKg(best1Rm)
                            .build());
                } else if (se instanceof EnduranceSessionExercise ese) {
                    int intervalsCount = ese.getIntervals() != null ? ese.getIntervals().size() : 0;
                    sumWorkingSets += intervalsCount;
                    exerciseList.add(WorkoutExerciseSummary.builder()
                            .exerciseId(exId)
                            .exerciseName(exName)
                            .category("ENDURANCE")
                            .completedSets(intervalsCount)
                            .totalReps(0)
                            .topWeightKg(0.0)
                            .volumeKg(0.0)
                            .estimated1RmKg(0.0)
                            .build());
                } else {
                    String category = se.getExercise() != null && se.getExercise().getPrimaryCategory() != null
                            ? se.getExercise().getPrimaryCategory().name()
                            : "OTHER";
                    exerciseList.add(WorkoutExerciseSummary.builder()
                            .exerciseId(exId)
                            .exerciseName(exName)
                            .category(category)
                            .completedSets(0)
                            .totalReps(0)
                            .topWeightKg(0.0)
                            .volumeKg(0.0)
                            .estimated1RmKg(0.0)
                            .build());
                }
            }
        }

        sumVolume = Math.round(sumVolume * 100.0) / 100.0;
        Double rpeVal = training.getRpe() != null ? training.getRpe().getValue() : null;

        return WorkoutSummary.builder()
                .trainingId(training.getId())
                .trainingName(training.getName())
                .status(training.getStatus())
                .startTime(training.getStartTime())
                .endTime(training.getEndTime())
                .durationSeconds(durationSecs)
                .formattedDuration(Duration.seconds(durationSecs).toFormattedString())
                .totalVolumeKg(sumVolume)
                .totalWorkingSets(sumWorkingSets)
                .totalReps(sumReps)
                .sessionRpe(rpeVal)
                .notes(training.getNotes())
                .exerciseSummaries(exerciseList)
                .build();
    }
}
