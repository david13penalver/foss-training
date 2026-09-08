package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecordCalculator;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class PersonalRecordTest {

    @Test
    void computeForExercise_nullExercise_returnsEmpty() {
        assertTrue(PersonalRecordCalculator.computeForExercise(null, Collections.emptyList()).isEmpty());
        assertTrue(PersonalRecordCalculator.computeForExercise(new Exercise(), Collections.emptyList()).isEmpty());
    }

    @Test
    void computeAll_nullOrEmpty_returnsEmpty() {
        assertTrue(PersonalRecordCalculator.computeAll(null, Collections.emptyList()).isEmpty());
        assertTrue(PersonalRecordCalculator.computeAll(Collections.emptyList(), null).isEmpty());
    }

    @Test
    void computeForExercise_withCompletedTrainings_calculatesMilestones() {
        Exercise bench = new Exercise();
        bench.setId(1);
        bench.setName("Bench Press");

        Exercise squat = new Exercise();
        squat.setId(2);
        squat.setName("Squat");

        // Workout 1 (Completed): Bench Press 100kg x 5, 120kg x 2
        Training t1 = new Training();
        t1.setId(10);
        t1.setTrainingDate(LocalDate.of(2026, 9, 1));
        t1.setStatus(TrainingStatusEnum.COMPLETED);

        Session s1 = new Session();
        ResistanceSessionExercise rse1 = new ResistanceSessionExercise();
        rse1.setExercise(bench);
        rse1.addSet(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 120));
        rse1.addSet(new ResistanceSet(2, SetType.WORKING, Weight.kg(120.0), 2, null, 180));
        s1.setSessionExercises(List.of(rse1));
        t1.setSession(s1);

        // Workout 2 (Completed): Bench Press 110kg x 5 (New best 1RM: 110*(1+5/30) = 128.33), 80kg x 15 (Max reps)
        Training t2 = new Training();
        t2.setId(20);
        t2.setTrainingDate(LocalDate.of(2026, 9, 5));
        t2.setStatus(TrainingStatusEnum.COMPLETED);

        Session s2 = new Session();
        ResistanceSessionExercise rse2 = new ResistanceSessionExercise();
        rse2.setExercise(bench);
        rse2.addSet(new ResistanceSet(1, SetType.WORKING, Weight.kg(110.0), 5, null, 120));
        rse2.addSet(new ResistanceSet(2, SetType.WORKING, Weight.kg(80.0), 15, null, 120));
        s2.setSessionExercises(List.of(rse2));
        t2.setSession(s2);

        // Workout 3 (In Progress - Should be IGNORED): Bench Press 200kg x 10
        Training t3 = new Training();
        t3.setId(30);
        t3.setStatus(TrainingStatusEnum.IN_PROGRESS);
        Session s3 = new Session();
        ResistanceSessionExercise rse3 = new ResistanceSessionExercise();
        rse3.setExercise(bench);
        rse3.addSet(new ResistanceSet(1, SetType.WORKING, Weight.kg(200.0), 10, null, 120));
        s3.setSessionExercises(List.of(rse3));
        t3.setSession(s3);

        // Workout 4 (Completed with Squat, null sets or null weights handled safely)
        Training t4 = new Training();
        t4.setId(40);
        t4.setStatus(TrainingStatusEnum.COMPLETED);
        Session s4 = new Session();
        ResistanceSessionExercise rse4 = new ResistanceSessionExercise();
        rse4.setExercise(squat);
        rse4.addSet(new ResistanceSet(1, SetType.WORKING, null, null, null, null));
        s4.setSessionExercises(List.of(rse4));
        t4.setSession(s4);

        List<Training> allTrainings = List.of(t1, t2, t3, t4);

        Optional<PersonalRecord> prOpt = PersonalRecordCalculator.computeForExercise(bench, allTrainings);
        assertTrue(prOpt.isPresent());
        PersonalRecord pr = prOpt.get();

        assertEquals(1, pr.getExerciseId());
        assertEquals("Bench Press", pr.getExerciseName());

        // Max Weight: 120kg (from t1)
        assertNotNull(pr.getMaxWeight());
        assertEquals(120.0, pr.getMaxWeight().getValue());
        assertEquals(WeightUnit.KG, pr.getMaxWeight().getUnit());
        assertEquals(2, pr.getMaxWeight().getRepetitions());
        assertEquals(10, pr.getMaxWeight().getTrainingId());
        assertEquals(LocalDate.of(2026, 9, 1), pr.getMaxWeight().getTrainingDate());

        // Best 1RM: 110kg x 5 -> 128.33 (from t2)
        assertNotNull(pr.getBestEstimated1Rm());
        assertEquals(128.33, pr.getBestEstimated1Rm().getEstimated1Rm());
        assertEquals(110.0, pr.getBestEstimated1Rm().getSourceWeight());
        assertEquals(5, pr.getBestEstimated1Rm().getSourceReps());
        assertEquals(OneRepMaxFormula.EPLEY, pr.getBestEstimated1Rm().getFormula());
        assertEquals(20, pr.getBestEstimated1Rm().getTrainingId());

        // Max Volume: t2 (110*5 + 80*15 = 550 + 1200 = 1750.0) vs t1 (100*5 + 120*2 = 740.0)
        assertNotNull(pr.getMaxSessionVolume());
        assertEquals(1750.0, pr.getMaxSessionVolume().getVolume());
        assertEquals(20, pr.getMaxSessionVolume().getTrainingId());

        // Max Reps: 15 reps at 80kg (from t2)
        assertNotNull(pr.getMaxReps());
        assertEquals(15, pr.getMaxReps().getRepetitions());
        assertEquals(80.0, pr.getMaxReps().getWeight());
        assertEquals(20, pr.getMaxReps().getTrainingId());

        // Test computeAll includes both exercises
        List<PersonalRecord> allPrs = PersonalRecordCalculator.computeAll(List.of(bench, squat), allTrainings);
        assertEquals(2, allPrs.size());
    }

    @Test
    void computeForExercise_withNullOrEmptyTrainings_returnsEmptyMilestones() {
        Exercise exercise = new Exercise();
        exercise.setId(5);
        exercise.setName("Deadlift");

        Optional<PersonalRecord> prOpt = PersonalRecordCalculator.computeForExercise(exercise, null);
        assertTrue(prOpt.isPresent());
        PersonalRecord pr = prOpt.get();

        assertEquals(5, pr.getExerciseId());
        assertEquals("Deadlift", pr.getExerciseName());
        assertNull(pr.getMaxWeight());
        assertNull(pr.getBestEstimated1Rm());
        assertNull(pr.getMaxSessionVolume());
        assertNull(pr.getMaxReps());
    }

    @Test
    void computeForExercise_edgeCases_andLbsWeight() {
        Exercise bench = new Exercise();
        bench.setId(1);
        bench.setName("Bench Press");

        // Training with null sessionExercises
        Training tNullExercises = new Training();
        tNullExercises.setStatus(TrainingStatusEnum.COMPLETED);
        Session sNull = new Session();
        sNull.setSessionExercises(null);
        tNullExercises.setSession(sNull);

        // Training with set with null weight
        Training tNullSet = new Training();
        tNullSet.setId(101);
        tNullSet.setTrainingDate(LocalDate.of(2026, 9, 2));
        tNullSet.setStatus(TrainingStatusEnum.COMPLETED);
        Session sNullSet = new Session();
        ResistanceSessionExercise rseNullSet = new ResistanceSessionExercise();
        rseNullSet.setExercise(bench);
        rseNullSet.addSet(new ResistanceSet(1, SetType.WORKING, null, 20, null, 60)); // reps > 0 but null weight
        sNullSet.setSessionExercises(List.of(rseNullSet));
        tNullSet.setSession(sNullSet);

        // Training with LBS weights
        Training tLbs = new Training();
        tLbs.setId(102);
        tLbs.setTrainingDate(LocalDate.of(2026, 9, 3));
        tLbs.setStatus(TrainingStatusEnum.COMPLETED);
        Session sLbs = new Session();
        ResistanceSessionExercise rseLbs = new ResistanceSessionExercise();
        rseLbs.setExercise(bench);
        rseLbs.addSet(new ResistanceSet(1, SetType.WORKING, Weight.lbs(300.0), 3, null, 120)); // 300 lbs = 136.07 kg (beats max weight & 1RM)
        rseLbs.addSet(new ResistanceSet(2, SetType.WORKING, Weight.lbs(315.0), 1, null, 180)); // 315 lbs = 142.88 kg
        sLbs.setSessionExercises(List.of(rseLbs));
        tLbs.setSession(sLbs);

        List<Training> trainings = List.of(tNullExercises, tNullSet, tLbs);
        Optional<PersonalRecord> prOpt = PersonalRecordCalculator.computeForExercise(bench, trainings);

        assertTrue(prOpt.isPresent());
        PersonalRecord pr = prOpt.get();
        assertEquals(315.0, pr.getMaxWeight().getValue());
        assertEquals(WeightUnit.LBS, pr.getMaxWeight().getUnit());
        assertEquals(WeightUnit.LBS, pr.getBestEstimated1Rm().getUnit());
        assertEquals(20, pr.getMaxReps().getRepetitions());
        assertEquals(0.0, pr.getMaxReps().getWeight());
    }

    @Test
    void personalRecordCalculator_constructorTest() {
        assertNotNull(new PersonalRecordCalculator());
    }

    @Test
    void personalRecord_gettersAndSetters() {
        PersonalRecord pr = new PersonalRecord();
        pr.setExerciseId(10);
        pr.setExerciseName("Pull-up");
        assertEquals(10, pr.getExerciseId());
        assertEquals("Pull-up", pr.getExerciseName());
    }
}
