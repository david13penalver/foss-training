package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.ExerciseRepositoryImpl;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.InMemoryExerciseDao;

class ExerciseRepositoryImplTest {

    private ExerciseRepositoryImpl exerciseRepository;
    private InMemoryExerciseDao exerciseDao;

    @BeforeEach
    void setUp() {
        exerciseDao = new InMemoryExerciseDao();
        exerciseRepository = new ExerciseRepositoryImpl(exerciseDao);
    }

    private Exercise buildExercise(String name) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);
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
    void findById_returnsEmpty_whenMissing() {
        assertEquals(Optional.empty(), exerciseRepository.findById(999));
    }

    @Test
    void save_assignsId() {
        Exercise result = exerciseRepository.save(buildExercise("Squat"));

        assertEquals(1, result.getId());
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
    void deleteById_doesNothing_whenUnknown() {
        assertDoesNotThrow(() -> exerciseRepository.deleteById(999));
    }

    @Test
    void existsById_returnsFalse_whenMissing() {
        assertFalse(exerciseRepository.existsById(999));
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