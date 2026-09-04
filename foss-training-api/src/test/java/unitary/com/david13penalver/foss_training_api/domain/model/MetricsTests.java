package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.model.exercise.MovementPattern;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.RecommendedTiming;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class MetricsTests {

    @Test
    void testResistanceMetrics() {
        ResistanceMetrics metrics = new ResistanceMetrics();
        metrics.setId(1);
        metrics.setMovementPattern(MovementPattern.PUSH);
        metrics.setPrimaryMuscles(List.of(MuscleGroup.CHEST));

        assertEquals(1, metrics.getId());
        assertEquals(MovementPattern.PUSH, metrics.getMovementPattern());
        assertEquals(MuscleGroup.CHEST, metrics.getPrimaryMuscles().get(0));

        ResistanceMetrics allArgs = new ResistanceMetrics(
                2,
                List.of(MuscleGroup.QUADRICEPS),
                Collections.emptyList(),
                MovementPattern.SQUAT,
                true, false, false, "kg",
                3, 8, 12, 60, "2-0-2",
                10.0, 100.0, 2.5);
        assertEquals(MovementPattern.SQUAT, allArgs.getMovementPattern());
    }

    @Test
    void testEnduranceMetrics() {
        EnduranceMetrics metrics = new EnduranceMetrics();
        metrics.setEnduranceType(EnduranceType.AEROBIC);

        assertNotNull(metrics);
        // Assuming Enum exists or matches what was created.
        // Note: In previous step EnduranceType had AEROBIC, ANAEROBIC etc.
        // Let's use a valid one from created file.
        metrics.setEnduranceType(EnduranceType.AEROBIC);
        assertEquals(EnduranceType.AEROBIC, metrics.getEnduranceType());
    }

    @Test
    void testMobilityMetrics() {
        MobilityMetrics metrics = new MobilityMetrics();
        metrics.setMobilityType(MobilityType.YOGA);

        assertNotNull(metrics);
        assertEquals(MobilityType.YOGA, metrics.getMobilityType());
    }

    @Test
    void testResistanceMetricsConstructors() {
        ResistanceMetrics noArgs = new ResistanceMetrics();
        assertNotNull(noArgs);
        assertNull(noArgs.getId());

        ResistanceMetrics allArgs = new ResistanceMetrics(
                1, List.of(MuscleGroup.CHEST), List.of(MuscleGroup.TRICEPS), MovementPattern.PUSH,
                true, false, false, "kg", 3, 8, 12, 60, "3-0-1-0", 20.0, 200.0, 5.0);
        assertEquals(1, allArgs.getId());
        assertEquals(MovementPattern.PUSH, allArgs.getMovementPattern());
        assertTrue(allArgs.isWeighted());
        assertFalse(allArgs.isBodyweight());
        assertEquals("kg", allArgs.getDefaultWeightUnit());
        assertEquals(3, allArgs.getRecommendedSets());
        assertEquals(20.0, allArgs.getMinWeight());
    }

    @Test
    void testResistanceMetricsEqualsAndHashCode() {
        ResistanceMetrics metrics1 = new ResistanceMetrics(
                1, List.of(MuscleGroup.CHEST), Collections.emptyList(), MovementPattern.PUSH,
                true, false, false, "kg", 3, 8, 12, 60, "3-0-1-0", 20.0, 200.0, 5.0);
        ResistanceMetrics metrics2 = new ResistanceMetrics(
                1, List.of(MuscleGroup.CHEST), Collections.emptyList(), MovementPattern.PUSH,
                true, false, false, "kg", 3, 8, 12, 60, "3-0-1-0", 20.0, 200.0, 5.0);
        assertEquals(metrics1, metrics2);
        assertEquals(metrics1.hashCode(), metrics2.hashCode());

        ResistanceMetrics metrics3 = new ResistanceMetrics(
                2, List.of(MuscleGroup.CHEST), Collections.emptyList(), MovementPattern.PUSH,
                true, false, false, "kg", 3, 8, 12, 60, "3-0-1-0", 20.0, 200.0, 5.0);
        assertNotEquals(metrics1, metrics3);
    }

    @Test
    void testResistanceMetricsToString() {
        ResistanceMetrics metrics = new ResistanceMetrics();
        metrics.setId(1);
        String toString = metrics.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
    }

    @Test
    void testResistanceMetricsWithNullValues() {
        ResistanceMetrics metrics = new ResistanceMetrics();
        assertNull(metrics.getId());
        assertNull(metrics.getPrimaryMuscles());
        assertNull(metrics.getSecondaryMuscles());
        assertNull(metrics.getMovementPattern());
        assertNull(metrics.getDefaultWeightUnit());
        assertNull(metrics.getTempoRecommendation());
        assertNull(metrics.getMinWeight());
        assertNull(metrics.getMaxWeight());
        assertNull(metrics.getWeightIncrement());
        assertFalse(metrics.isWeighted());
        assertFalse(metrics.isBodyweight());
        assertFalse(metrics.isTimed());
    }

    @Test
    void testResistanceMetricsSettersForAllFields() {
        ResistanceMetrics metrics = new ResistanceMetrics();
        metrics.setId(10);
        metrics.setPrimaryMuscles(List.of(MuscleGroup.QUADRICEPS));
        metrics.setSecondaryMuscles(List.of(MuscleGroup.HAMSTRINGS));
        metrics.setMovementPattern(MovementPattern.SQUAT);
        metrics.setWeighted(true);
        metrics.setBodyweight(false);
        metrics.setTimed(false);
        metrics.setDefaultWeightUnit("lbs");
        metrics.setRecommendedSets(4);
        metrics.setRecommendedRepsMin(6);
        metrics.setRecommendedRepsMax(10);
        metrics.setRecommendedRestSeconds(90);
        metrics.setTempoRecommendation("2-0-2-0");
        metrics.setMinWeight(50.0);
        metrics.setMaxWeight(150.0);
        metrics.setWeightIncrement(10.0);

        assertEquals(10, metrics.getId());
        assertEquals(List.of(MuscleGroup.QUADRICEPS), metrics.getPrimaryMuscles());
        assertEquals(List.of(MuscleGroup.HAMSTRINGS), metrics.getSecondaryMuscles());
        assertEquals(MovementPattern.SQUAT, metrics.getMovementPattern());
        assertTrue(metrics.isWeighted());
        assertFalse(metrics.isBodyweight());
        assertFalse(metrics.isTimed());
        assertEquals("lbs", metrics.getDefaultWeightUnit());
        assertEquals(4, metrics.getRecommendedSets());
        assertEquals(6, metrics.getRecommendedRepsMin());
        assertEquals(10, metrics.getRecommendedRepsMax());
        assertEquals(90, metrics.getRecommendedRestSeconds());
        assertEquals("2-0-2-0", metrics.getTempoRecommendation());
        assertEquals(50.0, metrics.getMinWeight());
        assertEquals(150.0, metrics.getMaxWeight());
        assertEquals(10.0, metrics.getWeightIncrement());
    }

    @Test
    void testEnduranceMetricsConstructors() {
        EnduranceMetrics noArgs = new EnduranceMetrics();
        assertNotNull(noArgs);
        assertNull(noArgs.getId());

        // EnduranceMetrics has too many fields for all-args constructor, test setters instead
        EnduranceMetrics metrics = new EnduranceMetrics();
        metrics.setId(1);
        metrics.setEnduranceType(EnduranceType.HIIT);
        metrics.setBlocks(3);
        metrics.setDistanceUnit("meters");
        assertEquals(1, metrics.getId());
        assertEquals(EnduranceType.HIIT, metrics.getEnduranceType());
        assertEquals(3, metrics.getBlocks());
        assertEquals("meters", metrics.getDistanceUnit());
    }

    @Test
    void testEnduranceMetricsEqualsAndHashCode() {
        EnduranceMetrics metrics1 = new EnduranceMetrics();
        metrics1.setId(1);
        metrics1.setEnduranceType(EnduranceType.AEROBIC);

        EnduranceMetrics metrics2 = new EnduranceMetrics();
        metrics2.setId(1);
        metrics2.setEnduranceType(EnduranceType.AEROBIC);

        assertEquals(metrics1, metrics2);
        assertEquals(metrics1.hashCode(), metrics2.hashCode());

        EnduranceMetrics metrics3 = new EnduranceMetrics();
        metrics3.setId(2);
        metrics3.setEnduranceType(EnduranceType.AEROBIC);

        assertNotEquals(metrics1, metrics3);
    }

    @Test
    void testEnduranceMetricsToString() {
        EnduranceMetrics metrics = new EnduranceMetrics();
        metrics.setId(1);
        String toString = metrics.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
    }

    @Test
    void testEnduranceMetricsWithNullValues() {
        EnduranceMetrics metrics = new EnduranceMetrics();
        assertNull(metrics.getId());
        assertNull(metrics.getEnduranceType());
        assertNull(metrics.getBlocks());
        // Many nulls, but to keep it short, just check a few
        assertNull(metrics.getDistanceUnit());
        assertNull(metrics.getPaceUnit());
        assertNull(metrics.getHeartRateUnit());
        assertNull(metrics.getPowerUnit());
    }

    @Test
    void testMobilityMetricsConstructors() {
        MobilityMetrics noArgs = new MobilityMetrics();
        assertNotNull(noArgs);
        assertNull(noArgs.getId());

        MobilityMetrics allArgs = new MobilityMetrics(
                1, MobilityType.YOGA, StretchType.STATIC, List.of(Joint.HIP), "degrees",
                30, 3, 4, true, RecommendedTiming.POST_WORKOUT);
        assertEquals(1, allArgs.getId());
        assertEquals(MobilityType.YOGA, allArgs.getMobilityType());
        assertEquals(StretchType.STATIC, allArgs.getStretchType());
        assertEquals(List.of(Joint.HIP), allArgs.getTargetJoints());
        assertEquals("degrees", allArgs.getRomMeasurementMethod());
        assertEquals(30, allArgs.getRecommendedHoldTimeSeconds());
        assertEquals(3, allArgs.getRecommendedRepetitions());
        assertEquals(4, allArgs.getRecommendedSets());
        assertTrue(allArgs.isPerformBilaterally());
        assertEquals(RecommendedTiming.POST_WORKOUT, allArgs.getTiming());
    }

    @Test
    void testMobilityMetricsEqualsAndHashCode() {
        MobilityMetrics metrics1 = new MobilityMetrics(
                1, MobilityType.YOGA, StretchType.STATIC, List.of(Joint.HIP), "degrees",
                30, 3, 4, true, RecommendedTiming.POST_WORKOUT);
        MobilityMetrics metrics2 = new MobilityMetrics(
                1, MobilityType.YOGA, StretchType.STATIC, List.of(Joint.HIP), "degrees",
                30, 3, 4, true, RecommendedTiming.POST_WORKOUT);
        assertEquals(metrics1, metrics2);
        assertEquals(metrics1.hashCode(), metrics2.hashCode());

        MobilityMetrics metrics3 = new MobilityMetrics(
                2, MobilityType.YOGA, StretchType.STATIC, List.of(Joint.HIP), "degrees",
                30, 3, 4, true, RecommendedTiming.POST_WORKOUT);
        assertNotEquals(metrics1, metrics3);
    }

    @Test
    void testMobilityMetricsToString() {
        MobilityMetrics metrics = new MobilityMetrics();
        metrics.setId(1);
        String toString = metrics.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
    }

    @Test
    void testMobilityMetricsWithNullValues() {
        MobilityMetrics metrics = new MobilityMetrics();
        assertNull(metrics.getId());
        assertNull(metrics.getMobilityType());
        assertNull(metrics.getStretchType());
        assertNull(metrics.getTargetJoints());
        assertNull(metrics.getRomMeasurementMethod());
        assertNull(metrics.getRecommendedHoldTimeSeconds());
        assertNull(metrics.getRecommendedRepetitions());
        assertNull(metrics.getRecommendedSets());
        assertNull(metrics.getTiming());
        assertFalse(metrics.isPerformBilaterally());
    }

    @Test
    void testMobilityMetricsSettersForAllFields() {
        MobilityMetrics metrics = new MobilityMetrics();
        metrics.setId(5);
        metrics.setMobilityType(MobilityType.FOAM_ROLLING);
        metrics.setStretchType(StretchType.DYNAMIC);
        metrics.setTargetJoints(List.of(Joint.SHOULDER, Joint.ANKLE));
        metrics.setRomMeasurementMethod("visual scale");
        metrics.setRecommendedHoldTimeSeconds(45);
        metrics.setRecommendedRepetitions(5);
        metrics.setRecommendedSets(3);
        metrics.setPerformBilaterally(false);
        metrics.setTiming(RecommendedTiming.PRE_WORKOUT);

        assertEquals(5, metrics.getId());
        assertEquals(MobilityType.FOAM_ROLLING, metrics.getMobilityType());
        assertEquals(StretchType.DYNAMIC, metrics.getStretchType());
        assertEquals(List.of(Joint.SHOULDER, Joint.ANKLE), metrics.getTargetJoints());
        assertEquals("visual scale", metrics.getRomMeasurementMethod());
        assertEquals(45, metrics.getRecommendedHoldTimeSeconds());
        assertEquals(5, metrics.getRecommendedRepetitions());
        assertEquals(3, metrics.getRecommendedSets());
        assertFalse(metrics.isPerformBilaterally());
        assertEquals(RecommendedTiming.PRE_WORKOUT, metrics.getTiming());
    }
}
