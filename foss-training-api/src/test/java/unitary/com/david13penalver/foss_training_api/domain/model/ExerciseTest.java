package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;

class ExerciseTest {

    @Test
    void testNoArgsConstructor() {
        Exercise exercise = new Exercise();
        assertNotNull(exercise);
        assertNull(exercise.getName());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Exercise exercise = new Exercise(
                1,
                "Push Up",
                "Basic push up",
                Collections.emptyList(),
                "video_url",
                ExerciseCategory.RESISTANCE,
                Collections.emptyList(),
                null,
                null,
                null,
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                "admin",
                now,
                now,
                true,
                Collections.emptyList());

        assertEquals(1, exercise.getId());
        assertEquals("Push Up", exercise.getName());
        assertEquals(ExerciseCategory.RESISTANCE, exercise.getPrimaryCategory());
        assertEquals(DifficultyLevel.BEGINNER, exercise.getDifficultyLevel());
        assertEquals(now, exercise.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Exercise exercise = new Exercise();
        exercise.setName("Squat");
        exercise.setId(10);

        assertEquals("Squat", exercise.getName());
        assertEquals(10, exercise.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Exercise exercise1 = new Exercise(
                1, "Push Up", "Basic push up", Collections.emptyList(), "video_url",
                ExerciseCategory.RESISTANCE, Collections.emptyList(), null, null, null,
                Collections.emptyList(), DifficultyLevel.BEGINNER, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                "admin", now, now, true, Collections.emptyList());

        Exercise exercise2 = new Exercise(
                1, "Push Up", "Basic push up", Collections.emptyList(), "video_url",
                ExerciseCategory.RESISTANCE, Collections.emptyList(), null, null, null,
                Collections.emptyList(), DifficultyLevel.BEGINNER, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                "admin", now, now, true, Collections.emptyList());

        assertEquals(exercise1, exercise2);
        assertEquals(exercise1.hashCode(), exercise2.hashCode());

        Exercise exercise3 = new Exercise(
                2, "Push Up", "Basic push up", Collections.emptyList(), "video_url",
                ExerciseCategory.RESISTANCE, Collections.emptyList(), null, null, null,
                Collections.emptyList(), DifficultyLevel.BEGINNER, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                "admin", now, now, true, Collections.emptyList());

        assertNotEquals(exercise1, exercise3);
    }

    @Test
    void testToString() {
        Exercise exercise = new Exercise();
        exercise.setName("Test Exercise");
        String toString = exercise.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Exercise"));
    }

    @Test
    void testWithNullValues() {
        Exercise exercise = new Exercise();
        assertNull(exercise.getId());
        assertNull(exercise.getName());
        assertNull(exercise.getDescription());
        assertNull(exercise.getImages());
        assertNull(exercise.getVideo());
        assertNull(exercise.getPrimaryCategory());
        assertNull(exercise.getSecondaryCategories());
        assertNull(exercise.getResistanceMetrics());
        assertNull(exercise.getEnduranceMetrics());
        assertNull(exercise.getMobilityMetrics());
        assertNull(exercise.getEquipmentRequired());
        assertNull(exercise.getDifficultyLevel());
        assertNull(exercise.getStepByStepInstructions());
        assertNull(exercise.getCommonMistakes());
        assertNull(exercise.getSafetyTips());
        assertNull(exercise.getAlternativeExercises());
        assertNull(exercise.getCreatedBy());
        assertNull(exercise.getCreatedAt());
        assertNull(exercise.getUpdatedAt());
        assertNull(exercise.getTags());
        assertFalse(exercise.isActive()); // default boolean is false
    }

    @Test
    void testSettersForAllFields() {
        Exercise exercise = new Exercise();
        LocalDateTime now = LocalDateTime.now();

        exercise.setId(100);
        exercise.setName("Bench Press");
        exercise.setDescription("A compound exercise");
        exercise.setImages(List.of("image1.jpg"));
        exercise.setVideo("bench_press.mp4");
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        exercise.setSecondaryCategories(List.of(ExerciseCategory.POWER));
        exercise.setResistanceMetrics(new ResistanceMetrics());
        exercise.setEnduranceMetrics(new EnduranceMetrics());
        exercise.setMobilityMetrics(new MobilityMetrics());
        exercise.setEquipmentRequired(List.of(Equipment.BARBELL));
        exercise.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        exercise.setStepByStepInstructions(List.of("Step 1", "Step 2"));
        exercise.setCommonMistakes(List.of("Mistake 1"));
        exercise.setSafetyTips(List.of("Tip 1"));
        exercise.setAlternativeExercises(List.of("alt1"));
        exercise.setCreatedBy("user123");
        exercise.setCreatedAt(now);
        exercise.setUpdatedAt(now);
        exercise.setActive(true);
        exercise.setTags(List.of("strength", "chest"));

        assertEquals(100, exercise.getId());
        assertEquals("Bench Press", exercise.getName());
        assertEquals("A compound exercise", exercise.getDescription());
        assertEquals(List.of("image1.jpg"), exercise.getImages());
        assertEquals("bench_press.mp4", exercise.getVideo());
        assertEquals(ExerciseCategory.RESISTANCE, exercise.getPrimaryCategory());
        assertEquals(List.of(ExerciseCategory.POWER), exercise.getSecondaryCategories());
        assertNotNull(exercise.getResistanceMetrics());
        assertNotNull(exercise.getEnduranceMetrics());
        assertNotNull(exercise.getMobilityMetrics());
        assertEquals(List.of(Equipment.BARBELL), exercise.getEquipmentRequired());
        assertEquals(DifficultyLevel.INTERMEDIATE, exercise.getDifficultyLevel());
        assertEquals(List.of("Step 1", "Step 2"), exercise.getStepByStepInstructions());
        assertEquals(List.of("Mistake 1"), exercise.getCommonMistakes());
        assertEquals(List.of("Tip 1"), exercise.getSafetyTips());
        assertEquals(List.of("alt1"), exercise.getAlternativeExercises());
        assertEquals("user123", exercise.getCreatedBy());
        assertEquals(now, exercise.getCreatedAt());
        assertEquals(now, exercise.getUpdatedAt());
        assertTrue(exercise.isActive());
        assertEquals(List.of("strength", "chest"), exercise.getTags());
    }

    @Test
    void testWithEmptyLists() {
        Exercise exercise = new Exercise(
                1, "Test", "Desc", Collections.emptyList(), "url",
                ExerciseCategory.ENDURANCE, Collections.emptyList(), null, null, null,
                Collections.emptyList(), DifficultyLevel.BEGINNER, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                "admin", LocalDateTime.now(), LocalDateTime.now(), true, Collections.emptyList());

        assertTrue(exercise.getImages().isEmpty());
        assertTrue(exercise.getSecondaryCategories().isEmpty());
        assertTrue(exercise.getEquipmentRequired().isEmpty());
        assertTrue(exercise.getStepByStepInstructions().isEmpty());
        assertTrue(exercise.getCommonMistakes().isEmpty());
        assertTrue(exercise.getSafetyTips().isEmpty());
        assertTrue(exercise.getAlternativeExercises().isEmpty());
        assertTrue(exercise.getTags().isEmpty());
    }

    @Test
    void testEdgeCases() {
        Exercise exercise = new Exercise();
        // Very long name
        String longName = "A".repeat(1000);
        exercise.setName(longName);
        assertEquals(longName, exercise.getName());

        // Past date
        LocalDateTime past = LocalDateTime.of(2000, 1, 1, 0, 0);
        exercise.setCreatedAt(past);
        assertEquals(past, exercise.getCreatedAt());

        // Future date
        LocalDateTime future = LocalDateTime.of(2050, 12, 31, 23, 59);
        exercise.setUpdatedAt(future);
        assertEquals(future, exercise.getUpdatedAt());
    }

    @Test
    void testIsResistance() {
        Exercise res = new Exercise();
        res.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        assertTrue(res.isResistance());
        assertFalse(res.isEndurance());
        assertFalse(res.isMobility());
    }

    @Test
    void testIsEndurance() {
        Exercise end = new Exercise();
        end.setPrimaryCategory(ExerciseCategory.ENDURANCE);
        assertFalse(end.isResistance());
        assertTrue(end.isEndurance());
        assertFalse(end.isMobility());
    }

    @Test
    void testIsMobility() {
        Exercise mob = new Exercise();
        mob.setPrimaryCategory(ExerciseCategory.MOBILITY);
        assertFalse(mob.isResistance());
        assertFalse(mob.isEndurance());
        assertTrue(mob.isMobility());
    }

    @Test
    void testIsResistanceEnduranceMobilityWithNullCategory() {
        Exercise e = new Exercise();
        assertFalse(e.isResistance());
        assertFalse(e.isEndurance());
        assertFalse(e.isMobility());
    }

    @Test
    void testHasMatchingMetricsResistance() {
        Exercise res = new Exercise();
        res.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        assertFalse(res.hasMatchingMetrics());
        res.setResistanceMetrics(new ResistanceMetrics());
        assertTrue(res.hasMatchingMetrics());
        res.setResistanceMetrics(null);
        assertFalse(res.hasMatchingMetrics());
    }

    @Test
    void testHasMatchingMetricsEndurance() {
        Exercise end = new Exercise();
        end.setPrimaryCategory(ExerciseCategory.ENDURANCE);
        assertFalse(end.hasMatchingMetrics());
        end.setEnduranceMetrics(new EnduranceMetrics());
        assertTrue(end.hasMatchingMetrics());
    }

    @Test
    void testHasMatchingMetricsMobility() {
        Exercise mob = new Exercise();
        mob.setPrimaryCategory(ExerciseCategory.MOBILITY);
        assertFalse(mob.hasMatchingMetrics());
        mob.setMobilityMetrics(new MobilityMetrics());
        assertTrue(mob.hasMatchingMetrics());
    }

    @Test
    void testHasMatchingMetricsNullCategory() {
        Exercise e = new Exercise();
        e.setResistanceMetrics(new ResistanceMetrics());
        assertFalse(e.hasMatchingMetrics());
    }

    @Test
    void testHasMatchingMetricsNonPrimaryCategory() {
        Exercise e = new Exercise();
        e.setPrimaryCategory(ExerciseCategory.POWER);
        e.setResistanceMetrics(new ResistanceMetrics());
        assertFalse(e.hasMatchingMetrics());
    }

    @Test
    void testDeactivate() {
        Exercise e = new Exercise();
        e.setActive(true);
        assertTrue(e.isActive());
        e.deactivate();
        assertFalse(e.isActive());
    }

    @Test
    void testActivate() {
        Exercise e = new Exercise();
        e.setActive(false);
        assertFalse(e.isActive());
        e.activate();
        assertTrue(e.isActive());
    }
}
