package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionPart;
import com.david13penalver.foss_training_api.domain.model.session.SessionPartEnum;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import org.junit.jupiter.api.Test;

class SessionTest {

    // --- Session ---

    @Test
    void testSessionNoArgsConstructor() {
        Session session = new Session();
        assertNotNull(session);
        assertNull(session.getId());
        assertNull(session.getName());
        assertNull(session.getSessionStatus());
        assertNull(session.getSessionExercises());
    }

    @Test
    void testSessionAllArgsConstructor() {
        List<SessionExercise> exercises = Collections.emptyList();
        Session session = new Session(
                1,
                "Morning Workout",
                "Full body session",
                SessionStatusEnum.PLANNED,
                exercises);

        assertEquals(1, session.getId());
        assertEquals("Morning Workout", session.getName());
        assertEquals("Full body session", session.getDescription());
        assertEquals(SessionStatusEnum.PLANNED, session.getSessionStatus());
        assertEquals(exercises, session.getSessionExercises());
    }

    @Test
    void testSessionSettersAndGetters() {
        Session session = new Session();
        session.setId(10);
        session.setName("Evening Session");
        session.setDescription("Cardio focused");
        session.setSessionStatus(SessionStatusEnum.IN_PROGRESS);
        session.setSessionExercises(Collections.emptyList());

        assertEquals(10, session.getId());
        assertEquals("Evening Session", session.getName());
        assertEquals("Cardio focused", session.getDescription());
        assertEquals(SessionStatusEnum.IN_PROGRESS, session.getSessionStatus());
        assertTrue(session.getSessionExercises().isEmpty());
    }

    @Test
    void testSessionEqualsAndHashCode() {
        Session session1 = new Session(1, "A", "Desc", SessionStatusEnum.PLANNED, Collections.emptyList());
        Session session2 = new Session(1, "A", "Desc", SessionStatusEnum.PLANNED, Collections.emptyList());
        assertEquals(session1, session2);
        assertEquals(session1.hashCode(), session2.hashCode());

        Session session3 = new Session(2, "A", "Desc", SessionStatusEnum.PLANNED, Collections.emptyList());
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

    // --- SessionPart ---

    @Test
    void testSessionPartNoArgsConstructor() {
        SessionPart sessionPart = new SessionPart();
        assertNotNull(sessionPart);
        assertNull(sessionPart.getId());
        assertNull(sessionPart.getSession());
        assertNull(sessionPart.getSessionPartEnum());
    }

    @Test
    void testSessionPartAllArgsConstructor() {
        Session session = new Session();
        session.setId(5);
        SessionPart sessionPart = new SessionPart(1, session, SessionPartEnum.MAIN_WORK);
        assertEquals(1, sessionPart.getId());
        assertEquals(session, sessionPart.getSession());
        assertEquals(SessionPartEnum.MAIN_WORK, sessionPart.getSessionPartEnum());
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
    void testSessionPartEqualsAndHashCode() {
        Session session = new Session();
        SessionPart part1 = new SessionPart(1, session, SessionPartEnum.ACTIVATION);
        SessionPart part2 = new SessionPart(1, session, SessionPartEnum.ACTIVATION);
        assertEquals(part1, part2);
        assertEquals(part1.hashCode(), part2.hashCode());

        SessionPart part3 = new SessionPart(2, session, SessionPartEnum.ACTIVATION);
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

    // --- SessionExercise (abstract) ---

    @Test
    void testSessionExerciseFields() {
        SessionExercise exercise = new SessionExercise(1, null, null, null, null, null, null, null) {};
        assertEquals(1, exercise.getId());
        assertNull(exercise.getSessionPart());
        assertNull(exercise.getSession());
        assertNull(exercise.getExercise());
        assertNull(exercise.getNotes());
        assertNull(exercise.getResistanceSessionExercise());
        assertNull(exercise.getEnduranceSessionExercise());
        assertNull(exercise.getMobilitySessionExercise());
    }

    @Test
    void testSessionExerciseSetters() {
        SessionExercise exercise = new SessionExercise() {};
        Session session = new Session();
        SessionPart sessionPart = new SessionPart();
        Exercise ex = new Exercise();
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        MobilitySessionExercise mse = new MobilitySessionExercise();

        exercise.setId(2);
        exercise.setSessionPart(sessionPart);
        exercise.setSession(session);
        exercise.setExercise(ex);
        exercise.setNotes("some notes");
        exercise.setResistanceSessionExercise(rse);
        exercise.setEnduranceSessionExercise(ese);
        exercise.setMobilitySessionExercise(mse);

        assertEquals(2, exercise.getId());
        assertEquals(sessionPart, exercise.getSessionPart());
        assertEquals(session, exercise.getSession());
        assertEquals(ex, exercise.getExercise());
        assertEquals("some notes", exercise.getNotes());
        assertEquals(rse, exercise.getResistanceSessionExercise());
        assertEquals(ese, exercise.getEnduranceSessionExercise());
        assertEquals(mse, exercise.getMobilitySessionExercise());
    }

    @Test
    void testSessionExerciseEqualsAndHashCode() {
        SessionExercise ex1 = new SessionExercise(1, null, null, null, null, null, null, null) {};
        SessionExercise ex2 = new SessionExercise(1, null, null, null, null, null, null, null) {};
        assertEquals(ex1, ex2);
        assertEquals(ex1.hashCode(), ex2.hashCode());

        SessionExercise ex3 = new SessionExercise(2, null, null, null, null, null, null, null) {};
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
