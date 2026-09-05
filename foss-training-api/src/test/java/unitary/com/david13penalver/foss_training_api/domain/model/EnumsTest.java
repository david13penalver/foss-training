package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.EquipmentCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.model.exercise.MovementPattern;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.RecommendedTiming;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory;
import org.junit.jupiter.api.Test;

class EnumsTest {

    @Test
    void testDifficultyLevel() {
        DifficultyLevel level = DifficultyLevel.BEGINNER;
        assertEquals(1, level.getLevel());
        assertEquals("Beginner", level.getName());

        assertTrue(DifficultyLevel.INTERMEDIATE.isHarderThan(DifficultyLevel.BEGINNER));
        assertTrue(DifficultyLevel.BEGINNER.isEasierThan(DifficultyLevel.INTERMEDIATE));

        assertEquals(DifficultyLevel.ADVANCED, DifficultyLevel.fromLevel(3));
        assertEquals(DifficultyLevel.EXPERT, DifficultyLevel.fromString("Expert"));

        assertThrows(IllegalArgumentException.class, () -> DifficultyLevel.fromLevel(99));
    }

    @Test
    void testMovementPattern() {
        MovementPattern push = MovementPattern.PUSH;
        assertTrue(push.isCompound());

        MovementPattern isolation = MovementPattern.ISOLATION;
        assertFalse(isolation.isCompound());

        assertEquals(MovementPattern.SQUAT, MovementPattern.fromString("Squat"));
    }

    @Test
    void testEquipment() {
        Equipment eq = Equipment.BARBELL;
        assertNotNull(eq.getDescription());
        assertEquals("Barbell", eq.getName());

        assertEquals(Equipment.DUMBBELL, Equipment.fromString("Dumbbell"));
    }

    @Test
    void testEnduranceType() {
        EnduranceType type = EnduranceType.HIIT;
        assertTrue(type.isHighIntensity());
        assertFalse(type.isLowIntensity());

        EnduranceType liss = EnduranceType.LISS;
        assertTrue(liss.isLowIntensity());
    }

    @Test
    void testExerciseCategory() {
        ExerciseCategory resistance = ExerciseCategory.RESISTANCE;
        assertEquals("Resistance Training", resistance.getName());
        assertNotNull(resistance.getDescription());

        assertTrue(resistance.isPrimaryCategory());
        assertTrue(resistance.requiresEquipment());

        assertFalse(ExerciseCategory.CALISTHENICS.requiresEquipment());

        assertTrue(ExerciseCategory.POWER.isPerformanceOriented());
        assertTrue(ExerciseCategory.REHABILITATION.isTherapeutic());

        assertEquals(ExerciseCategory.ENDURANCE, ExerciseCategory.fromString("endurance"));
        assertThrows(IllegalArgumentException.class, () -> ExerciseCategory.fromString("invalid"));
    }

    @Test
    void testEquipmentCategory() {
        EquipmentCategory freeWeights = EquipmentCategory.FREE_WEIGHTS;
        assertEquals("Free Weights", freeWeights.getName());
    }

    @Test
    void testMobilityType() {
        MobilityType yoga = MobilityType.YOGA;
        assertEquals("Yoga", yoga.getName());
        assertNotNull(yoga.getDescription());

        assertEquals(MobilityType.STATIC_STRETCHING, MobilityType.fromString("static_stretching"));
        assertThrows(IllegalArgumentException.class, () -> MobilityType.fromString("invalid"));
    }

    @Test
    void testStretchType() {
        StretchType staticStretch = StretchType.STATIC;
        assertEquals("Static Stretch", staticStretch.getName());
        assertNotNull(staticStretch.getDescription());
        assertNotNull(staticStretch.getExamples());

        assertTrue(staticStretch.isRecommendedForCooldown());
        assertFalse(staticStretch.isRecommendedForWarmup());

        StretchType dynamic = StretchType.DYNAMIC;
        assertTrue(dynamic.isRecommendedForWarmup());

        assertEquals(StretchType.PNF, StretchType.fromString("pnf"));
        assertThrows(IllegalArgumentException.class, () -> StretchType.fromString("invalid"));
    }

