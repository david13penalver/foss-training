package com.david13penalver.foss_training_api.domain.model.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

/**
 * Pure domain service for aggregating weekly muscle group working volume and analyzing
 * hypertrophy balance according to evidence-based landmarks (Brad Schoenfeld, Mike Israetel).
 */
public class MuscleVolumeCalculator {

    private static final List<MuscleGroup> MAJOR_TARGET_MUSCLES = List.of(
            MuscleGroup.CHEST,
            MuscleGroup.LATS,
            MuscleGroup.UPPER_BACK,
            MuscleGroup.SHOULDERS,
            MuscleGroup.BICEPS,
            MuscleGroup.TRICEPS,
            MuscleGroup.QUADRICEPS,
            MuscleGroup.HAMSTRINGS,
            MuscleGroup.GLUTES,
            MuscleGroup.CALVES,
            MuscleGroup.ABS
    );

    public static WeeklyMuscleVolume compute(
            List<Exercise> exercises, List<Training> trainings, LocalDate startDate, LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate: " + startDate + " > " + endDate);
        }

        LocalDate effectiveEnd = (endDate != null) ? endDate : LocalDate.now();
        LocalDate effectiveStart = (startDate != null) ? startDate : effectiveEnd.minusDays(6);

        // 1. Build exercise lookup index
        Map<Integer, Exercise> exerciseMap = new HashMap<>();
        if (exercises != null) {
            for (Exercise ex : exercises) {
                if (ex != null && ex.getId() != null) {
                    exerciseMap.put(ex.getId(), ex);
                }
            }
        }

        // 2. Initialize accumulator per muscle group
        Map<MuscleGroup, MuscleAccumulator> accumulators = new EnumMap<>(MuscleGroup.class);
        for (MuscleGroup mg : MuscleGroup.values()) {
            accumulators.put(mg, new MuscleAccumulator(mg));
        }

        int totalWorkingSets = 0;
        double totalVolumeKg = 0.0;

