package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import org.junit.jupiter.api.Test;

class SessionExerciseMetricsTest {

    // --- ResistanceSessionExercise ---

    @Test
    void testResistanceSessionExerciseNoArgsConstructor() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        assertNotNull(rse);
        assertNull(rse.getId());
        assertNull(rse.getWeight());
        assertNull(rse.getRepetitions());
        assertNull(rse.getRpe());
        assertNull(rse.getRestSeconds());
        assertFalse(rse.isWarmup());
        assertFalse(rse.isFailure());
        assertNull(rse.getNotes());
    }

    @Test
    void testResistanceSessionExerciseAllArgsConstructor() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise(
                1, 80.0, 10, 7.5, 90, true, false, "Heavy set");
        assertEquals(1, rse.getId());
        assertEquals(80.0, rse.getWeight());
        assertEquals(10, rse.getRepetitions());
        assertEquals(7.5, rse.getRpe());
        assertEquals(90, rse.getRestSeconds());
        assertTrue(rse.isWarmup());
        assertFalse(rse.isFailure());
        assertEquals("Heavy set", rse.getNotes());
    }

    @Test
    void testResistanceSessionExerciseSetters() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(2);
        rse.setWeight(100.0);
        rse.setRepetitions(5);
        rse.setRpe(9.0);
        rse.setRestSeconds(120);
        rse.setWarmup(false);
        rse.setFailure(true);
        rse.setNotes("Max effort");

        assertEquals(2, rse.getId());
        assertEquals(100.0, rse.getWeight());
        assertEquals(5, rse.getRepetitions());
        assertEquals(9.0, rse.getRpe());
        assertEquals(120, rse.getRestSeconds());
        assertFalse(rse.isWarmup());
        assertTrue(rse.isFailure());
        assertEquals("Max effort", rse.getNotes());
    }

    @Test
    void testResistanceSessionExerciseEqualsAndHashCode() {
        ResistanceSessionExercise rse1 = new ResistanceSessionExercise(1, 80.0, 10, 7.5, 90, true, false, null);
        ResistanceSessionExercise rse2 = new ResistanceSessionExercise(1, 80.0, 10, 7.5, 90, true, false, null);
        assertEquals(rse1, rse2);
        assertEquals(rse1.hashCode(), rse2.hashCode());

        ResistanceSessionExercise rse3 = new ResistanceSessionExercise(2, 80.0, 10, 7.5, 90, true, false, null);
        assertNotEquals(rse1, rse3);
    }

    @Test
    void testResistanceSessionExerciseToString() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(1);
        String toString = rse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
    }

    // --- MobilitySessionExercise ---

    @Test
    void testMobilitySessionExerciseNoArgsConstructor() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        assertNotNull(mse);
        assertNull(mse.getDurationSeconds());
        assertNull(mse.getRepetitions());
        assertFalse(mse.isBilateral());
    }

    @Test
    void testMobilitySessionExerciseAllArgsConstructor() {
        MobilitySessionExercise mse = new MobilitySessionExercise(30, 3, true);
        assertEquals(30, mse.getDurationSeconds());
        assertEquals(3, mse.getRepetitions());
        assertTrue(mse.isBilateral());
    }

    @Test
    void testMobilitySessionExerciseSetters() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.setDurationSeconds(45);
        mse.setRepetitions(5);
        mse.setBilateral(false);

        assertEquals(45, mse.getDurationSeconds());
        assertEquals(5, mse.getRepetitions());
        assertFalse(mse.isBilateral());
    }

    @Test
    void testMobilitySessionExerciseEqualsAndHashCode() {
        MobilitySessionExercise mse1 = new MobilitySessionExercise(30, 3, true);
        MobilitySessionExercise mse2 = new MobilitySessionExercise(30, 3, true);
        assertEquals(mse1, mse2);
        assertEquals(mse1.hashCode(), mse2.hashCode());

        MobilitySessionExercise mse3 = new MobilitySessionExercise(45, 3, true);
        assertNotEquals(mse1, mse3);
    }

    @Test
    void testMobilitySessionExerciseToString() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.setDurationSeconds(30);
        String toString = mse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("30"));
    }

    // --- EnduranceSessionExercise ---

    @Test
    void testEnduranceSessionExerciseNoArgsConstructor() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        assertNotNull(ese);
        assertNull(ese.getBlocks());
        assertNull(ese.getRepetitions());
        assertNull(ese.getRestBetweenBlocks());
        assertNull(ese.getRestBetweenRepetitions());
        assertNull(ese.getTrackDistance());
        assertNull(ese.getTrackDuration());
        assertNull(ese.getTrackAverageHeartRate());
        assertNull(ese.getDistanceUnit());
    }

    @Test
    void testEnduranceSessionExerciseAllArgsConstructor() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise(
                3, 4, 60, 30,
                5.0, 600, 90,
                140, 120, 160, 150,
                95, 85, 100, 90,
                250.0, 200.0, 300.0, 275.0,
                85.0, 80.0, 90.0, 85.0,
                12.5, 11.0, 14.0, 13.0,
                0.5, 60, 90,
                130, 110, 145, 125,
                200.0, 180.0, 220.0, 210.0,
                80.0, 75.0, 85.0, 80.0,
                10.0, 9.0, 11.0, 10.0,
                "meters", "min/km", "bpm", "W");

        assertEquals(3, ese.getBlocks());
        assertEquals(4, ese.getRepetitions());
        assertEquals(60, ese.getRestBetweenBlocks());
        assertEquals(30, ese.getRestBetweenRepetitions());
        assertEquals(5.0, ese.getTrackDistance());
        assertEquals(600, ese.getTrackDuration());
        assertEquals(140, ese.getTrackAverageHeartRate());
        assertEquals(250.0, ese.getTrackAveragePower());
        assertEquals("meters", ese.getDistanceUnit());
        assertEquals("W", ese.getPowerUnit());
    }

    @Test
    void testEnduranceSessionExerciseSetters() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setBlocks(5);
        ese.setRepetitions(3);
        ese.setRestBetweenBlocks(45);
        ese.setRestBetweenRepetitions(15);
        ese.setTrackDistance(10.0);
        ese.setTrackDuration(1800);
        ese.setTrackPace(120);
        ese.setTrackAverageHeartRate(150);
        ese.setDistanceUnit("miles");
        ese.setHeartRateUnit("bpm");

        assertEquals(5, ese.getBlocks());
        assertEquals(3, ese.getRepetitions());
        assertEquals(45, ese.getRestBetweenBlocks());
        assertEquals(15, ese.getRestBetweenRepetitions());
        assertEquals(10.0, ese.getTrackDistance());
        assertEquals(1800, ese.getTrackDuration());
        assertEquals(120, ese.getTrackPace());
        assertEquals(150, ese.getTrackAverageHeartRate());
        assertEquals("miles", ese.getDistanceUnit());
        assertEquals("bpm", ese.getHeartRateUnit());
    }

    @Test
    void testEnduranceSessionExerciseRestFields() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setRestTrackDistance(1.0);
        ese.setRestTrackDuration(120);
        ese.setRestTrackPace(60);
        ese.setRestTrackAverageHeartRate(130);
        ese.setRestTrackMinHeartRate(120);
        ese.setRestTrackMaxHeartRate(140);
        ese.setRestTrackTargetHeartRate(135);
        ese.setRestTrackAveragePower(180.0);
        ese.setRestTrackMinPower(160.0);
        ese.setRestTrackMaxPower(200.0);
        ese.setRestTrackTargetPower(190.0);

        assertEquals(1.0, ese.getRestTrackDistance());
        assertEquals(120, ese.getRestTrackDuration());
        assertEquals(60, ese.getRestTrackPace());
        assertEquals(130, ese.getRestTrackAverageHeartRate());
        assertEquals(180.0, ese.getRestTrackAveragePower());
    }

    @Test
    void testEnduranceSessionExerciseCadenceAndSpeed() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setTrackAverageCadence(85.0);
        ese.setTrackMinCadence(80.0);
        ese.setTrackMaxCadence(90.0);
        ese.setTrackTargetCadence(85.0);
        ese.setTrackAverageSpeed(12.0);
        ese.setTrackMinSpeed(11.0);
        ese.setTrackMaxSpeed(13.0);
        ese.setTrackTargetSpeed(12.5);

        assertEquals(85.0, ese.getTrackAverageCadence());
        assertEquals(12.0, ese.getTrackAverageSpeed());

        ese.setRestTrackAverageCadence(80.0);
        ese.setRestTrackMinCadence(75.0);
        ese.setRestMaxCadence(85.0);
        ese.setRestTrackTargetCadence(80.0);
        ese.setRestTrackAverageSpeed(10.0);
        ese.setRestTrackMinSpeed(9.0);
        ese.setRestTrackMaxSpeed(11.0);
        ese.setRestTrackTargetSpeed(10.0);

        assertEquals(80.0, ese.getRestTrackAverageCadence());
        assertEquals(10.0, ese.getRestTrackAverageSpeed());
    }

    @Test
    void testEnduranceSessionExerciseEqualsAndHashCode() {
        EnduranceSessionExercise ese1 = new EnduranceSessionExercise();
        ese1.setBlocks(3);
        ese1.setRepetitions(4);

        EnduranceSessionExercise ese2 = new EnduranceSessionExercise();
        ese2.setBlocks(3);
        ese2.setRepetitions(4);

        assertEquals(ese1, ese2);
        assertEquals(ese1.hashCode(), ese2.hashCode());
    }

    @Test
    void testEnduranceSessionExerciseToString() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setBlocks(3);
        String toString = ese.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("3"));
    }
}
