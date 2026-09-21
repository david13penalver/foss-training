package com.david13penalver.foss_training_api.domain.model.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

/**
 * Pure domain service for calculating Acute:Chronic Workload Ratio (ACWR) and fatigue monitoring
 * based on the sports science models of Tim Gabbett and Carl Foster (Session-RPE).
 */
public class WorkloadRatioCalculator {

    public static final int ACUTE_DAYS = 7;
    public static final int CHRONIC_DAYS = 28;
    public static final double DEFAULT_MODERATE_RPE = 6.0;
    public static final double DEFAULT_SESSION_MINUTES = 45.0;

    public static WorkloadRatio compute(List<Training> trainings) {
        return compute(trainings, LocalDate.now());
    }

    public static WorkloadRatio compute(List<Training> trainings, LocalDate targetDate) {
        LocalDate effectiveTarget = targetDate != null ? targetDate : LocalDate.now();

        // 1. Initialize 28-day timeline in chronological order
        Map<LocalDate, DailyWorkload> dailyMap = new LinkedHashMap<>(CHRONIC_DAYS);
        for (int i = CHRONIC_DAYS - 1; i >= 0; i--) {
            LocalDate day = effectiveTarget.minusDays(i);
            dailyMap.put(day, DailyWorkload.builder()
                    .date(day)
                    .workloadAu(0.0)
                    .totalVolumeKg(0.0)
                    .completedSessions(0)
                    .build());
        }

        // 2. Aggregate completed trainings onto the timeline
        if (trainings != null) {
            for (Training training : trainings) {
                if (training == null || training.getStatus() != TrainingStatusEnum.COMPLETED) {
                    continue;
                }
                LocalDate date = resolveTrainingDate(training);
                if (date != null && dailyMap.containsKey(date)) {
                    DailyWorkload daily = dailyMap.get(date);
                    double sessionLoad = calculateSessionLoad(training);
                    double sessionVolume = training.calculateTotalVolume();

                    daily.setWorkloadAu(round1(daily.getWorkloadAu() + sessionLoad));
                    daily.setTotalVolumeKg(round1(daily.getTotalVolumeKg() + sessionVolume));
                    daily.setCompletedSessions(daily.getCompletedSessions() + 1);
                }
            }
        }

        // 3. Compute Acute (7-day) and Chronic (28-day) metrics
        LocalDate acuteStartDate = effectiveTarget.minusDays(ACUTE_DAYS - 1);
        double acuteSum = 0.0;
        double chronicSum = 0.0;

        for (DailyWorkload daily : dailyMap.values()) {
            chronicSum += daily.getWorkloadAu();
            if (!daily.getDate().isBefore(acuteStartDate)) {
                acuteSum += daily.getWorkloadAu();
            }
        }

        double acuteWorkload = round1(acuteSum);
        double acuteDailyAverage = round2(acuteWorkload / (double) ACUTE_DAYS);
        double chronicWorkload = round1(chronicSum);
        double chronicWeeklyAverage = round2(chronicWorkload / 4.0);
        double chronicDailyAverage = round2(chronicWorkload / (double) CHRONIC_DAYS);

        // 4. Calculate ACWR ratio (Acute 7d total / Chronic weekly average)
        double acwr;
        if (chronicWeeklyAverage <= 0.0) {
            acwr = (acuteWorkload <= 0.0) ? 0.0 : 2.0;
        } else {
            acwr = round2(acuteWorkload / chronicWeeklyAverage);
        }

        AcwrRiskZone riskZone = AcwrRiskZone.fromRatio(acwr);
        boolean deloadRecommended = (riskZone == AcwrRiskZone.HIGH_RISK);
        String recommendation = generateRecommendation(riskZone, acwr, chronicWorkload, acuteWorkload);

        return WorkloadRatio.builder()
                .targetDate(effectiveTarget)
                .acuteWorkload(acuteWorkload)
                .acuteDailyAverage(acuteDailyAverage)
                .chronicWorkload(chronicWorkload)
                .chronicWeeklyAverage(chronicWeeklyAverage)
                .chronicDailyAverage(chronicDailyAverage)
                .acwr(acwr)
                .riskZone(riskZone)
                .deloadRecommended(deloadRecommended)
                .recommendation(recommendation)
                .dailyWorkloads(new ArrayList<>(dailyMap.values()))
                .build();
    }

    /**
     * Calculates the session training load in Arbitrary Units (AU) using the Session-RPE (sRPE) method:
     * Load = RPE × Duration in minutes
     */
    public static double calculateSessionLoad(Training training) {
        if (training == null || training.getStatus() != TrainingStatusEnum.COMPLETED) {
            return 0.0;
        }

        double rpe = resolveRpe(training);
        double durationMinutes = resolveDurationMinutes(training);

        return round1(rpe * durationMinutes);
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

    private static double resolveRpe(Training training) {
        if (training.getRpe() != null) {
            return training.getRpe().getValue();
        }
        Session session = training.getSession();
        if (session != null && session.getRpe() != null) {
            return session.getRpe().getValue();
        }
        return DEFAULT_MODERATE_RPE;
    }

    private static double resolveDurationMinutes(Training training) {
        if (training.calculateDuration().getTotalSeconds() > 0) {
            return training.calculateDuration().getTotalSeconds() / 60.0;
        }
        Session session = training.getSession();
        if (session != null && session.calculateDuration().getTotalSeconds() > 0) {
            return session.calculateDuration().getTotalSeconds() / 60.0;
        }
        if (session != null && session.getSessionExercises() != null && !session.getSessionExercises().isEmpty()) {
            int setCount = 0;
            double enduranceMins = 0.0;
            for (SessionExercise se : session.getSessionExercises()) {
                if (se instanceof ResistanceSessionExercise rse) {
                    setCount += rse.getTotalSetsCount();
                } else if (se instanceof EnduranceSessionExercise ese) {
                    enduranceMins += ese.calculateTotalDuration().getTotalSeconds() / 60.0;
                }
            }
            if (enduranceMins > 0.0) {
                return enduranceMins + (setCount * 2.5);
            }
            if (setCount > 0) {
                return Math.max(20.0, setCount * 2.5);
            }
        }
        return DEFAULT_SESSION_MINUTES;
    }

    private static String generateRecommendation(
            AcwrRiskZone riskZone, double acwr, double chronicWorkload, double acuteWorkload) {
        if (chronicWorkload <= 0.0 && acuteWorkload <= 0.0) {
            return "No completed workouts recorded in the past 28 days. Start with light-to-moderate sessions to build your chronic fitness base.";
        }
        if (chronicWorkload <= 0.0 && acuteWorkload > 0.0) {
            return "Building initial training baseline. Keep weekly load progression under 10% to prevent acute overload while establishing chronic conditioning.";
        }
        return switch (riskZone) {
            case UNDERTRAINING ->
                "Acute workload is significantly below your chronic training baseline (ACWR " + acwr + " < 0.80). Progressive overload is recommended to maintain fitness and avoid deconditioning injury.";
            case OPTIMAL ->
                "Acute workload is in the optimal 'sweet spot' (ACWR " + acwr + "). Fitness gains are maximized while keeping injury risk minimal. Maintain your current progression.";
            case OVERREACHING ->
                "Acute workload is moderately elevated above your chronic baseline (ACWR " + acwr + "). Fatigue is accumulating; prioritize post-workout recovery, nutrition, and sleep.";
            case HIGH_RISK ->
                "Acute workload spike detected (ACWR " + acwr + " > 1.50). Training in the danger zone significantly elevates injury risk. A deload week (40-50% volume reduction) or active recovery is strongly recommended.";
        };
    }

    private static double round1(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