        // 3. Aggregate completed training sessions within date range
        if (trainings != null) {
            for (Training training : trainings) {
                if (training == null || training.getStatus() != TrainingStatusEnum.COMPLETED) {
                    continue;
                }
                LocalDate date = resolveTrainingDate(training);
                if (date == null || date.isBefore(effectiveStart) || date.isAfter(effectiveEnd)) {
                    continue;
                }

                Session session = training.getSession();
                if (session == null || session.getSessionExercises() == null) {
                    continue;
                }

                for (SessionExercise se : session.getSessionExercises()) {
                    if (!(se instanceof ResistanceSessionExercise rse)) {
                        continue;
                    }
                    Exercise ex = se.getExercise();
                    if (ex != null && ex.getId() != null && exerciseMap.containsKey(ex.getId())) {
                        ex = exerciseMap.get(ex.getId());
                    }
                    if (ex == null || ex.getResistanceMetrics() == null) {
                        continue;
                    }

                    ResistanceMetrics rm = ex.getResistanceMetrics();
                    List<MuscleGroup> primaries = rm.getPrimaryMuscles() != null ? rm.getPrimaryMuscles() : List.of();
                    List<MuscleGroup> secondaries = rm.getSecondaryMuscles() != null ? rm.getSecondaryMuscles() : List.of();
                    if (primaries.isEmpty() && secondaries.isEmpty()) {
                        continue;
                    }

                    if (rse.getSets() != null) {
                        for (ResistanceSet set : rse.getSets()) {
                            if (set == null || !set.isWorkingVolume()) {
                                continue;
                            }
                            totalWorkingSets++;
                            double setVol = set.calculateVolume();
                            totalVolumeKg += setVol;

                            for (MuscleGroup p : primaries) {
                                MuscleAccumulator acc = accumulators.get(p);
                                if (acc != null) {
                                    acc.directSets++;
                                    acc.volumeKg += setVol;
                                }
                            }
                            for (MuscleGroup s : secondaries) {
                                MuscleAccumulator acc = accumulators.get(s);
                                if (acc != null) {
                                    acc.indirectSets++;
                                    acc.volumeKg += (setVol * 0.5);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Build sorted muscle group volume list
        List<MuscleGroupVolume> muscleVolumes = accumulators.values().stream()
                .map(acc -> MuscleGroupVolume.builder()
                        .muscleGroup(acc.group)
                        .muscleGroupName(acc.group.getName())
                        .category(acc.group.getCategory())
                        .directSets(acc.directSets)
                        .indirectSets(acc.indirectSets)
                        .effectiveSets(round1(acc.effectiveSets()))
                        .totalVolumeKg(round1(acc.volumeKg))
                        .status(HypertrophyVolumeStatus.fromSets(acc.effectiveSets()))
                        .build())
                .sorted(Comparator.comparingDouble(MuscleGroupVolume::getEffectiveSets).reversed()
                        .thenComparing(MuscleGroupVolume::getMuscleGroupName))
                .toList();

        // 5. Category volume breakdown
        Map<MuscleCategory, Double> categoryVolumes = new LinkedHashMap<>();
        for (MuscleCategory cat : MuscleCategory.values()) {
            categoryVolumes.put(cat, 0.0);
        }
        for (MuscleAccumulator acc : accumulators.values()) {
            MuscleCategory cat = acc.group.getCategory();
            categoryVolumes.put(cat, round1(categoryVolumes.get(cat) + acc.effectiveSets()));
        }

        // 6. Push vs. Pull and Upper vs. Lower Ratios
        double pushSets = getEffectiveSets(accumulators, MuscleGroup.CHEST)
                + getEffectiveSets(accumulators, MuscleGroup.SHOULDERS)
                + getEffectiveSets(accumulators, MuscleGroup.TRICEPS);

        double pullSets = getEffectiveSets(accumulators, MuscleGroup.LATS)
                + getEffectiveSets(accumulators, MuscleGroup.UPPER_BACK)
                + getEffectiveSets(accumulators, MuscleGroup.BICEPS);

        double pushPullRatio = (pullSets > 0.0)
                ? round2(pushSets / pullSets)
                : (pushSets > 0.0 ? 2.0 : 1.0);

        double upperSets = categoryVolumes.getOrDefault(MuscleCategory.UPPER_BODY, 0.0);
        double lowerSets = categoryVolumes.getOrDefault(MuscleCategory.LOWER_BODY, 0.0);
        double upperLowerRatio = (lowerSets > 0.0)
                ? round2(upperSets / lowerSets)
                : (upperSets > 0.0 ? 3.0 : 1.0);

        // 7. Classify volume categories
        List<MuscleGroup> neglected = MAJOR_TARGET_MUSCLES.stream()
                .filter(mg -> accumulators.get(mg).effectiveSets() < 6.0)
                .toList();

        List<MuscleGroup> optimal = Arrays.stream(MuscleGroup.values())
                .filter(mg -> {
                    double sets = accumulators.get(mg).effectiveSets();
                    return sets >= 10.0 && sets <= 20.0;
                })
                .toList();

        List<MuscleGroup> overtrained = Arrays.stream(MuscleGroup.values())
                .filter(mg -> accumulators.get(mg).effectiveSets() > 20.0)
                .toList();

        // 8. Generate actionable recommendations
        List<String> recommendations = generateRecommendations(
                totalWorkingSets, pushPullRatio, upperLowerRatio, upperSets, lowerSets, pushSets, pullSets, neglected, overtrained);

        return WeeklyMuscleVolume.builder()
                .startDate(effectiveStart)
                .endDate(effectiveEnd)
                .totalWorkingSets(totalWorkingSets)
                .totalVolumeKg(round1(totalVolumeKg))
                .muscleVolumes(muscleVolumes)
                .categoryVolumes(categoryVolumes)
                .pushPullRatio(pushPullRatio)
                .upperLowerRatio(upperLowerRatio)
                .neglectedMuscleGroups(neglected)
                .optimalMuscleGroups(optimal)
                .overtrainedMuscleGroups(overtrained)
                .recommendations(recommendations)
                .build();
    }

    private static double getEffectiveSets(Map<MuscleGroup, MuscleAccumulator> accumulators, MuscleGroup group) {
        MuscleAccumulator acc = accumulators.get(group);
        return acc != null ? acc.effectiveSets() : 0.0;
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

    private static List<String> generateRecommendations(
            int totalSets, double pushPullRatio, double upperLowerRatio,
            double upperSets, double lowerSets, double pushSets, double pullSets,
            List<MuscleGroup> neglected, List<MuscleGroup> overtrained) {

        List<String> recs = new ArrayList<>();

        if (totalSets == 0) {
            recs.add("No completed resistance workouts recorded in this date range. Plan working sessions aiming for 10–20 weekly sets per muscle group.");
            return recs;
        }

        if (upperLowerRatio > 2.5 && lowerSets < 15.0) {
            recs.add(String.format(
                    "Upper body volume (%.1f sets) significantly outpaces lower body (%.1f sets). Consider adding squats, hinges, or leg press sets to maintain symmetrical structural development.",
                    upperSets, lowerSets));
        } else if (upperLowerRatio < 0.5 && upperSets < 15.0) {
            recs.add(String.format(
                    "Lower body volume (%.1f sets) significantly outpaces upper body (%.1f sets). Ensure sufficient pressing and pulling volume for complete athletic balance.",
                    lowerSets, upperSets));
        }

        if (pushPullRatio > 1.5 && pushSets >= 10.0) {
            recs.add(String.format(
                    "Push-to-pull ratio is elevated (%.2f). Add rowing and vertical pulling volume to preserve scapular stability and shoulder health.",
                    pushPullRatio));
        } else if (pushPullRatio < 0.67 && pullSets >= 10.0) {
            recs.add(String.format(
                    "Pull volume dominates push volume (Push/Pull ratio %.2f). Balance your split with horizontal and overhead pressing sets.",
                    pushPullRatio));
        }

        if (!overtrained.isEmpty()) {
            String names = overtrained.stream().map(MuscleGroup::getName).collect(Collectors.joining(", "));
            recs.add("Excessive weekly volume (>20 sets) detected for: " + names + ". Consider reducing sets to avoid junk volume and delayed connective tissue recovery.");
        }

        if (!neglected.isEmpty() && totalSets >= 15) {
            String names = neglected.stream().map(MuscleGroup::getName).limit(4).collect(Collectors.joining(", "));
            recs.add("Under-stimulated target muscle groups (<6 sets): " + names + ". Add 2–4 working sets to meet maintenance or hypertrophy thresholds.");
        }

        if (recs.isEmpty()) {
            recs.add("Excellent weekly volume distribution. Working sets align well with evidence-based hypertrophy landmarks (10–20 sets/muscle group).");
        }

        return recs;
    }

    private static double round1(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static class MuscleAccumulator {
        final MuscleGroup group;
        int directSets = 0;
        int indirectSets = 0;
        double volumeKg = 0.0;

        MuscleAccumulator(MuscleGroup group) {
            this.group = group;
        }

        double effectiveSets() {
            return directSets + (0.5 * indirectSets);
        }
    }
}
