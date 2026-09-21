package com.david13penalver.foss_training_api.domain.model.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

/**
 * Pure domain calculator for historical exercise strength progression time-series.
 */
public class ExerciseProgressionCalculator {

    public static ExerciseProgression compute(
            Exercise exercise,
            List<Training> trainings,
            LocalDate startDate,
            LocalDate endDate,
            OneRepMaxFormula formula) {

        if (exercise == null || exercise.getId() == null) {
            throw new IllegalArgumentException("Exercise cannot be null and must have an ID");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate: " + startDate + " > " + endDate);
        }

        OneRepMaxFormula effectiveFormula = formula != null ? formula : OneRepMaxFormula.EPLEY;
        List<ProgressionDataPoint> dataPoints = new ArrayList<>();

        if (trainings != null) {
            for (Training training : trainings) {
                if (training == null || training.getStatus() != TrainingStatusEnum.COMPLETED) {
                    continue;
                }

                LocalDate date = resolveTrainingDate(training);
                if (date == null) {
                    continue;
                }

                if (startDate != null && date.isBefore(startDate)) {
                    continue;
                }
                if (endDate != null && date.isAfter(endDate)) {
                    continue;
                }

                Session session = training.getSession();
                if (session == null || session.getSessionExercises() == null) {
                    continue;
                }

                int totalSets = 0;
                int workingSets = 0;
                int totalReps = 0;
                int totalWorkingReps = 0;
                double workingVolumeKg = 0.0;
                double allSetsVolumeKg = 0.0;
                double topWeightKg = 0.0;
                int topWeightReps = 0;
                Double topWeightRpe = null;
                double maxEstimated1RmKg = 0.0;
                boolean hasValidSets = false;

                for (SessionExercise se : session.getSessionExercises()) {
                    if (se instanceof ResistanceSessionExercise rse && se.getExercise() != null
                            && exercise.getId().equals(se.getExercise().getId())) {

                        if (rse.getSets() != null) {
                            for (ResistanceSet set : rse.getSets()) {
                                if (set == null) {
                                    continue;
                                }
                                Integer reps = set.getRepetitions();
                                Weight weight = set.getWeight();

                                if (reps != null && reps > 0) {
                                    totalSets++;
                                    totalReps += reps;
                                    double setVolume = set.calculateVolume();
                                    allSetsVolumeKg += setVolume;

                                    boolean isWorking = set.getSetType() == null || set.getSetType().countsAsWorkingVolume();
                                    if (isWorking) {
                                        workingSets++;
                                        totalWorkingReps += reps;
                                        workingVolumeKg += setVolume;
                                    }

                                    if (weight != null && weight.getValue() > 0) {
                                        hasValidSets = true;
                                        double weightKg = weight.toKg().getValue();

                                        if (weightKg > topWeightKg || (Double.compare(weightKg, topWeightKg) == 0 && reps > topWeightReps)) {
                                            topWeightKg = weightKg;
                                            topWeightReps = reps;
                                            topWeightRpe = set.getRpe() != null ? set.getRpe().getValue() : null;
                                        }

                                        double est1Rm = effectiveFormula.calculate(weightKg, reps);
                                        if (est1Rm > maxEstimated1RmKg) {
                                            maxEstimated1RmKg = est1Rm;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (hasValidSets && totalSets > 0) {
                    int repsToUse = workingSets > 0 ? totalWorkingReps : totalReps;
                    double volumeToUse = workingSets > 0 ? workingVolumeKg : allSetsVolumeKg;
                    double averageIntensity = repsToUse > 0 ? round2(volumeToUse / repsToUse) : 0.0;

                    dataPoints.add(ProgressionDataPoint.builder()
                            .trainingId(training.getId())
                            .date(date)
                            .totalSets(totalSets)
                            .workingSets(workingSets)
                            .totalReps(repsToUse)
                            .totalVolumeKg(round2(volumeToUse))
                            .topWeightKg(round2(topWeightKg))
                            .topWeightReps(topWeightReps)
                            .topWeightRpe(topWeightRpe)
                            .estimated1RmKg(round2(maxEstimated1RmKg))
                            .averageIntensityKg(averageIntensity)
                            .build());
                }
            }
        }

        // Sort data points chronologically by date ascending, then trainingId ascending
        dataPoints.sort(Comparator.comparing(ProgressionDataPoint::getDate)
                .thenComparing(dp -> dp.getTrainingId() != null ? dp.getTrainingId() : 0));

        int totalSessions = dataPoints.size();
        double initial1RmKg = 0.0;
        double latest1RmKg = 0.0;
        double absoluteGain = 0.0;
        double relativeGain = 0.0;
        double allTimeBest1Rm = 0.0;
        double allTimeBestTopWeight = 0.0;
        double allTimeMaxVolume = 0.0;
        LocalDate effectiveStartRange = startDate;
        LocalDate effectiveEndRange = endDate;

        if (totalSessions > 0) {
            ProgressionDataPoint first = dataPoints.get(0);
            ProgressionDataPoint last = dataPoints.get(totalSessions - 1);

            if (effectiveStartRange == null) {
                effectiveStartRange = first.getDate();
            }
            if (effectiveEndRange == null) {
                effectiveEndRange = last.getDate();
            }

            initial1RmKg = first.getEstimated1RmKg();
            latest1RmKg = last.getEstimated1RmKg();

            allTimeBest1Rm = dataPoints.stream()
                    .mapToDouble(ProgressionDataPoint::getEstimated1RmKg)
                    .max()
                    .orElse(0.0);

            allTimeBestTopWeight = dataPoints.stream()
                    .mapToDouble(ProgressionDataPoint::getTopWeightKg)
                    .max()
                    .orElse(0.0);

            allTimeMaxVolume = dataPoints.stream()
                    .mapToDouble(ProgressionDataPoint::getTotalVolumeKg)
                    .max()
                    .orElse(0.0);

            if (totalSessions >= 2) {
                absoluteGain = round2(latest1RmKg - initial1RmKg);
                if (initial1RmKg > 0) {
                    relativeGain = round2(((latest1RmKg - initial1RmKg) / initial1RmKg) * 100.0);
                } else if (latest1RmKg > 0) {
                    relativeGain = 100.0;
                }
            }
        }

        ProgressionTrend trend = ProgressionTrend.evaluate(relativeGain, totalSessions);

        return ExerciseProgression.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .formula(effectiveFormula)
                .startDate(effectiveStartRange)
                .endDate(effectiveEndRange)
                .totalSessions(totalSessions)
                .initial1RmKg(round2(initial1RmKg))
                .latest1RmKg(round2(latest1RmKg))
                .absolute1RmGainKg(round2(absoluteGain))
                .relative1RmGainPercentage(round2(relativeGain))
                .allTimeBest1RmKg(round2(allTimeBest1Rm))
                .allTimeBestTopWeightKg(round2(allTimeBestTopWeight))
                .allTimeMaxVolumeKg(round2(allTimeMaxVolume))
                .trend(trend)
                .dataPoints(dataPoints)
                .build();
    }

    private static LocalDate resolveTrainingDate(Training training) {
        if (training.getTrainingDate() != null) {
            return training.getTrainingDate();
        }
        if (training.getStartTime() != null) {
            return training.getStartTime().toLocalDate();
        }
        return null;
    }

    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