    @Test
    void testJoint() {
        Joint shoulder = Joint.SHOULDER;
        assertEquals("Shoulder", shoulder.getDisplayName());
        assertNotNull(shoulder.getDescription());
    }

    @Test
    void testMuscleGroup() {
        MuscleGroup chest = MuscleGroup.CHEST;
        assertEquals("Chest", chest.getName());
        assertNotNull(chest.getDescription());
        assertEquals(MuscleCategory.UPPER_BODY, chest.getCategory());

        var upperBodyGroups = MuscleGroup.getByCategory(MuscleCategory.UPPER_BODY);
        assertFalse(upperBodyGroups.isEmpty());
        assertTrue(upperBodyGroups.contains(MuscleGroup.CHEST));
    }

    @Test
    void testRecommendedTiming() {
        RecommendedTiming preWorkout = RecommendedTiming.PRE_WORKOUT;
        assertEquals("Pre-Workout", preWorkout.getName());
        assertNotNull(preWorkout.getDescription());
        assertNotNull(preWorkout.getSuitableActivities());

        assertEquals(RecommendedTiming.POST_WORKOUT, RecommendedTiming.fromString("post_workout"));
        assertThrows(IllegalArgumentException.class, () -> RecommendedTiming.fromString("invalid"));
    }

    @Test
    void testMuscleCategory() {
        MuscleCategory upperBody = MuscleCategory.UPPER_BODY;
        assertEquals("Upper Body", upperBody.getName());
    }

    @Test
    void testEnduranceType_exhaustivePredicates() {
        for (EnduranceType t : EnduranceType.values()) {
            boolean highIntensity = t == EnduranceType.HIIT || t == EnduranceType.ANAEROBIC
                    || t == EnduranceType.SIT || t == EnduranceType.TEMPO;
            boolean lowIntensity = t == EnduranceType.LISS || t == EnduranceType.AEROBIC
                    || t == EnduranceType.STEADY_STATE;
            boolean timeEfficient = t == EnduranceType.SIT || t == EnduranceType.HIIT;

            assertEquals(highIntensity, t.isHighIntensity());
            assertEquals(lowIntensity, t.isLowIntensity());
            assertEquals(timeEfficient, t.isTimeEfficient());
            assertEquals(t, EnduranceType.fromString(t.name()));
        }
        assertThrows(IllegalArgumentException.class, () -> EnduranceType.fromString("invalid"));
    }

    @Test
    void testEnduranceType_expectsMixedOutcomes() {
        assertTrue(EnduranceType.HIIT.isHighIntensity());
        assertFalse(EnduranceType.AEROBIC.isHighIntensity());
        assertTrue(EnduranceType.LISS.isLowIntensity());
        assertFalse(EnduranceType.HIIT.isLowIntensity());
        assertTrue(EnduranceType.SIT.isTimeEfficient());
        assertFalse(EnduranceType.LISS.isTimeEfficient());
    }

    @Test
    void testExerciseCategory_exhaustivePredicates() {
        for (ExerciseCategory c : ExerciseCategory.values()) {
            boolean primary = c == ExerciseCategory.RESISTANCE || c == ExerciseCategory.ENDURANCE
                    || c == ExerciseCategory.MOBILITY;
            boolean requiresEquipment = c != ExerciseCategory.CALISTHENICS
                    && c != ExerciseCategory.BALANCE && c != ExerciseCategory.AGILITY
                    && c != ExerciseCategory.SPEED;
            boolean performance = c == ExerciseCategory.POWER || c == ExerciseCategory.PLYOMETRICS
                    || c == ExerciseCategory.SPEED || c == ExerciseCategory.AGILITY
                    || c == ExerciseCategory.SPORT_SPECIFIC;
            boolean therapeutic = c == ExerciseCategory.REHABILITATION
                    || c == ExerciseCategory.STABILIZATION || c == ExerciseCategory.PILATES;

            assertEquals(primary, c.isPrimaryCategory());
            assertEquals(requiresEquipment, c.requiresEquipment());
            assertEquals(performance, c.isPerformanceOriented());
            assertEquals(therapeutic, c.isTherapeutic());
            assertEquals(c, ExerciseCategory.fromString(c.name()));
        }
        assertThrows(IllegalArgumentException.class, () -> ExerciseCategory.fromString("invalid"));
    }

