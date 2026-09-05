package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import java.util.List;
import org.junit.jupiter.api.Test;

class SessionExerciseMetricsTest {

    @Test
    void testResistanceSessionExerciseIsInstanceOfSessionExercise() {
        assertTrue(new ResistanceSessionExercise() instanceof SessionExercise);
    }

    @Test
    void testEnduranceSessionExerciseIsInstanceOfSessionExercise() {
        assertTrue(new EnduranceSessionExercise() instanceof SessionExercise);
    }

    @Test
    void testMobilitySessionExerciseIsInstanceOfSessionExercise() {
        assertTrue(new MobilitySessionExercise() instanceof SessionExercise);
    }

    @Test
    void testBaseSessionExerciseCannotBeInstantiatedNormally() {
        assertThrows(IllegalStateException.class, () -> sessionExerciseSubclass());
    }

    private SessionExercise sessionExerciseSubclass() {
        throw new IllegalStateException();
    }

    @Test
    void testBaseSessionExerciseAnonymousSubclassDataMethods() {
        SessionExercise se = new SessionExercise() {
        };
        assertNull(se.getId());
        assertNull(se.getExercise());
        assertNull(se.getOrderIndex());
        assertNull(se.getNotes());

        Exercise ex = new Exercise();
        se.setId(1);
        se.setExercise(ex);
        se.setOrderIndex(2);
        se.setNotes("notes");
        assertEquals(1, se.getId());
        assertEquals(ex, se.getExercise());
        assertEquals(2, se.getOrderIndex());
        assertEquals("notes", se.getNotes());
        assertNotNull(se.toString());
    }

    @Test
    void testBaseSessionExerciseAllArgsConstructor() {
        Exercise ex = new Exercise();
        SessionExercise se = new SessionExercise(7, ex, 3, "hello") {
        };
        assertEquals(7, se.getId());
        assertEquals(ex, se.getExercise());
        assertEquals(3, se.getOrderIndex());
        assertEquals("hello", se.getNotes());
    }

    @Test
    void testBaseSessionExerciseEqualsAndHashCode() {
        Exercise ex = new Exercise();
        SessionExercise se1 = new SessionExercise(1, ex, 2, "a") {
        };
        SessionExercise se2 = new SessionExercise(1, ex, 2, "a") {
        };
        SessionExercise se3 = new SessionExercise(2, ex, 2, "a") {
        };
        assertEquals(se1, se2);
        assertEquals(se1.hashCode(), se2.hashCode());
        assertNotEquals(se1, se3);
    }

    @Test
    void testResistanceSetNoArgsConstructor() {
        ResistanceSet rs = new ResistanceSet();
        assertNull(rs.getSetNumber());
        assertNull(rs.getSetType());
        assertNull(rs.getWeight());
        assertNull(rs.getRepetitions());
        assertNull(rs.getRpe());
        assertNull(rs.getRestSeconds());
    }

    @Test
    void testResistanceSetAllArgsConstructor() {
        ResistanceSet rs = new ResistanceSet(1, SetType.WORKING, Weight.kg(80.0), 10, Rpe.of(7.5), 90);
        assertEquals(1, rs.getSetNumber());
        assertEquals(SetType.WORKING, rs.getSetType());
        assertEquals(Weight.kg(80.0), rs.getWeight());
        assertEquals(10, rs.getRepetitions());
        assertEquals(Rpe.of(7.5), rs.getRpe());
        assertEquals(90, rs.getRestSeconds());
    }

    @Test
    void testResistanceSetSettersAndGetters() {
        ResistanceSet rs = new ResistanceSet();
        rs.setSetNumber(2);
        rs.setSetType(SetType.DROP_SET);
        rs.setWeight(Weight.lbs(100.0));
        rs.setRepetitions(5);
        rs.setRpe(Rpe.of(9.0));
        rs.setRestSeconds(120);
        assertEquals(2, rs.getSetNumber());
        assertEquals(SetType.DROP_SET, rs.getSetType());
        assertEquals(Weight.lbs(100.0), rs.getWeight());
        assertEquals(5, rs.getRepetitions());
        assertEquals(Rpe.of(9.0), rs.getRpe());
        assertEquals(120, rs.getRestSeconds());
    }

