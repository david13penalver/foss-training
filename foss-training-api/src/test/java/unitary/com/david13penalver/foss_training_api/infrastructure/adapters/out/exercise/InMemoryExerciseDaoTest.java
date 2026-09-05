package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.InMemoryExerciseDao;

class InMemoryExerciseDaoTest {

    private InMemoryExerciseDao dao;

    @BeforeEach
    void setUp() {
        dao = new InMemoryExerciseDao();
    }

    private Exercise buildExercise(String name) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        return exercise;
    }

    @Test
    void findAll_returnsEmpty_whenNothingSaved() {
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void save_assignsId_whenIdIsNull() {
        Exercise exercise = buildExercise("Squat");

        Exercise saved = dao.save(exercise);

        assertEquals(1, saved.getId());
        assertSame(exercise, saved);
        assertTrue(dao.findById(1).isPresent());
    }

    @Test
    void save_incrementsId_forSequentialSaves() {
        Exercise first = dao.save(buildExercise("Squat"));
        Exercise second = dao.save(buildExercise("Bench Press"));

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void save_upserts_whenIdIsPresent() {
        Exercise original = dao.save(buildExercise("Squat"));
        Exercise updated = buildExercise("Squat - Updated");
        updated.setId(original.getId());

        dao.save(updated);

        assertEquals("Squat - Updated", dao.findById(original.getId()).orElseThrow().getName());
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void findById_returnsSavedExercise() {
        Exercise saved = dao.save(buildExercise("Deadlift"));

        Optional<Exercise> found = dao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Deadlift", found.get().getName());
    }

    @Test
    void findById_returnsEmpty_whenUnknown() {
        assertEquals(Optional.empty(), dao.findById(999));
    }

    @Test
    void findById_returnsEmpty_whenNull() {
        assertEquals(Optional.empty(), dao.findById(null));
    }

    @Test
    void existsById_returnsTrue_whenSaved() {
        Exercise saved = dao.save(buildExercise("Pull-up"));

        assertTrue(dao.existsById(saved.getId()));
    }

    @Test
    void existsById_returnsFalse_whenUnknownOrNull() {
        assertFalse(dao.existsById(999));
        assertFalse(dao.existsById(null));
    }

    @Test
    void deleteById_removesExercise() {
        Exercise saved = dao.save(buildExercise("Push Press"));

        dao.deleteById(saved.getId());

        assertFalse(dao.existsById(saved.getId()));
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void deleteById_doesNothing_whenUnknown() {
        assertDoesNotThrow(() -> dao.deleteById(999));
    }

    @Test
    void deleteById_doesNothing_whenNull() {
        assertDoesNotThrow(() -> dao.deleteById(null));
    }

    @Test
    void findAll_returnsAllInInsertionOrder() {
        dao.save(buildExercise("Squat"));
        dao.save(buildExercise("Bench Press"));
        dao.save(buildExercise("Deadlift"));

        List<Exercise> result = dao.findAll();

        assertEquals(3, result.size());
        assertEquals("Squat", result.get(0).getName());
        assertEquals("Bench Press", result.get(1).getName());
        assertEquals("Deadlift", result.get(2).getName());
    }

    @Test
    void clear_removesAllAndResetsSequence() {
        dao.save(buildExercise("Squat"));

        dao.clear();

        assertTrue(dao.findAll().isEmpty());
        assertEquals(1, dao.save(buildExercise("Squat")).getId());
    }
}