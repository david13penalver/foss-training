package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class TrainingTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Training training = new Training();
        assertNull(training.getId());
        assertNull(training.getName());
        assertNull(training.getDescription());
        assertNull(training.getSession());
        assertNull(training.getTrainingDate());
        assertNull(training.getStartTime());
        assertNull(training.getEndTime());
        assertNull(training.getStatus());
        assertNull(training.getNotes());
        assertNull(training.getRpe());

        Session session = new Session();
        LocalDate date = LocalDate.of(2026, 9, 8);
        LocalDateTime start = LocalDateTime.of(2026, 9, 8, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 8, 11, 0);
        Rpe rpe = new Rpe(8.5);

        training.setId(1);
        training.setName("Morning Workout");
        training.setDescription("Full body session");
        training.setSession(session);
        training.setTrainingDate(date);
        training.setStartTime(start);
        training.setEndTime(end);
        training.setStatus(TrainingStatusEnum.PLANNED);
        training.setNotes("Felt great");
        training.setRpe(rpe);

        assertEquals(1, training.getId());
        assertEquals("Morning Workout", training.getName());
        assertEquals("Full body session", training.getDescription());
        assertSame(session, training.getSession());
        assertEquals(date, training.getTrainingDate());
        assertEquals(start, training.getStartTime());
        assertEquals(end, training.getEndTime());
        assertEquals(TrainingStatusEnum.PLANNED, training.getStatus());
        assertEquals("Felt great", training.getNotes());
        assertEquals(rpe, training.getRpe());
    }

    @Test
    void testAllArgsConstructor() {
        Session session = new Session();
        LocalDate date = LocalDate.of(2026, 9, 8);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Rpe rpe = new Rpe(7.0);

        Training training = new Training(1, "Leg Day", "Heavy squats", session, date, start, end,
                TrainingStatusEnum.PLANNED, "Good", rpe);

        assertEquals(1, training.getId());
        assertEquals("Leg Day", training.getName());
        assertEquals("Heavy squats", training.getDescription());
        assertSame(session, training.getSession());
        assertEquals(date, training.getTrainingDate());
        assertEquals(start, training.getStartTime());
        assertEquals(end, training.getEndTime());
        assertEquals(TrainingStatusEnum.PLANNED, training.getStatus());
        assertEquals("Good", training.getNotes());
        assertEquals(rpe, training.getRpe());
    }

    @Test
    void start_fromPlanned_succeeds() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PLANNED);

        training.start();

        assertEquals(TrainingStatusEnum.IN_PROGRESS, training.getStatus());
        assertNotNull(training.getStartTime());
        assertNotNull(training.getTrainingDate());
    }

    @Test
    void start_fromNullStatus_initializesAndStarts() {
        Training training = new Training();
        LocalDate explicitDate = LocalDate.of(2026, 1, 1);
        training.setTrainingDate(explicitDate);

        training.start();

        assertEquals(TrainingStatusEnum.IN_PROGRESS, training.getStatus());
        assertNotNull(training.getStartTime());
        assertEquals(explicitDate, training.getTrainingDate());
    }

    @Test
    void start_fromCompleted_throwsIllegalStateException() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.COMPLETED);

        assertThrows(IllegalStateException.class, training::start);
    }

    @Test
    void complete_fromInProgress_succeeds() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);

        training.complete();

        assertEquals(TrainingStatusEnum.COMPLETED, training.getStatus());
        assertNotNull(training.getEndTime());
    }

    @Test
    void complete_fromPaused_succeeds() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PAUSED);

        training.complete();

        assertEquals(TrainingStatusEnum.COMPLETED, training.getStatus());
        assertNotNull(training.getEndTime());
    }

    @Test
    void complete_fromPlannedOrNull_throwsIllegalStateException() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PLANNED);
        assertThrows(IllegalStateException.class, training::complete);

        training.setStatus(null);
        assertThrows(IllegalStateException.class, training::complete);
    }

    @Test
    void cancel_fromPlanned_succeeds() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PLANNED);

        training.cancel();

        assertEquals(TrainingStatusEnum.CANCELLED, training.getStatus());
    }

    @Test
    void cancel_fromPaused_succeeds() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PAUSED);

        training.cancel();

        assertEquals(TrainingStatusEnum.CANCELLED, training.getStatus());
    }

    @Test
    void cancel_fromCompletedOrNull_throwsIllegalStateException() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.COMPLETED);
        assertThrows(IllegalStateException.class, training::cancel);

        training.setStatus(null);
        assertThrows(IllegalStateException.class, training::cancel);
    }

    @Test
    void calculateDuration_returnsZero_whenTimesNull() {
        Training training = new Training();
        assertEquals(Duration.zero(), training.calculateDuration());

        training.setStartTime(LocalDateTime.now());
        assertEquals(Duration.zero(), training.calculateDuration());

        training.setStartTime(null);
        training.setEndTime(LocalDateTime.now());
        assertEquals(Duration.zero(), training.calculateDuration());
    }

    @Test
    void calculateDuration_returnsDifference() {
        Training training = new Training();
        LocalDateTime start = LocalDateTime.of(2026, 9, 8, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 8, 11, 15, 30);
        training.setStartTime(start);
        training.setEndTime(end);

        Duration duration = training.calculateDuration();
        assertEquals(4530, duration.getTotalSeconds());
    }

    @Test
    void calculateTotalVolume_delegatesToSession() {
        Training training = new Training();
        assertEquals(0.0, training.calculateTotalVolume());

        Session session = new Session();
        Exercise squat = new Exercise();
        squat.setName("Squat");
        squat.setPrimaryCategory(ExerciseCategory.RESISTANCE);

        ResistanceSessionExercise rExercise = new ResistanceSessionExercise();
        rExercise.setExercise(squat);
        rExercise.setSets(List.of(
                new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, new Rpe(8.0), null)
        ));
        session.setSessionExercises(List.of(rExercise));

        training.setSession(session);
        assertEquals(500.0, training.calculateTotalVolume());
    }

    @Test
    void testEqualsAndHashCodeAndToString() {
        Training t1 = new Training();
        t1.setId(1);
        t1.setName("Workout A");

        Training t2 = new Training();
        t2.setId(1);
        t2.setName("Workout A");

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertTrue(t1.toString().contains("Workout A"));
    }
}
