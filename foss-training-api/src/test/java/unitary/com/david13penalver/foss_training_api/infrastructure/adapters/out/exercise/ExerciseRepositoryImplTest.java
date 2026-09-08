package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.ExerciseRepositoryImpl;

import org.springframework.context.annotation.Import;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@Import(TestDatabaseCleaner.class)
class ExerciseRepositoryImplTest {

    @Autowired
    private ExerciseRepositoryImpl exerciseRepository;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
    }

    private Exercise buildExercise(String name) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        exercise.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        exercise.setActive(true);
        return exercise;
    }

    @Test
    void findAll_returnsEmpty_whenNothingSaved() {
        assertTrue(exerciseRepository.findAll().isEmpty());
    }

    @Test
    void findById_returnsSavedExercise() {
        Exercise saved = exerciseRepository.save(buildExercise("Squat"));

        Optional<Exercise> result = exerciseRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Squat", result.get().getName());
    }

    @Test
    void findById_returnsEmpty_whenMissingOrNull() {
        assertEquals(Optional.empty(), exerciseRepository.findById(999));
        assertEquals(Optional.empty(), exerciseRepository.findById(null));
    }

    @Test
    void save_assignsId() {
        Exercise result = exerciseRepository.save(buildExercise("Squat"));

        assertNotNull(result.getId());
        assertEquals(1, exerciseRepository.findAll().size());
    }

    @Test
    void save_returnsDifferentCountAcrossSaves() {
        exerciseRepository.save(buildExercise("Squat"));
        exerciseRepository.save(buildExercise("Bench Press"));

        assertEquals(2, exerciseRepository.findAll().size());
    }

    @Test
    void deleteById_removesExercise() {
        Exercise saved = exerciseRepository.save(buildExercise("Squat"));

        exerciseRepository.deleteById(saved.getId());

        assertFalse(exerciseRepository.existsById(saved.getId()));
    }

    @Test
    void deleteById_doesNothing_whenUnknownOrNull() {
        assertDoesNotThrow(() -> exerciseRepository.deleteById(999));
        assertDoesNotThrow(() -> exerciseRepository.deleteById(null));
    }

    @Test
    void existsById_returnsFalse_whenMissingOrNull() {
        assertFalse(exerciseRepository.existsById(999));
        assertFalse(exerciseRepository.existsById(null));
    }

    @Test
    void existsById_returnsTrue_whenSaved() {
        Exercise saved = exerciseRepository.save(buildExercise("Squat"));

        assertTrue(exerciseRepository.existsById(saved.getId()));
    }

    @Test
    void findAll_returnsAllSaved() {
        exerciseRepository.save(buildExercise("Squat"));
        exerciseRepository.save(buildExercise("Bench Press"));

        List<Exercise> result = exerciseRepository.findAll();

        assertEquals(2, result.size());
    }
}