    @Test
    void testExerciseCategory_expectsMixedOutcomes() {
        assertFalse(ExerciseCategory.CALISTHENICS.isPrimaryCategory());
        assertTrue(ExerciseCategory.RESISTANCE.isPrimaryCategory());
        assertFalse(ExerciseCategory.SPEED.requiresEquipment());
        assertTrue(ExerciseCategory.RESISTANCE.requiresEquipment());
        assertFalse(ExerciseCategory.ISOMETRIC.isPerformanceOriented());
        assertTrue(ExerciseCategory.POWER.isPerformanceOriented());
        assertFalse(ExerciseCategory.POWER.isTherapeutic());
        assertTrue(ExerciseCategory.STABILIZATION.isTherapeutic());
    }

    @Test
    void testStretchType_exhaustivePredicates() {
        for (StretchType s : StretchType.values()) {
            boolean warmup = s == StretchType.DYNAMIC || s == StretchType.ACTIVE;
            boolean cooldown = s == StretchType.STATIC || s == StretchType.PNF
                    || s == StretchType.PASSIVE;

            assertEquals(warmup, s.isRecommendedForWarmup());
            assertEquals(cooldown, s.isRecommendedForCooldown());
            assertEquals(s, StretchType.fromString(s.name()));
        }
        assertThrows(IllegalArgumentException.class, () -> StretchType.fromString("invalid"));
    }

    @Test
    void testMovementPattern_exhaustivePredicates() {
        EnumSet<MovementPattern> compound = EnumSet.of(MovementPattern.PUSH, MovementPattern.PULL,
                MovementPattern.SQUAT, MovementPattern.HINGE, MovementPattern.LUNGE,
                MovementPattern.COMPOUND);

        for (MovementPattern p : MovementPattern.values()) {
            assertEquals(compound.contains(p), p.isCompound());
            assertEquals(p, MovementPattern.fromString(p.name()));
        }
        assertThrows(IllegalArgumentException.class, () -> MovementPattern.fromString("invalid"));
    }

    @Test
    void testMovementPattern_expectsMixedOutcomes() {
        assertTrue(MovementPattern.PUSH.isCompound());
        assertFalse(MovementPattern.ISOLATION.isCompound());
        assertFalse(MovementPattern.CARRY.isCompound());
    }

    @Test
    void testDifficultyLevel_exhaustive() {
        assertTrue(DifficultyLevel.BEGINNER.isEasierThan(DifficultyLevel.EXPERT));
        assertTrue(DifficultyLevel.EXPERT.isHarderThan(DifficultyLevel.BEGINNER));
        assertFalse(DifficultyLevel.BEGINNER.isHarderThan(DifficultyLevel.BEGINNER));
        assertFalse(DifficultyLevel.BEGINNER.isEasierThan(DifficultyLevel.BEGINNER));

        for (DifficultyLevel d : DifficultyLevel.values()) {
            assertEquals(d, DifficultyLevel.fromLevel(d.getLevel()));
            assertEquals(d, DifficultyLevel.fromString(d.name()));
        }
        assertThrows(IllegalArgumentException.class, () -> DifficultyLevel.fromString("invalid"));
    }

    @Test
    void testEquipment_exhaustiveRoundTrip() {
        for (Equipment e : Equipment.values()) {
            assertNotNull(e.getName());
            assertNotNull(e.getDescription());
            assertNotNull(e.getCategory());
            assertEquals(e, Equipment.fromString(e.name()));
        }
        assertThrows(IllegalArgumentException.class, () -> Equipment.fromString("invalid"));
    }
}
