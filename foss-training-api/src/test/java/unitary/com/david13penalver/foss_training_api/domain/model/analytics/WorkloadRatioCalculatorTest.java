package unitary.com.david13penalver.foss_training_api.domain.model.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.AcwrRiskZone;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatioCalculator;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class WorkloadRatioCalculatorTest {

    private final LocalDate targetDate = LocalDate.of(2026, 9, 21);

    @Test
    void compute_withNoTrainings_returnsZeroWorkloadAndUndertraining() {
        WorkloadRatio ratio = WorkloadRatioCalculator.compute(List.of(), targetDate);

        assertNotNull(ratio);
        assertEquals(targetDate, ratio.getTargetDate());
        assertEquals(0.0, ratio.getAcuteWorkload());
        assertEquals(0.0, ratio.getAcuteDailyAverage());
        assertEquals(0.0, ratio.getChronicWorkload());
        assertEquals(0.0, ratio.getChronicWeeklyAverage());
        assertEquals(0.0, ratio.getChronicDailyAverage());
        assertEquals(0.0, ratio.getAcwr());
        assertEquals(AcwrRiskZone.UNDERTRAINING, ratio.getRiskZone());
        assertFalse(ratio.isDeloadRecommended());
        assertEquals(28, ratio.getDailyWorkloads().size());
        assertEquals(targetDate.minusDays(27), ratio.getDailyWorkloads().get(0).getDate());
        assertEquals(targetDate, ratio.getDailyWorkloads().get(27).getDate());
    }

    @Test
    void compute_withNullTrainingsAndNullTargetDate_handlesGracefully() {
        WorkloadRatio ratio = WorkloadRatioCalculator.compute(null, null);

        assertNotNull(ratio);
        assertNotNull(ratio.getTargetDate());
        assertEquals(28, ratio.getDailyWorkloads().size());
        assertEquals(0.0, ratio.getAcwr());
    }

    @Test
    void calculateSessionLoad_onlyAppliesToCompletedTrainings() {
        Training scheduled = createCompletedTraining(targetDate, 8.0, 60);
        scheduled.setStatus(TrainingStatusEnum.PLANNED);

        Training inProgress = createCompletedTraining(targetDate, 8.0, 60);
        inProgress.setStatus(TrainingStatusEnum.IN_PROGRESS);

        Training cancelled = createCompletedTraining(targetDate, 8.0, 60);
        cancelled.setStatus(TrainingStatusEnum.CANCELLED);

        Training completed = createCompletedTraining(targetDate, 8.0, 60);

        assertEquals(0.0, WorkloadRatioCalculator.calculateSessionLoad(scheduled));
        assertEquals(0.0, WorkloadRatioCalculator.calculateSessionLoad(inProgress));
        assertEquals(0.0, WorkloadRatioCalculator.calculateSessionLoad(cancelled));
        assertEquals(480.0, WorkloadRatioCalculator.calculateSessionLoad(completed)); // 8.0 * 60 min
    }

    @Test
    void compute_withConsistentWorkload_resultsInOptimalSweetSpot() {
        // 4 sessions per week across all 4 weeks at 300 AU each (1200 AU/week)
        List<Training> trainings = new ArrayList<>();
        for (int week = 0; week < 4; week++) {
            // Days: Monday, Wednesday, Friday, Saturday of each week
            trainings.add(createCompletedTraining(targetDate.minusDays(week * 7), 6.0, 50));     // 300 AU
            trainings.add(createCompletedTraining(targetDate.minusDays(week * 7 + 2), 6.0, 50)); // 300 AU
            trainings.add(createCompletedTraining(targetDate.minusDays(week * 7 + 4), 6.0, 50)); // 300 AU
            trainings.add(createCompletedTraining(targetDate.minusDays(week * 7 + 5), 6.0, 50)); // 300 AU
        }

        WorkloadRatio ratio = WorkloadRatioCalculator.compute(trainings, targetDate);

        assertEquals(1200.0, ratio.getAcuteWorkload());
        assertEquals(4800.0, ratio.getChronicWorkload());
        assertEquals(1200.0, ratio.getChronicWeeklyAverage());
        assertEquals(1.0, ratio.getAcwr()); // 1200 / 1200 = 1.0
        assertEquals(AcwrRiskZone.OPTIMAL, ratio.getRiskZone());
        assertFalse(ratio.isDeloadRecommended());
        assertTrue(ratio.getRecommendation().contains("sweet spot"));
    }

    @Test
    void compute_withAcuteSpike_triggersHighRiskAndDeloadRecommendation() {
        // Steady baseline of 400 AU/week for weeks 2, 3, 4 (weeks prior to acute week)
        List<Training> trainings = new ArrayList<>();
        trainings.add(createCompletedTraining(targetDate.minusDays(8), 5.0, 80));  // 400 AU (week 2)
        trainings.add(createCompletedTraining(targetDate.minusDays(15), 5.0, 80)); // 400 AU (week 3)
        trainings.add(createCompletedTraining(targetDate.minusDays(22), 5.0, 80)); // 400 AU (week 4)

        // Massive acute spike: 1800 AU in week 1
        trainings.add(createCompletedTraining(targetDate, 9.0, 100));            // 900 AU
        trainings.add(createCompletedTraining(targetDate.minusDays(2), 9.0, 100)); // 900 AU

        WorkloadRatio ratio = WorkloadRatioCalculator.compute(trainings, targetDate);

        // Acute: 1800 AU
        // Chronic: 1800 + 400 + 400 + 400 = 3000 AU
        // Chronic Weekly Avg: 3000 / 4 = 750 AU
        // ACWR = 1800 / 750 = 2.40 (> 1.50)
        assertEquals(1800.0, ratio.getAcuteWorkload());
        assertEquals(3000.0, ratio.getChronicWorkload());
        assertEquals(750.0, ratio.getChronicWeeklyAverage());
        assertEquals(2.40, ratio.getAcwr());
        assertEquals(AcwrRiskZone.HIGH_RISK, ratio.getRiskZone());
        assertTrue(ratio.isDeloadRecommended());
        assertTrue(ratio.getRecommendation().contains("danger zone"));
    }

    @Test
    void compute_withModerateFatigue_resultsInOverreaching() {
        // Chronic baseline ~1000 AU/week
        List<Training> trainings = new ArrayList<>();
        trainings.add(createCompletedTraining(targetDate.minusDays(8), 10.0, 100));  // 1000 AU
        trainings.add(createCompletedTraining(targetDate.minusDays(15), 10.0, 100)); // 1000 AU
        trainings.add(createCompletedTraining(targetDate.minusDays(22), 10.0, 100)); // 1000 AU

        // Acute week has 1600 AU
        trainings.add(createCompletedTraining(targetDate, 8.0, 100));            // 800 AU
        trainings.add(createCompletedTraining(targetDate.minusDays(3), 8.0, 100)); // 800 AU

        // Acute = 1600
        // Chronic = 1600 + 3000 = 4600
        // Chronic weekly avg = 4600 / 4 = 1150
        // ACWR = 1600 / 1150 = 1.39 (between 1.30 and 1.50)
        WorkloadRatio ratio = WorkloadRatioCalculator.compute(trainings, targetDate);

        assertEquals(1600.0, ratio.getAcuteWorkload());
        assertEquals(4600.0, ratio.getChronicWorkload());
        assertEquals(1150.0, ratio.getChronicWeeklyAverage());
        assertEquals(1.39, ratio.getAcwr());
        assertEquals(AcwrRiskZone.OVERREACHING, ratio.getRiskZone());
        assertFalse(ratio.isDeloadRecommended());
        assertTrue(ratio.getRecommendation().contains("moderately elevated"));
    }

    @Test
    void compute_ignoresTrainingsOlderThan28Days() {
        Training oldTraining = createCompletedTraining(targetDate.minusDays(35), 10.0, 100);
        Training validTraining = createCompletedTraining(targetDate, 7.0, 60);

        WorkloadRatio ratio = WorkloadRatioCalculator.compute(List.of(oldTraining, validTraining), targetDate);

        assertEquals(420.0, ratio.getAcuteWorkload());
        assertEquals(420.0, ratio.getChronicWorkload());
    }

    @Test
    void compute_aggregatesMultipleSessionsOnSameDay() {
        Training morning = createCompletedTraining(targetDate, 6.0, 30); // 180 AU
        Training evening = createCompletedTraining(targetDate, 8.0, 45); // 360 AU

        WorkloadRatio ratio = WorkloadRatioCalculator.compute(List.of(morning, evening), targetDate);

        assertEquals(540.0, ratio.getAcuteWorkload());
        assertEquals(2, ratio.getDailyWorkloads().get(27).getCompletedSessions());
        assertEquals(540.0, ratio.getDailyWorkloads().get(27).getWorkloadAu());
    }

    private Training createCompletedTraining(LocalDate date, double rpeValue, int durationMinutes) {
        LocalDateTime start = date.atTime(10, 0);
        LocalDateTime end = start.plusMinutes(durationMinutes);

        Exercise exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Squat");
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);

        ResistanceSessionExercise se = new ResistanceSessionExercise();
        se.setExercise(exercise);
        ResistanceSet set = new ResistanceSet();
        set.setSetNumber(1);
        set.setSetType(SetType.WORKING);
        set.setWeight(new Weight(100.0, WeightUnit.KG));
        set.setRepetitions(10);
        se.addSet(set);

        Session session = new Session();
        session.setSessionStatus(SessionStatusEnum.COMPLETED);
        session.setSessionExercises(List.of(se));

        Training training = new Training();
        training.setId(1);
        training.setName("Workout " + date);
        training.setSession(session);
        training.setTrainingDate(date);
        training.setStartTime(start);
        training.setEndTime(end);
        training.setStatus(TrainingStatusEnum.COMPLETED);
        training.setRpe(Rpe.of(rpeValue));

        return training;
    }
}
