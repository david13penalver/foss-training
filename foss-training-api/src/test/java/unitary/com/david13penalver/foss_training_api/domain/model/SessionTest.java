package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionPart;
import com.david13penalver.foss_training_api.domain.model.session.SessionPartEnum;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void testSessionNoArgsConstructor() {
        Session session = new Session();
        assertNotNull(session);
        assertNull(session.getId());
        assertNull(session.getName());
        assertNull(session.getDescription());
        assertNull(session.getSessionStatus());
        assertNull(session.getSessionExercises());
        assertNull(session.getStartTime());
        assertNull(session.getEndTime());
        assertNull(session.getNotes());
        assertNull(session.getRpe());
    }

    @Test
    void testSessionAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 11, 30);
        List<SessionExercise> exercises = Collections.emptyList();
        Rpe rpe = Rpe.of(8.0);

        Session session = new Session(
                1,
                "Morning Workout",
                "Full body session",
                SessionStatusEnum.PLANNED,
                exercises,
                start,
                end,
                "Great session",
                rpe);

        assertEquals(1, session.getId());
        assertEquals("Morning Workout", session.getName());
        assertEquals("Full body session", session.getDescription());
        assertEquals(SessionStatusEnum.PLANNED, session.getSessionStatus());
        assertEquals(exercises, session.getSessionExercises());
        assertEquals(start, session.getStartTime());
        assertEquals(end, session.getEndTime());
        assertEquals("Great session", session.getNotes());
        assertEquals(rpe, session.getRpe());
    }

    @Test
    void testSessionSettersAndGetters() {
        Session session = new Session();
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 12, 0);
        Rpe rpe = Rpe.of(7.5);

        session.setId(10);
        session.setName("Evening Session");
        session.setDescription("Cardio focused");
        session.setSessionStatus(SessionStatusEnum.IN_PROGRESS);
        session.setSessionExercises(Collections.emptyList());
        session.setStartTime(start);
        session.setEndTime(end);
        session.setNotes("Intense");
        session.setRpe(rpe);

        assertEquals(10, session.getId());
        assertEquals("Evening Session", session.getName());
        assertEquals("Cardio focused", session.getDescription());
        assertEquals(SessionStatusEnum.IN_PROGRESS, session.getSessionStatus());
        assertTrue(session.getSessionExercises().isEmpty());
        assertEquals(start, session.getStartTime());
        assertEquals(end, session.getEndTime());
        assertEquals("Intense", session.getNotes());
        assertEquals(rpe, session.getRpe());
    }

    @Test
    void testSessionEqualsAndHashCode() {
        Session session1 = new Session(1, "A", "Desc", SessionStatusEnum.PLANNED, Collections.emptyList(), null, null, null, null);
        Session session2 = new Session(1, "A", "Desc", SessionStatusEnum.PLANNED, Collections.emptyList(), null, null, null, null);
        assertEquals(session1, session2);
        assertEquals(session1.hashCode(), session2.hashCode());

        Session session3 = new Session(2, "A", "Desc", SessionStatusEnum.PLANNED, Collections.emptyList(), null, null, null, null);
        assertNotEquals(session1, session3);
    }

    @Test
    void testSessionToString() {
        Session session = new Session();
        session.setName("Test Session");
        String toString = session.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Session"));
    }

    @Test
    void testSessionStartValid() {
        Session session = new Session(1, "A", null, SessionStatusEnum.PLANNED, new ArrayList<>(), null, null, null, null);
        session.start();
        assertEquals(SessionStatusEnum.IN_PROGRESS, session.getSessionStatus());
        assertNotNull(session.getStartTime());
    }

    @Test
    void testSessionStartDraft() {
        Session session = new Session(1, "A", null, SessionStatusEnum.DRAFT, new ArrayList<>(), null, null, null, null);
        session.start();
        assertEquals(SessionStatusEnum.IN_PROGRESS, session.getSessionStatus());
    }

    @Test
    void testSessionStartReady() {
        Session session = new Session(1, "A", null, SessionStatusEnum.READY, new ArrayList<>(), null, null, null, null);
        session.start();
        assertEquals(SessionStatusEnum.IN_PROGRESS, session.getSessionStatus());
    }

    @Test
    void testSessionStartInvalidFromInProgress() {
        Session session = new Session(1, "A", null, SessionStatusEnum.IN_PROGRESS, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::start);
    }

    @Test
    void testSessionStartInvalidFromCompleted() {
        Session session = new Session(1, "A", null, SessionStatusEnum.COMPLETED, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::start);
    }

    @Test
    void testSessionStartInvalidFromCancelled() {
        Session session = new Session(1, "A", null, SessionStatusEnum.CANCELLED, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::start);
    }

    @Test
    void testSessionCompleteValid() {
        Session session = new Session(1, "A", null, SessionStatusEnum.IN_PROGRESS, new ArrayList<>(), null, null, null, null);
        session.complete();
        assertEquals(SessionStatusEnum.COMPLETED, session.getSessionStatus());
        assertNotNull(session.getEndTime());
    }

    @Test
    void testSessionCompleteValidFromPaused() {
        Session session = new Session(1, "A", null, SessionStatusEnum.PAUSED, new ArrayList<>(), null, null, null, null);
        session.complete();
        assertEquals(SessionStatusEnum.COMPLETED, session.getSessionStatus());
    }

    @Test
    void testSessionCompleteInvalidFromPlanned() {
        Session session = new Session(1, "A", null, SessionStatusEnum.PLANNED, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::complete);
    }

    @Test
    void testSessionCompleteInvalidFromCancelled() {
        Session session = new Session(1, "A", null, SessionStatusEnum.CANCELLED, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::complete);
    }

    @Test
    void testSessionCancelValid() {
        Session session = new Session(1, "A", null, SessionStatusEnum.PLANNED, new ArrayList<>(), null, null, null, null);
        session.cancel();
        assertEquals(SessionStatusEnum.CANCELLED, session.getSessionStatus());
    }

    @Test
    void testSessionCancelValidFromDraft() {
        Session session = new Session(1, "A", null, SessionStatusEnum.DRAFT, new ArrayList<>(), null, null, null, null);
        session.cancel();
        assertEquals(SessionStatusEnum.CANCELLED, session.getSessionStatus());
    }

    @Test
    void testSessionCancelValidFromReady() {
        Session session = new Session(1, "A", null, SessionStatusEnum.READY, new ArrayList<>(), null, null, null, null);
        session.cancel();
        assertEquals(SessionStatusEnum.CANCELLED, session.getSessionStatus());
    }

    @Test
    void testSessionCancelValidFromPaused() {
        Session session = new Session(1, "A", null, SessionStatusEnum.PAUSED, new ArrayList<>(), null, null, null, null);
        session.cancel();
        assertEquals(SessionStatusEnum.CANCELLED, session.getSessionStatus());
    }

    @Test
    void testSessionCancelInvalidFromInProgress() {
        Session session = new Session(1, "A", null, SessionStatusEnum.IN_PROGRESS, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::cancel);
    }

    @Test
    void testSessionCancelInvalidFromCompleted() {
        Session session = new Session(1, "A", null, SessionStatusEnum.COMPLETED, new ArrayList<>(), null, null, null, null);
        assertThrows(IllegalStateException.class, session::cancel);
    }

    @Test
    void testSessionCalculateDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 11, 30);
        Session session = new Session(1, "A", null, SessionStatusEnum.PLANNED, new ArrayList<>(), start, end, null, null);

        Duration duration = session.calculateDuration();
        assertEquals(Duration.minutes(90), duration);
    }

    @Test
    void testSessionCalculateDurationWithNulls() {
        Session session = new Session();
        assertEquals(Duration.zero(), session.calculateDuration());
    }

    @Test
    void testSessionCalculateDurationWithOnlyStart() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        Session session = new Session(1, "A", null, SessionStatusEnum.PLANNED, new ArrayList<>(), start, null, null, null);
        assertEquals(Duration.zero(), session.calculateDuration());
    }

    @Test
    void testSessionCalculateTotalVolume() {
        Exercise exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Bench Press");

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(1);
        rse.setExercise(exercise);

        ResistanceSet workingSet1 = new ResistanceSet();
        workingSet1.setSetType(SetType.WORKING);
        workingSet1.setWeight(Weight.kg(100));
        workingSet1.setRepetitions(5);
        rse.addSet(workingSet1);

        ResistanceSet workingSet2 = new ResistanceSet();
        workingSet2.setSetType(SetType.WORKING);
        workingSet2.setWeight(Weight.kg(80));
        workingSet2.setRepetitions(8);
        rse.addSet(workingSet2);

        ResistanceSet warmupSet = new ResistanceSet();
        warmupSet.setSetType(SetType.WARMUP);
        warmupSet.setWeight(Weight.kg(60));
        warmupSet.setRepetitions(10);
        rse.addSet(warmupSet);

        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setId(2);

        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.setId(3);

        List<SessionExercise> exercises = new ArrayList<>();
        exercises.add(rse);
        exercises.add(ese);
        exercises.add(mse);

        Session session = new Session(1, "A", null, SessionStatusEnum.PLANNED, exercises, null, null, null, null);

        double volume = session.calculateTotalVolume();
        assertEquals(1140.0, volume);
    }

    @Test
    void testSessionCalculateTotalVolumeWithNullExercises() {
        Session session = new Session();
        assertEquals(0.0, session.calculateTotalVolume());
    }

    @Test
    void testSessionAddExercise() {
        Session session = new Session();
        SessionExercise exercise = new SessionExercise() {};
        session.addExercise(exercise);

        assertEquals(1, session.getSessionExercises().size());
        assertEquals(exercise, session.getSessionExercises().get(0));
        assertEquals(Integer.valueOf(1), exercise.getOrderIndex());
    }

    @Test
    void testSessionAddExerciseWithExistingOrderIndex() {
        Session session = new Session();
        SessionExercise exercise = new SessionExercise() {};
        exercise.setOrderIndex(5);
        session.addExercise(exercise);

        assertEquals(Integer.valueOf(5), exercise.getOrderIndex());
    }

    @Test
    void testSessionAddExerciseAutoOrderIndex() {
        Session session = new Session();
        SessionExercise ex1 = new SessionExercise() {};
        SessionExercise ex2 = new SessionExercise() {};
        session.addExercise(ex1);
        session.addExercise(ex2);

        assertEquals(Integer.valueOf(1), ex1.getOrderIndex());
        assertEquals(Integer.valueOf(2), ex2.getOrderIndex());
    }

    @Test
    void testSessionPartNoArgsConstructor() {
        SessionPart sessionPart = new SessionPart();
        assertNotNull(sessionPart);
        assertNull(sessionPart.getId());
        assertNull(sessionPart.getSession());
        assertNull(sessionPart.getSessionPartEnum());
        assertNotNull(sessionPart.getExercises());
        assertTrue(sessionPart.getExercises().isEmpty());
    }

    @Test
    void testSessionPartAllArgsConstructor() {
        Session session = new Session();
        session.setId(5);
        List<SessionExercise> exercises = new ArrayList<>();
        SessionPart sessionPart = new SessionPart(1, session, SessionPartEnum.MAIN_WORK, exercises);
        assertEquals(1, sessionPart.getId());
        assertEquals(session, sessionPart.getSession());
        assertEquals(SessionPartEnum.MAIN_WORK, sessionPart.getSessionPartEnum());
        assertEquals(exercises, sessionPart.getExercises());
    }

    @Test
    void testSessionPartSettersAndGetters() {
        Session session = new Session();
        SessionPart sessionPart = new SessionPart();
        sessionPart.setId(2);
        sessionPart.setSession(session);
        sessionPart.setSessionPartEnum(SessionPartEnum.WARMUP);

        assertEquals(2, sessionPart.getId());
        assertEquals(session, sessionPart.getSession());
        assertEquals(SessionPartEnum.WARMUP, sessionPart.getSessionPartEnum());
    }

    @Test
    void testSessionPartAddExercise() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise exercise = new SessionExercise() {};
        sessionPart.addExercise(exercise);

        assertEquals(1, sessionPart.getExercises().size());
        assertEquals(Integer.valueOf(1), exercise.getOrderIndex());
    }

    @Test
    void testSessionPartAddExerciseAutoOrderIndex() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        SessionExercise ex2 = new SessionExercise() {};
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);

        assertEquals(Integer.valueOf(1), ex1.getOrderIndex());
        assertEquals(Integer.valueOf(2), ex2.getOrderIndex());
    }

    @Test
    void testSessionPartAddExercisePreservesExistingOrderIndex() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        SessionExercise ex2 = new SessionExercise() {};
        ex2.setOrderIndex(7);
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);

        assertEquals(Integer.valueOf(1), ex1.getOrderIndex());
        assertEquals(Integer.valueOf(7), ex2.getOrderIndex());
    }

    @Test
    void testSessionPartRemoveExerciseWithNullId() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        ex1.setId(1);
        SessionExercise ex2 = new SessionExercise() {};
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);

        assertFalse(sessionPart.removeExercise(2));
        assertEquals(2, sessionPart.getExercises().size());
    }

    @Test
    void testSessionPartReorderExercisesMissingIdAndNullOrderIndex() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        ex1.setId(1);
        SessionExercise ex2 = new SessionExercise() {};
        ex2.setId(2);
        SessionExercise ex3 = new SessionExercise() {};
        ex3.setId(3);
        SessionExercise exNull = new SessionExercise() {};
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);
        sessionPart.addExercise(ex3);
        sessionPart.addExercise(exNull);

        sessionPart.reorderExercises(List.of(3, 1, 99));

        assertEquals(Integer.valueOf(1), ex3.getOrderIndex());
        assertEquals(Integer.valueOf(2), ex1.getOrderIndex());
        assertEquals(Integer.valueOf(2), ex2.getOrderIndex());
        assertEquals(Integer.valueOf(4), exNull.getOrderIndex());
        assertEquals(4, sessionPart.getExercises().size());
        assertEquals(ex3, sessionPart.getExercises().get(0));
    }

    @Test
    void testSessionPartReorderLeavesUnmatchedExerciseWithNullOrderIndexAtEnd() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        ex1.setId(1);
        SessionExercise ex2 = new SessionExercise() {};
        ex2.setId(2);
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);
        ex2.setOrderIndex(null);

        sessionPart.reorderExercises(List.of(1));

        assertEquals(Integer.valueOf(1), ex1.getOrderIndex());
        assertNull(ex2.getOrderIndex());
        assertEquals(2, sessionPart.getExercises().size());
        assertEquals(ex1, sessionPart.getExercises().get(0));
        assertEquals(ex2, sessionPart.getExercises().get(1));
    }

    @Test
    void testSessionPartRemoveExercise() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        ex1.setId(1);
        SessionExercise ex2 = new SessionExercise() {};
        ex2.setId(2);
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);

        assertTrue(sessionPart.removeExercise(1));
        assertEquals(1, sessionPart.getExercises().size());
        assertEquals(ex2, sessionPart.getExercises().get(0));
    }

    @Test
    void testSessionPartRemoveExerciseNotFound() {
        SessionPart sessionPart = new SessionPart();
        assertFalse(sessionPart.removeExercise(99));
    }

    @Test
    void testSessionPartReorderExercises() {
        SessionPart sessionPart = new SessionPart();
        SessionExercise ex1 = new SessionExercise() {};
        ex1.setId(1);
        SessionExercise ex2 = new SessionExercise() {};
        ex2.setId(2);
        SessionExercise ex3 = new SessionExercise() {};
        ex3.setId(3);
        sessionPart.addExercise(ex1);
        sessionPart.addExercise(ex2);
        sessionPart.addExercise(ex3);

        sessionPart.reorderExercises(List.of(3, 1, 2));

        assertEquals(3, sessionPart.getExercises().size());
        assertEquals(Integer.valueOf(1), sessionPart.getExercises().get(0).getOrderIndex());
        assertEquals(Integer.valueOf(2), sessionPart.getExercises().get(1).getOrderIndex());
        assertEquals(Integer.valueOf(3), sessionPart.getExercises().get(2).getOrderIndex());
    }

    @Test
    void testSessionPartGetExercisesUnmodifiable() {
        SessionPart sessionPart = new SessionPart();
        List<SessionExercise> exercises = sessionPart.getExercises();
        assertThrows(UnsupportedOperationException.class, () -> exercises.add(new SessionExercise() {}));
    }

    @Test
    void testSessionPartEqualsAndHashCode() {
        Session session = new Session();
        SessionPart part1 = new SessionPart(1, session, SessionPartEnum.ACTIVATION, new ArrayList<>());
        SessionPart part2 = new SessionPart(1, session, SessionPartEnum.ACTIVATION, new ArrayList<>());
        assertEquals(part1, part2);
        assertEquals(part1.hashCode(), part2.hashCode());

        SessionPart part3 = new SessionPart(2, session, SessionPartEnum.ACTIVATION, new ArrayList<>());
        assertNotEquals(part1, part3);
    }

    @Test
    void testSessionPartToString() {
        SessionPart sessionPart = new SessionPart();
        sessionPart.setId(3);
        String toString = sessionPart.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("3"));
    }

    @Test
    void testSessionExerciseNoArgsConstructor() {
        SessionExercise exercise = new SessionExercise() {};
        assertNotNull(exercise);
        assertNull(exercise.getId());
        assertNull(exercise.getExercise());
        assertNull(exercise.getOrderIndex());
        assertNull(exercise.getNotes());
    }

    @Test
    void testSessionExerciseAllArgsConstructor() {
        Exercise ex = new Exercise();
        ex.setId(1);
        SessionExercise exercise = new SessionExercise(1, ex, 2, "some notes") {};

        assertEquals(1, exercise.getId());
        assertEquals(ex, exercise.getExercise());
        assertEquals(Integer.valueOf(2), exercise.getOrderIndex());
        assertEquals("some notes", exercise.getNotes());
    }

    @Test
    void testSessionExerciseSettersAndGetters() {
        SessionExercise exercise = new SessionExercise() {};
        Exercise ex = new Exercise();
        ex.setId(10);

        exercise.setId(2);
        exercise.setExercise(ex);
        exercise.setOrderIndex(3);
        exercise.setNotes("some notes");

        assertEquals(2, exercise.getId());
        assertEquals(ex, exercise.getExercise());
        assertEquals(Integer.valueOf(3), exercise.getOrderIndex());
        assertEquals("some notes", exercise.getNotes());
    }

    @Test
    void testSessionExerciseEqualsAndHashCode() {
        Exercise ex = new Exercise();
        ex.setId(1);
        SessionExercise ex1 = new SessionExercise(1, ex, 1, "notes") {};
        SessionExercise ex2 = new SessionExercise(1, ex, 1, "notes") {};
        assertEquals(ex1, ex2);
        assertEquals(ex1.hashCode(), ex2.hashCode());

        SessionExercise ex3 = new SessionExercise(2, ex, 1, "notes") {};
        assertNotEquals(ex1, ex3);
    }

    @Test
    void testSessionExerciseToString() {
        SessionExercise exercise = new SessionExercise() {};
        exercise.setId(5);
        String toString = exercise.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("5"));
    }
}
