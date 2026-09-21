package unitary.com.david13penalver.foss_training_api.domain.model.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgressionCalculator;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.analytics.ProgressionDataPoint;
import com.david13penalver.foss_training_api.domain.model.analytics.ProgressionTrend;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class ExerciseProgressionCalculatorTest {

    private final Exercise benchPress = createExercise(1, "Bench Press");
    private final Exercise squat = createExercise(2, "Barbell Back Squat");

    @Test
    void compute_withNullExercise_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                ExerciseProgressionCalculator.compute(null, List.of(), null, null, OneRepMaxFormula.EPLEY));
    }

    @Test
    void compute_withInvalidDateRange_throwsException() {
        LocalDate start = LocalDate.of(2026, 9, 20);
        LocalDate end = LocalDate.of(2026, 9, 10);
        assertThrows(IllegalArgumentException.class, () ->
                ExerciseProgressionCalculator.compute(benchPress, List.of(), start, end, OneRepMaxFormula.EPLEY));
    }

    @Test
    void compute_withNoCompletedTrainings_returnsEmptyProgression() {
        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(), null, null, OneRepMaxFormula.EPLEY);

        assertNotNull(progression);
        assertEquals(1, progression.getExerciseId());
        assertEquals("Bench Press", progression.getExerciseName());
        assertEquals(0, progression.getTotalSessions());
        assertEquals(0.0, progression.getInitial1RmKg());
        assertEquals(0.0, progression.getLatest1RmKg());
        assertEquals(0.0, progression.getAbsolute1RmGainKg());
        assertEquals(0.0, progression.getRelative1RmGainPercentage());
        assertEquals(ProgressionTrend.INSUFFICIENT_DATA, progression.getTrend());
        assertTrue(progression.getDataPoints().isEmpty());
    }

    @Test
    void compute_withSingleSession_returnsInsufficientDataTrendAndCorrectMetrics() {
        Training training = createTraining(
                10,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(
                        new ResistanceSet(1, SetType.WARMUP, Weight.kg(60.0), 10, null, 90),
                        new ResistanceSet(2, SetType.WORKING, Weight.kg(100.0), 5, Rpe.of(8.5), 180),
                        new ResistanceSet(3, SetType.WORKING, Weight.kg(100.0), 4, Rpe.of(9.0), 180)
                )
        );

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(training), null, null, OneRepMaxFormula.EPLEY);

        assertEquals(1, progression.getTotalSessions());
        assertEquals(LocalDate.of(2026, 9, 1), progression.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 1), progression.getEndDate());
        assertEquals(ProgressionTrend.INSUFFICIENT_DATA, progression.getTrend());
        assertEquals(0.0, progression.getAbsolute1RmGainKg());

        // Epley 100kg x 5 = 100 * (1 + 5/30) = 116.67 kg
        assertEquals(116.67, progression.getInitial1RmKg());
        assertEquals(116.67, progression.getLatest1RmKg());
        assertEquals(116.67, progression.getAllTimeBest1RmKg());
        assertEquals(100.0, progression.getAllTimeBestTopWeightKg());
        // Working volume = (100*5) + (100*4) = 900.0 kg
        assertEquals(900.0, progression.getAllTimeMaxVolumeKg());

        assertEquals(1, progression.getDataPoints().size());
        ProgressionDataPoint point = progression.getDataPoints().get(0);
        assertEquals(10, point.getTrainingId());
        assertEquals(3, point.getTotalSets());
        assertEquals(2, point.getWorkingSets());
        assertEquals(9, point.getTotalReps());
        assertEquals(900.0, point.getTotalVolumeKg());
        assertEquals(100.0, point.getTopWeightKg());
        assertEquals(5, point.getTopWeightReps());
        assertEquals(8.5, point.getTopWeightRpe());
        assertEquals(116.67, point.getEstimated1RmKg());
        assertEquals(100.0, point.getAverageIntensityKg());
    }

    @Test
    void compute_withMultipleSessions_improving_returnsCorrectGainAndImprovingTrend() {
        Training t1 = createTraining(
                101,
                LocalDate.of(2026, 8, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, Rpe.of(8.0), 180))
        ); // 1RM = 116.67

        Training t2 = createTraining(
                102,
                LocalDate.of(2026, 8, 15),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(105.0), 5, Rpe.of(8.5), 180))
        ); // 1RM = 105 * (1 + 5/30) = 122.50

        Training t3 = createTraining(
                103,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(110.0), 5, Rpe.of(9.0), 180))
        ); // 1RM = 110 * (1 + 5/30) = 128.33

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(t3, t1, t2), null, null, OneRepMaxFormula.EPLEY); // intentionally unordered input

        assertEquals(3, progression.getTotalSessions());
        assertEquals(LocalDate.of(2026, 8, 1), progression.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 1), progression.getEndDate());
        assertEquals(116.67, progression.getInitial1RmKg());
        assertEquals(128.33, progression.getLatest1RmKg());
        assertEquals(11.66, progression.getAbsolute1RmGainKg());
        assertEquals(9.99, progression.getRelative1RmGainPercentage()); // (128.33 - 116.67) / 116.67 * 100 = 9.99%
        assertEquals(ProgressionTrend.IMPROVING, progression.getTrend());
        assertEquals(128.33, progression.getAllTimeBest1RmKg());
        assertEquals(110.0, progression.getAllTimeBestTopWeightKg());

        // Chronological order verification
        assertEquals(101, progression.getDataPoints().get(0).getTrainingId());
        assertEquals(102, progression.getDataPoints().get(1).getTrainingId());
        assertEquals(103, progression.getDataPoints().get(2).getTrainingId());
    }

    @Test
    void compute_withMultipleSessions_declining_returnsDecliningTrend() {
        Training t1 = createTraining(
                201,
                LocalDate.of(2026, 8, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 180))
        ); // 116.67

        Training t2 = createTraining(
                202,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(90.0), 5, null, 180))
        ); // 90 * (1 + 5/30) = 105.00

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(t1, t2), null, null, OneRepMaxFormula.EPLEY);

        assertEquals(ProgressionTrend.DECLINING, progression.getTrend());
        assertEquals(-11.67, progression.getAbsolute1RmGainKg());
        assertEquals(-10.0, progression.getRelative1RmGainPercentage());
    }

    @Test
    void compute_withMultipleSessions_stagnant_returnsStagnantTrend() {
        Training t1 = createTraining(
                301,
                LocalDate.of(2026, 8, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 180))
        ); // 116.67

        Training t2 = createTraining(
                302,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.5), 5, null, 180))
        ); // 100.5 * (1 + 5/30) = 117.25 -> 0.5% gain

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(t1, t2), null, null, OneRepMaxFormula.EPLEY);

        assertEquals(ProgressionTrend.STAGNANT, progression.getTrend());
    }

    @Test
    void compute_withDateRangeFiltering_onlyIncludesSessionsWithinRange() {
        Training t1 = createTraining(
                401,
                LocalDate.of(2026, 8, 10),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 180))
        );

        Training t2 = createTraining(
                402,
                LocalDate.of(2026, 8, 20),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(105.0), 5, null, 180))
        );

        Training t3 = createTraining(
                403,
                LocalDate.of(2026, 9, 5),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(110.0), 5, null, 180))
        );

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress,
                List.of(t1, t2, t3),
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 25),
                OneRepMaxFormula.EPLEY);

        assertEquals(1, progression.getTotalSessions());
        assertEquals(402, progression.getDataPoints().get(0).getTrainingId());
        assertEquals(LocalDate.of(2026, 8, 15), progression.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 25), progression.getEndDate());
    }

    @Test
    void compute_withLbsUnits_convertsAccuratelyToKg() {
        // 225 lbs = 102.058 kg -> 1RM with 5 reps = 102.058 * (1 + 5/30) = 119.07 kg
        Training t1 = createTraining(
                501,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.lbs(225.0), 5, null, 180))
        );

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(t1), null, null, OneRepMaxFormula.EPLEY);

        assertEquals(1, progression.getTotalSessions());
        assertEquals(102.06, progression.getDataPoints().get(0).getTopWeightKg());
        assertEquals(119.07, progression.getDataPoints().get(0).getEstimated1RmKg());
    }

    @Test
    void compute_withCustomFormula_usesFormula() {
        // Brzycki: weight * (36 / (37 - reps))
        // 100 * (36 / 32) = 112.50
        Training t1 = createTraining(
                601,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.COMPLETED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 180))
        );

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(t1), null, null, OneRepMaxFormula.BRZYCKI);

        assertEquals(OneRepMaxFormula.BRZYCKI, progression.getFormula());
        assertEquals(112.5, progression.getInitial1RmKg());
    }

    @Test
    void compute_ignoresNonCompletedTrainingsAndOtherExercises() {
        Training scheduled = createTraining(
                701,
                LocalDate.of(2026, 9, 1),
                TrainingStatusEnum.PLANNED,
                benchPress,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 180))
        );

        Training squatTraining = createTraining(
                702,
                LocalDate.of(2026, 9, 2),
                TrainingStatusEnum.COMPLETED,
                squat,
                List.of(new ResistanceSet(1, SetType.WORKING, Weight.kg(140.0), 5, null, 180))
        );

        ExerciseProgression progression = ExerciseProgressionCalculator.compute(
                benchPress, List.of(scheduled, squatTraining), null, null, OneRepMaxFormula.EPLEY);

        assertEquals(0, progression.getTotalSessions());
    }

    private static Exercise createExercise(int id, String name) {
        Exercise ex = new Exercise();
        ex.setId(id);
        ex.setName(name);
        ex.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        return ex;
    }

    private Training createTraining(
            int id, LocalDate date, TrainingStatusEnum status, Exercise exercise, List<ResistanceSet> sets) {

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(id * 10);
        rse.setExercise(exercise);
        if (sets != null) {
            for (ResistanceSet s : sets) {
                rse.addSet(s);
            }
        }

        Session session = new Session();
        session.setId(id * 100);
        session.setName("Test Session");
        session.setSessionExercises(List.of(rse));

        Training training = new Training();
        training.setId(id);
        training.setName("Training on " + date);
        training.setTrainingDate(date);
        training.setStartTime(date.atTime(10, 0));
        training.setEndTime(date.atTime(11, 0));
        training.setStatus(status);
        training.setSession(session);

        return training;
    }
}
