package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.services.exercise.ExerciseServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    private Exercise createTestExercise(Integer id, String name) {
        LocalDateTime now = LocalDateTime.now();
        return new Exercise(
                id, name, "Description", Collections.emptyList(), "video_url",
                ExerciseCategory.RESISTANCE, Collections.emptyList(), null, null, null,
                Collections.emptyList(), DifficultyLevel.BEGINNER, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                "admin", now, now, true, Collections.emptyList());
    }

    @Test
    void testFindAll() {
        // Given
        List<Exercise> expectedExercises = List.of(
                createTestExercise(1, "Push Up"),
                createTestExercise(2, "Squat"));
        when(exerciseRepository.findAll()).thenReturn(expectedExercises);

        // When
        List<Exercise> result = exerciseService.findAll();

        // Then
        assertEquals(expectedExercises, result);
        verify(exerciseRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        Integer id = 1;
        Exercise expectedExercise = createTestExercise(id, "Push Up");
        when(exerciseRepository.findById(id)).thenReturn(Optional.of(expectedExercise));

        // When
        Optional<Exercise> result = exerciseService.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedExercise, result.get());
        verify(exerciseRepository).findById(id);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        Integer id = 999;
        when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Exercise> result = exerciseService.findById(id);

        // Then
        assertFalse(result.isPresent());
        verify(exerciseRepository).findById(id);
    }

    @Test
    void testSave() {
        // Given
        Exercise exercise = createTestExercise(1, "Push Up");
        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        // When
        Exercise result = exerciseService.save(exercise);

        // Then
        assertEquals(exercise, result);
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        Integer id = 1;
        when(exerciseRepository.existsById(id)).thenReturn(true);
        doNothing().when(exerciseRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> exerciseService.deleteById(id));

        // Then
        verify(exerciseRepository).existsById(id);
        verify(exerciseRepository).deleteById(id);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        Integer id = 999;
        when(exerciseRepository.existsById(id)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> exerciseService.deleteById(id));
        assertEquals("Exercise not found with id: " + id, exception.getMessage());
        verify(exerciseRepository).existsById(id);
        verify(exerciseRepository, never()).deleteById(id);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        Integer id = 1;
        when(exerciseRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = exerciseService.existsById(id);

        // Then
        assertTrue(result);
        verify(exerciseRepository).existsById(id);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        Integer id = 999;
        when(exerciseRepository.existsById(id)).thenReturn(false);

        // When
        boolean result = exerciseService.existsById(id);

        // Then
        assertFalse(result);
        verify(exerciseRepository).existsById(id);
    }
}