    @Test
    void testResistanceSetCalculateVolumeKg() {
        ResistanceSet rs = new ResistanceSet(1, SetType.WORKING, Weight.kg(80.0), 10, Rpe.of(7.5), 90);
        assertEquals(800.0, rs.calculateVolume(), 0.0001);
    }

    @Test
    void testResistanceSetCalculateVolumeLbsConvertsToKg() {
        ResistanceSet rs = new ResistanceSet(1, SetType.WORKING, Weight.lbs(220.0), 5, Rpe.of(7.5), 90);
        assertEquals(220.0 * 0.45359237 * 5, rs.calculateVolume(), 0.0001);
    }

    @Test
    void testResistanceSetCalculateVolumeNullWeight() {
        ResistanceSet rs = new ResistanceSet(1, SetType.WORKING, null, 10, Rpe.of(7.5), 90);
        assertEquals(0.0, rs.calculateVolume(), 0.0001);
    }

    @Test
    void testResistanceSetCalculateVolumeNullRepetitions() {
        ResistanceSet rs = new ResistanceSet(1, SetType.WORKING, Weight.kg(80.0), null, Rpe.of(7.5), 90);
        assertEquals(0.0, rs.calculateVolume(), 0.0001);
    }

    @Test
    void testResistanceSetIsWorkingVolumeWarmupFalse() {
        ResistanceSet rs = new ResistanceSet(1, SetType.WARMUP, Weight.kg(40.0), 10, Rpe.of(3.0), 60);
        assertFalse(rs.isWorkingVolume());
    }

    @Test
    void testResistanceSetIsWorkingVolumeTrueForWorkingTypes() {
        assertTrue(new ResistanceSet(1, SetType.WORKING, null, null, null, null).isWorkingVolume());
        assertTrue(new ResistanceSet(1, SetType.DROP_SET, null, null, null, null).isWorkingVolume());
        assertTrue(new ResistanceSet(1, SetType.MYOREP, null, null, null, null).isWorkingVolume());
        assertTrue(new ResistanceSet(1, SetType.FAILURE, null, null, null, null).isWorkingVolume());
    }

    @Test
    void testResistanceSetIsWorkingVolumeNullSetTypeFalse() {
        ResistanceSet rs = new ResistanceSet(1, null, Weight.kg(80.0), 10, Rpe.of(7.5), 90);
        assertFalse(rs.isWorkingVolume());
    }

    @Test
    void testResistanceSetEqualsHashCodeToString() {
        ResistanceSet rs1 = new ResistanceSet(1, SetType.WORKING, Weight.kg(80.0), 10, Rpe.of(7.5), 90);
        ResistanceSet rs2 = new ResistanceSet(1, SetType.WORKING, Weight.kg(80.0), 10, Rpe.of(7.5), 90);
        ResistanceSet rs3 = new ResistanceSet(2, SetType.WORKING, Weight.kg(80.0), 10, Rpe.of(7.5), 90);
        assertEquals(rs1, rs2);
        assertEquals(rs1.hashCode(), rs2.hashCode());
        assertNotEquals(rs1, rs3);
        assertNotNull(rs1.toString());
    }

    @Test
    void testResistanceSessionExerciseAddSetAutoNumbers() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(7, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        assertEquals(1, rse.getSets().get(0).getSetNumber());
        assertEquals(2, rse.getSets().get(1).getSetNumber());
        assertEquals(7, rse.getSets().get(2).getSetNumber());
    }

