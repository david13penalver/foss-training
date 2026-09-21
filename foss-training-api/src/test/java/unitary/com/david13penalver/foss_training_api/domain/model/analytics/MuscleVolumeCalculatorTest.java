package unitary.com.david13penalver.foss_training_api.domain.model.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.HypertrophyVolumeStatus;
import com.david13penalver.foss_training_api.domain.model.analytics.MuscleGroupVolume;
import com.david13penalver.foss_training_api.domain.model.analytics.MuscleVolumeCalculator;
import com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class MuscleVolumeCalculatorTest {

    private final LocalDate start = LocalDate.of(2026, 9, 15);
    private final LocalDate end = LocalDate.of(2026, 9, 21);

    @Test
    void compute_withInvalidDates_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                MuscleVolumeCalculator.compute(List.of(), List.of(), end, start));
    }

    @Test
    void compute_withNoTrainings_returnsEmptyVolumeAndIdentifiesNeglectedMuscles() {
        WeeklyMuscleVolume result = MuscleVolumeCalculator.compute(List.of(), List.of(), start, end);

        assertNotNull(result);
        assertEquals(start, result.getStartDate());
        assertEquals(end, result.getEndDate());
        assertEquals(0, result.getTotalWorkingSets());
        assertEquals(0.0, result.getTotalVolumeKg());
        assertEquals(MuscleGroup.values().length, result.getMuscleVolumes().size());
        assertTrue(result.getNeglectedMuscleGroups().contains(MuscleGroup.CHEST));
        assertTrue(result.getNeglectedMuscleGroups().contains(MuscleGroup.QUADRICEPS));
        assertFalse(result.getRecommendations().isEmpty());
    }

    @Test
    void compute_allocatesDirectAndIndirectSetsAndCalculatesHypertrophyStatus() {
        // Create Bench Press: primary = CHEST, secondary = TRICEPS
        Exercise benchPress = createExercise(1, "Bench Press", List.of(MuscleGroup.CHEST), List.of(MuscleGroup.TRICEPS));

        // Create 3 workouts with 4 working sets each = 12 sets total of Bench Press
        Training t1 = createTraining(1, benchPress, start, 4, 100.0, 10);
        Training t2 = createTraining(2, benchPress, start.plusDays(2), 4, 100.0, 10);
        Training t3 = createTraining(3, benchPress, start.plusDays(4), 4, 100.0, 10);

        WeeklyMuscleVolume result = MuscleVolumeCalculator.compute(
                List.of(benchPress), List.of(t1, t2, t3), start, end);

        assertEquals(12, result.getTotalWorkingSets());
        assertEquals(12000.0, result.getTotalVolumeKg());

        // CHEST: 12 direct sets, 0 indirect -> 12.0 effective sets (OPTIMAL: 10-20)
        MuscleGroupVolume chest = findMuscleVolume(result, MuscleGroup.CHEST);
        assertNotNull(chest);
        assertEquals(12, chest.getDirectSets());
        assertEquals(0, chest.getIndirectSets());
        assertEquals(12.0, chest.getEffectiveSets());
        assertEquals(12000.0, chest.getTotalVolumeKg());
        assertEquals(HypertrophyVolumeStatus.OPTIMAL, chest.getStatus());
        assertTrue(result.getOptimalMuscleGroups().contains(MuscleGroup.CHEST));

        // TRICEPS: 0 direct sets, 12 indirect sets -> 6.0 effective sets (MAINTENANCE: 6-9)
        MuscleGroupVolume triceps = findMuscleVolume(result, MuscleGroup.TRICEPS);
        assertNotNull(triceps);
        assertEquals(0, triceps.getDirectSets());
        assertEquals(12, triceps.getIndirectSets());
        assertEquals(6.0, triceps.getEffectiveSets());
        assertEquals(6000.0, triceps.getTotalVolumeKg()); // 50% load allocation
        assertEquals(HypertrophyVolumeStatus.MAINTENANCE, triceps.getStatus());

        // Category breakdown: UPPER_BODY should have chest (12) + triceps (6) = 18.0 sets
        assertEquals(18.0, result.getCategoryVolumes().get(MuscleCategory.UPPER_BODY));
        assertEquals(0.0, result.getCategoryVolumes().get(MuscleCategory.LOWER_BODY));
    }

    @Test
    void compute_ignoresWarmupSetsAndNonCompletedWorkouts() {
        Exercise squat = createExercise(2, "Squat", List.of(MuscleGroup.QUADRICEPS), List.of(MuscleGroup.GLUTES));

        // Workout with 2 warmup sets and 3 working sets
        Training completed = createTraining(1, squat, start, 3, 120.0, 5);
        // Add 2 warmup sets to completed session
        ResistanceSessionExercise rse = (ResistanceSessionExercise) completed.getSession().getSessionExercises().get(0);
        ResistanceSet warmup1 = new ResistanceSet(4, SetType.WARMUP, new Weight(60.0, WeightUnit.KG), 10, null, null);
        ResistanceSet warmup2 = new ResistanceSet(5, SetType.WARMUP, new Weight(80.0, WeightUnit.KG), 8, null, null);
        rse.addSet(warmup1);
        rse.addSet(warmup2);

        // Another workout that is IN_PROGRESS (should be ignored)
        Training inProgress = createTraining(2, squat, start.plusDays(1), 5, 120.0, 5);
        inProgress.setStatus(TrainingStatusEnum.IN_PROGRESS);

        // Another workout OUTSIDE date window
        Training outsideDate = createTraining(3, squat, start.minusDays(5), 5, 120.0, 5);

        WeeklyMuscleVolume result = MuscleVolumeCalculator.compute(
                List.of(squat), List.of(completed, inProgress, outsideDate), start, end);

        assertEquals(3, result.getTotalWorkingSets()); // only 3 working sets from completed workout
        MuscleGroupVolume quads = findMuscleVolume(result, MuscleGroup.QUADRICEPS);
        assertEquals(3, quads.getDirectSets());
        assertEquals(HypertrophyVolumeStatus.UNDERTRAINED, quads.getStatus());
    }

    @Test
    void compute_detectsOvertrainingThreshold() {
        // High volume workout: 22 working sets of Chest in one week
        Exercise bench = createExercise(1, "Bench", List.of(MuscleGroup.CHEST), List.of());
        Training highVol = createTraining(1, bench, start, 22, 100.0, 5);

        WeeklyMuscleVolume result = MuscleVolumeCalculator.compute(
                List.of(bench), List.of(highVol), start, end);

        MuscleGroupVolume chest = findMuscleVolume(result, MuscleGroup.CHEST);
        assertEquals(22.0, chest.getEffectiveSets());
        assertEquals(HypertrophyVolumeStatus.OVERTRAINED, chest.getStatus());
        assertTrue(result.getOvertrainedMuscleGroups().contains(MuscleGroup.CHEST));
        assertTrue(result.getRecommendations().stream().anyMatch(r -> r.contains("Excessive weekly volume")));
    }

    @Test
    void compute_calculatesPushPullAndUpperLowerRatios() {
        Exercise bench = createExercise(1, "Bench", List.of(MuscleGroup.CHEST), List.of()); // Upper Push (10 sets)
        Exercise row = createExercise(2, "Row", List.of(MuscleGroup.UPPER_BACK), List.of()); // Upper Pull (10 sets)
        Exercise squat = createExercise(3, "Squat", List.of(MuscleGroup.QUADRICEPS), List.of()); // Lower Push (10 sets)

        Training t1 = createTraining(1, bench, start, 10, 100.0, 5);
        Training t2 = createTraining(2, row, start.plusDays(1), 10, 80.0, 8);
        Training t3 = createTraining(3, squat, start.plusDays(2), 10, 120.0, 5);

        WeeklyMuscleVolume result = MuscleVolumeCalculator.compute(
                List.of(bench, row, squat), List.of(t1, t2, t3), start, end);

        // Push/Pull: Upper Push = 10 (Chest), Upper Pull = 10 (Upper Back) -> ratio = 1.0
        assertEquals(1.0, result.getPushPullRatio());

        // Upper/Lower: Upper = 20 (Bench + Row), Lower = 10 (Squat) -> ratio = 2.0
        assertEquals(2.0, result.getUpperLowerRatio());
    }

    private Exercise createExercise(int id, String name, List<MuscleGroup> primaries, List<MuscleGroup> secondaries) {
        Exercise ex = new Exercise();
        ex.setId(id);
        ex.setName(name);
        ex.setPrimaryCategory(ExerciseCategory.RESISTANCE);

        ResistanceMetrics rm = new ResistanceMetrics();
        rm.setPrimaryMuscles(primaries);
        rm.setSecondaryMuscles(secondaries);
        ex.setResistanceMetrics(rm);

        return ex;
    }

    private Training createTraining(int id, Exercise exercise, LocalDate date, int setsCount, double weightKg, int reps) {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setExercise(exercise);
        for (int i = 1; i <= setsCount; i++) {
            rse.addSet(new ResistanceSet(i, SetType.WORKING, new Weight(weightKg, WeightUnit.KG), reps, null, null));
        }

        Session session = new Session();
        session.setSessionStatus(SessionStatusEnum.COMPLETED);
        session.setSessionExercises(List.of(rse));

        Training training = new Training();
        training.setId(id);
        training.setName("Session on " + date);
        training.setSession(session);
        training.setTrainingDate(date);
        training.setStartTime(date.atTime(10, 0));
        training.setEndTime(date.atTime(11, 0));
        training.setStatus(TrainingStatusEnum.COMPLETED);

        return training;
    }

    private MuscleGroupVolume findMuscleVolume(WeeklyMuscleVolume weekly, MuscleGroup group) {
        return weekly.getMuscleVolumes().stream()
                .filter(mv -> mv.getMuscleGroup() == group)
                .findFirst()
                .orElse(null);
    }
}
