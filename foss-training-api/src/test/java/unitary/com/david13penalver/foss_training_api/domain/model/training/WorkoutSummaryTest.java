package unitary.com.david13penalver.foss_training_api.domain.model.training;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.model.training.WorkoutExerciseSummary;
import com.david13penalver.foss_training_api.domain.model.training.WorkoutSummary;

class WorkoutSummaryTest {

    @Test
    void fromTraining_whenNull_returnsNull() {
        assertNull(WorkoutSummary.fromTraining(null));
    }

    @Test
    void fromTraining_computesCompletedWorkoutMetrics() {
        Training training = new Training();
        training.setId(1);
        training.setName("Upper Power");
        training.setStatus(TrainingStatusEnum.COMPLETED);
        training.setStartTime(LocalDateTime.of(2026, 9, 21, 10, 0, 0));
        training.setEndTime(LocalDateTime.of(2026, 9, 21, 11, 15, 0)); // 75 mins = 4500 secs
        training.setRpe(Rpe.of(8.5));
        training.setNotes("Pushed hard on bench press");

        Exercise bench = new Exercise();
        bench.setId(101);
        bench.setName("Bench Press");
        bench.setPrimaryCategory(ExerciseCategory.RESISTANCE);

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(1);
        rse.setExercise(bench);
        rse.setSets(List.of(
                new ResistanceSet(1, SetType.WARMUP, Weight.kg(60.0), 10, Rpe.of(5.0), 60, true),
                new ResistanceSet(2, SetType.WORKING, Weight.kg(100.0), 8, Rpe.of(8.0), 120, true),
                new ResistanceSet(3, SetType.WORKING, Weight.kg(105.0), 6, Rpe.of(9.0), 120, true)
        ));

        Exercise run = new Exercise();
        run.setId(102);
        run.setName("Treadmill Intervals");
        run.setPrimaryCategory(ExerciseCategory.ENDURANCE);

        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setId(2);
        ese.setExercise(run);
        EnduranceInterval interval = new EnduranceInterval();
        interval.setIntervalNumber(1);
        interval.setDistance(new Distance(1.0, DistanceUnit.KILOMETERS));
        interval.setDuration(Duration.minutes(5));
        ese.setIntervals(List.of(interval));

        Session session = new Session();
        session.setSessionExercises(List.of(rse, ese));
        training.setSession(session);

        WorkoutSummary summary = WorkoutSummary.fromTraining(training);

        assertNotNull(summary);
        assertEquals(1, summary.getTrainingId());
        assertEquals("Upper Power", summary.getTrainingName());
        assertEquals(TrainingStatusEnum.COMPLETED, summary.getStatus());
        assertEquals(4500, summary.getDurationSeconds());
        assertEquals(8.5, summary.getSessionRpe());
        assertEquals("Pushed hard on bench press", summary.getNotes());

        // Working sets: 2 for bench + 1 for endurance = 3
        assertEquals(3, summary.getTotalWorkingSets());

        // Reps: 8 + 6 = 14
        assertEquals(14, summary.getTotalReps());

        // Volume: 100*8 + 105*6 = 800 + 630 = 1430.0 kg
        assertEquals(1430.0, summary.getTotalVolumeKg());

        // Per-exercise checks
        assertEquals(2, summary.getExerciseSummaries().size());

        WorkoutExerciseSummary benchSummary = summary.getExerciseSummaries().get(0);
        assertEquals(101, benchSummary.getExerciseId());
        assertEquals("Bench Press", benchSummary.getExerciseName());
        assertEquals("RESISTANCE", benchSummary.getCategory());
        assertEquals(2, benchSummary.getCompletedSets());
        assertEquals(14, benchSummary.getTotalReps());
        assertEquals(105.0, benchSummary.getTopWeightKg());
        assertEquals(1430.0, benchSummary.getVolumeKg());
        // 1RM: best of (100*(1 + 8/30) = 126.67) and (105*(1 + 6/30) = 105*1.2 = 126.0) -> 126.67
        assertEquals(126.67, benchSummary.getEstimated1RmKg());

        WorkoutExerciseSummary runSummary = summary.getExerciseSummaries().get(1);
        assertEquals(102, runSummary.getExerciseId());
        assertEquals("ENDURANCE", runSummary.getCategory());
        assertEquals(1, runSummary.getCompletedSets());
    }

    @Test
    void fromTraining_computesLiveWorkoutDuration() {
        Training training = new Training();
        training.setId(2);
        training.setName("Live Session");
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);
        training.setStartTime(LocalDateTime.now().minusMinutes(25));
        training.setEndTime(null);

        WorkoutSummary summary = WorkoutSummary.fromTraining(training);

        assertNotNull(summary);
        assertTrue(summary.getDurationSeconds() >= 1490 && summary.getDurationSeconds() <= 1510);
        assertNotNull(summary.getFormattedDuration());
    }
}
