package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.Exercise;
import com.david13penalver.foss_training_api.domain.model.ExerciseCategory;

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
}