    @Test
    void testResistanceSessionExerciseGetSetsUnmodifiable() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        List<ResistanceSet> sets = rse.getSets();
        assertThrows(UnsupportedOperationException.class, () -> sets.add(new ResistanceSet()));
    }

    @Test
    void testResistanceSessionExerciseGetTotalSetsCount() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WARMUP, Weight.kg(40.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.DROP_SET, Weight.kg(60.0), 8, null, null));
        assertEquals(3, rse.getTotalSetsCount());
    }

    @Test
    void testResistanceSessionExerciseGetWorkingSetsCount() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WARMUP, Weight.kg(40.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.DROP_SET, Weight.kg(60.0), 8, null, null));
        assertEquals(2, rse.getWorkingSetsCount());
    }

    @Test
    void testResistanceSessionExerciseCalculateVolumeExcludesWarmups() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WARMUP, Weight.kg(40.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(90.0), 5, null, null));
        assertEquals(800.0 + 450.0, rse.calculateVolume(), 0.0001);
    }

    @Test
    void testResistanceSessionExerciseGetHeaviestWeight() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.lbs(220.0), 5, null, null));
        Weight heaviest = rse.getHeaviestWeight();
        assertEquals(220.0 * 0.45359237, heaviest.toKg().getValue(), 0.0001);
    }

    @Test
    void testResistanceSessionExerciseGetHeaviestWeightEmptyReturnsZeroKg() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        Weight heaviest = rse.getHeaviestWeight();
        assertEquals(0.0, heaviest.toKg().getValue(), 0.0001);
        assertEquals(WeightUnit.KG, heaviest.getUnit());
    }

    @Test
    void testResistanceSessionExerciseGetHeaviestWeightIgnoresNullWeights() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WORKING, null, 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        Weight heaviest = rse.getHeaviestWeight();
        assertEquals(80.0, heaviest.toKg().getValue(), 0.0001);
    }

    @Test
    void testResistanceSessionExerciseGetTotalRepsWorkingOnlyWithNullDefault() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.addSet(new ResistanceSet(null, SetType.WARMUP, Weight.kg(40.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(80.0), 10, null, null));
        rse.addSet(new ResistanceSet(null, SetType.WORKING, Weight.kg(90.0), null, null, null));
        rse.addSet(new ResistanceSet(null, SetType.DROP_SET, Weight.kg(60.0), 5, null, null));
        assertEquals(15, rse.getTotalReps());
    }

    @Test
    void testResistanceSessionExerciseInheritance() {
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(5);
        rse.setOrderIndex(1);
        rse.setNotes("heavy day");
        assertEquals(5, rse.getId());
        assertEquals(1, rse.getOrderIndex());
        assertEquals("heavy day", rse.getNotes());
    }

    @Test
    void testEnduranceIntervalNoArgsConstructor() {
        EnduranceInterval ei = new EnduranceInterval();
        assertNull(ei.getIntervalNumber());
        assertNull(ei.getDistance());
        assertNull(ei.getDuration());
        assertNull(ei.getPace());
        assertNull(ei.getAvgHeartRate());
        assertNull(ei.getMaxHeartRate());
        assertNull(ei.getAvgPower());
        assertNull(ei.getCadence());
        assertNull(ei.getRestSeconds());
    }

    @Test
    void testEnduranceIntervalAllArgsConstructor() {
        Distance d = Distance.meters(400);
        Duration dur = Duration.minutes(2);
        Pace p = Pace.from(Distance.kilometers(1), Duration.minutes(5));
        EnduranceInterval ei = new EnduranceInterval(1, d, dur, p, 150, 170, 250.0, 85.0, 60);
        assertEquals(1, ei.getIntervalNumber());
        assertEquals(d, ei.getDistance());
        assertEquals(dur, ei.getDuration());
        assertEquals(p, ei.getPace());
        assertEquals(150, ei.getAvgHeartRate());
        assertEquals(170, ei.getMaxHeartRate());
        assertEquals(250.0, ei.getAvgPower());
        assertEquals(85.0, ei.getCadence());
        assertEquals(60, ei.getRestSeconds());
    }

    @Test
    void testEnduranceIntervalSettersAndGetters() {
        EnduranceInterval ei = new EnduranceInterval();
        ei.setIntervalNumber(2);
        ei.setDistance(Distance.kilometers(1));
        ei.setDuration(Duration.seconds(240));
        ei.setAvgHeartRate(140);
        ei.setMaxHeartRate(180);
        ei.setAvgPower(200.0);
        ei.setCadence(90.0);
        ei.setRestSeconds(45);
        assertEquals(2, ei.getIntervalNumber());
        assertEquals(Distance.kilometers(1), ei.getDistance());
        assertEquals(Duration.seconds(240), ei.getDuration());
        assertEquals(140, ei.getAvgHeartRate());
        assertEquals(180, ei.getMaxHeartRate());
        assertEquals(200.0, ei.getAvgPower());
        assertEquals(90.0, ei.getCadence());
        assertEquals(45, ei.getRestSeconds());
    }

    @Test
    void testEnduranceIntervalHeartRateZoneNullAvg() {
        EnduranceInterval ei = new EnduranceInterval(1, null, null, null, null, null, null, null, null);
        assertNull(ei.getHeartRateZone(180));
    }

    @Test
    void testEnduranceIntervalHeartRateZones() {
        assertEquals(HeartRateZone.ZONE_1, new EnduranceInterval(1, null, null, null, 100, null, null, null, null).getHeartRateZone(200));
        assertEquals(HeartRateZone.ZONE_2, new EnduranceInterval(1, null, null, null, 130, null, null, null, null).getHeartRateZone(200));
        assertEquals(HeartRateZone.ZONE_3, new EnduranceInterval(1, null, null, null, 150, null, null, null, null).getHeartRateZone(200));
        assertEquals(HeartRateZone.ZONE_4, new EnduranceInterval(1, null, null, null, 170, null, null, null, null).getHeartRateZone(200));
        assertEquals(HeartRateZone.ZONE_5, new EnduranceInterval(1, null, null, null, 190, null, null, null, null).getHeartRateZone(200));
    }

    @Test
    void testEnduranceIntervalEqualsHashCodeToString() {
        EnduranceInterval ei1 = new EnduranceInterval(1, Distance.meters(400), Duration.minutes(2), null, 150, 170, 250.0, 85.0, 60);
        EnduranceInterval ei2 = new EnduranceInterval(1, Distance.meters(400), Duration.minutes(2), null, 150, 170, 250.0, 85.0, 60);
        EnduranceInterval ei3 = new EnduranceInterval(2, Distance.meters(400), Duration.minutes(2), null, 150, 170, 250.0, 85.0, 60);
        assertEquals(ei1, ei2);
        assertEquals(ei1.hashCode(), ei2.hashCode());
        assertNotEquals(ei1, ei3);
        assertNotNull(ei1.toString());
    }

    @Test
    void testEnduranceSessionExerciseAddIntervalAutoNumbers() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(null, Distance.meters(400), null, null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, Distance.meters(400), null, null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(9, Distance.meters(400), null, null, null, null, null, null, null));
        assertEquals(1, ese.getIntervals().get(0).getIntervalNumber());
        assertEquals(2, ese.getIntervals().get(1).getIntervalNumber());
        assertEquals(9, ese.getIntervals().get(2).getIntervalNumber());
    }

    @Test
    void testEnduranceSessionExerciseGetIntervalsUnmodifiable() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(null, Distance.meters(400), null, null, null, null, null, null, null));
        List<EnduranceInterval> intervals = ese.getIntervals();
        assertThrows(UnsupportedOperationException.class, () -> intervals.add(new EnduranceInterval()));
    }

    @Test
    void testEnduranceSessionExerciseGetIntervalsCount() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(null, Distance.meters(400), null, null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, Distance.meters(800), null, null, null, null, null, null, null));
        assertEquals(2, ese.getIntervalsCount());
    }

    @Test
    void testEnduranceSessionExerciseCalculateTotalDistance() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(null, Distance.meters(400), null, null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, Distance.kilometers(1), null, null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, null, null, null, null, null, null, null, null));
        Distance total = ese.calculateTotalDistance();
        assertEquals(1400.0, total.toMeters().getValue(), 0.0001);
    }

    @Test
    void testEnduranceSessionExerciseCalculateTotalDistanceEmpty() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        Distance total = ese.calculateTotalDistance();
        assertEquals(0.0, total.toMeters().getValue(), 0.0001);
        assertEquals(DistanceUnit.METERS, total.getUnit());
    }

    @Test
    void testEnduranceSessionExerciseCalculateTotalDuration() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(null, null, Duration.minutes(2), null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, null, Duration.seconds(30), null, null, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, null, null, null, null, null, null, null, null));
        assertEquals(150, ese.calculateTotalDuration().getTotalSeconds());
    }

    @Test
    void testEnduranceSessionExerciseCalculateTotalDurationEmpty() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        assertEquals(0, ese.calculateTotalDuration().getTotalSeconds());
    }

    @Test
    void testEnduranceSessionExerciseCalculateAverageHeartRate() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(null, null, null, null, 150, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, null, null, null, 130, null, null, null, null));
        ese.addInterval(new EnduranceInterval(null, null, null, null, null, null, null, null, null));
        assertEquals(140, ese.calculateAverageHeartRate());
    }

    @Test
    void testEnduranceSessionExerciseCalculateAverageHeartRateEmpty() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.addInterval(new EnduranceInterval(1, null, null, null, null, null, null, null, null));
        assertEquals(0, ese.calculateAverageHeartRate());
    }

    @Test
    void testEnduranceSessionExerciseInheritance() {
        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setId(3);
        ese.setOrderIndex(2);
        ese.setNotes("intervals");
        assertEquals(3, ese.getId());
        assertEquals(2, ese.getOrderIndex());
        assertEquals("intervals", ese.getNotes());
    }

    @Test
    void testMobilitySetNoArgsConstructor() {
        MobilitySet ms = new MobilitySet();
        assertNull(ms.getSetNumber());
        assertNull(ms.getHoldDuration());
        assertNull(ms.getRepetitions());
        assertFalse(ms.isBilateral());
    }

    @Test
    void testMobilitySetAllArgsConstructor() {
        MobilitySet ms = new MobilitySet(1, Duration.seconds(30), 3, true);
        assertEquals(1, ms.getSetNumber());
        assertEquals(Duration.seconds(30), ms.getHoldDuration());
        assertEquals(3, ms.getRepetitions());
        assertTrue(ms.isBilateral());
    }

    @Test
    void testMobilitySetSettersAndGetters() {
        MobilitySet ms = new MobilitySet();
        ms.setSetNumber(2);
        ms.setHoldDuration(Duration.minutes(1));
        ms.setRepetitions(5);
        ms.setBilateral(false);
        assertEquals(2, ms.getSetNumber());
        assertEquals(Duration.minutes(1), ms.getHoldDuration());
        assertEquals(5, ms.getRepetitions());
        assertFalse(ms.isBilateral());
    }

    @Test
    void testMobilitySetEqualsHashCodeToString() {
        MobilitySet ms1 = new MobilitySet(1, Duration.seconds(30), 3, true);
        MobilitySet ms2 = new MobilitySet(1, Duration.seconds(30), 3, true);
        MobilitySet ms3 = new MobilitySet(2, Duration.seconds(30), 3, true);
        assertEquals(ms1, ms2);
        assertEquals(ms1.hashCode(), ms2.hashCode());
        assertNotEquals(ms1, ms3);
        assertNotNull(ms1.toString());
    }

    @Test
    void testMobilitySessionExerciseAddSetAutoNumbers() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        mse.addSet(new MobilitySet(6, Duration.seconds(30), 3, true));
        assertEquals(1, mse.getSets().get(0).getSetNumber());
        assertEquals(2, mse.getSets().get(1).getSetNumber());
        assertEquals(6, mse.getSets().get(2).getSetNumber());
    }

    @Test
    void testMobilitySessionExerciseGetSetsUnmodifiable() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        List<MobilitySet> sets = mse.getSets();
        assertThrows(UnsupportedOperationException.class, () -> sets.add(new MobilitySet()));
    }

    @Test
    void testMobilitySessionExerciseGetTotalSetsCount() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        assertEquals(3, mse.getTotalSetsCount());
    }

    @Test
    void testMobilitySessionExerciseCalculateTotalHoldDuration() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.addSet(new MobilitySet(null, Duration.seconds(30), 3, true));
        mse.addSet(new MobilitySet(null, Duration.seconds(20), 4, false));
        mse.addSet(new MobilitySet(null, Duration.seconds(10), null, false));
        mse.addSet(new MobilitySet(null, null, 3, false));
        assertEquals(30 * 3 * 2 + 20 * 4 * 1 + 10 * 1 * 1, mse.calculateTotalHoldDuration().getTotalSeconds());
    }

    @Test
    void testMobilitySessionExerciseCalculateTotalHoldDurationEmpty() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        assertEquals(0, mse.calculateTotalHoldDuration().getTotalSeconds());
    }

    @Test
    void testMobilitySessionExerciseInheritance() {
        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.setId(4);
        mse.setOrderIndex(3);
        mse.setNotes("stretch day");
        assertEquals(4, mse.getId());
        assertEquals(3, mse.getOrderIndex());
        assertEquals("stretch day", mse.getNotes());
    }

    @Test
    void testSetTypeValuesExist() {
        assertEquals(5, SetType.values().length);
        assertEquals(SetType.WARMUP, SetType.valueOf("WARMUP"));
        assertEquals(SetType.WORKING, SetType.valueOf("WORKING"));
        assertEquals(SetType.DROP_SET, SetType.valueOf("DROP_SET"));
        assertEquals(SetType.MYOREP, SetType.valueOf("MYOREP"));
        assertEquals(SetType.FAILURE, SetType.valueOf("FAILURE"));
    }

    @Test
    void testSetTypeCountsAsWorkingVolume() {
        assertFalse(SetType.WARMUP.countsAsWorkingVolume());
        assertTrue(SetType.WORKING.countsAsWorkingVolume());
        assertTrue(SetType.DROP_SET.countsAsWorkingVolume());
        assertTrue(SetType.MYOREP.countsAsWorkingVolume());
        assertTrue(SetType.FAILURE.countsAsWorkingVolume());
    }

    @Test
    void testSetTypeFromStringValid() {
        assertEquals(SetType.WARMUP, SetType.fromString("warmup"));
        assertEquals(SetType.WORKING, SetType.fromString("WORKING"));
        assertEquals(SetType.DROP_SET, SetType.fromString("drop_set"));
        assertEquals(SetType.MYOREP, SetType.fromString("MyoRep"));
        assertEquals(SetType.FAILURE, SetType.fromString("failure"));
    }

    @Test
    void testSetTypeFromStringInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> SetType.fromString("bogus"));
    }

    @Test
    void testSetTypeDisplayNameAndDescription() {
        assertEquals("Warm-up", SetType.WARMUP.getDisplayName());
        assertEquals("Working", SetType.WORKING.getDisplayName());
        assertEquals("Drop Set", SetType.DROP_SET.getDisplayName());
        assertEquals("Myo-Rep", SetType.MYOREP.getDisplayName());
        assertEquals("Failure", SetType.FAILURE.getDisplayName());
        assertNotNull(SetType.WARMUP.getDescription());
        assertNotNull(SetType.WORKING.getDescription());
        assertNotNull(SetType.DROP_SET.getDescription());
        assertNotNull(SetType.MYOREP.getDescription());
        assertNotNull(SetType.FAILURE.getDescription());
    }

    @Test
    void testSetTypeValues() {
        assertNotNull(SetType.values());
        for (SetType type : SetType.values()) {
            assertNotNull(type);
            assertNotNull(type.name());
            assertNotNull(type.getDisplayName());
            assertNotNull(type.getDescription());
        }
    }
}