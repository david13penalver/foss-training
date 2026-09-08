package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.training;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.training.InMemoryTrainingDao;

class InMemoryTrainingDaoTest {

    private InMemoryTrainingDao dao;

    @BeforeEach
    void setUp() {
        dao = new InMemoryTrainingDao();
    }

    private Training buildTraining(String name) {
        Training training = new Training();
        training.setName(name);
        training.setTrainingDate(LocalDate.now());
        training.setStatus(TrainingStatusEnum.PLANNED);
        return training;
    }

    @Test
    void findAll_returnsEmpty_whenNothingSaved() {
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void save_assignsId_whenIdIsNull() {
        Training training = buildTraining("Morning Workout");

        Training saved = dao.save(training);

        assertEquals(1, saved.getId());
        assertSame(training, saved);
        assertTrue(dao.findById(1).isPresent());
    }

    @Test
    void save_incrementsId_forSequentialSaves() {
        Training first = dao.save(buildTraining("Workout 1"));
        Training second = dao.save(buildTraining("Workout 2"));

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void save_upserts_whenIdIsPresent() {
        Training original = dao.save(buildTraining("Workout 1"));
        Training updated = buildTraining("Workout 1 - Updated");
        updated.setId(original.getId());

        dao.save(updated);

        assertEquals("Workout 1 - Updated", dao.findById(original.getId()).orElseThrow().getName());
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void findById_returnsSavedTraining() {
        Training saved = dao.save(buildTraining("Leg Training"));

        Optional<Training> found = dao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Leg Training", found.get().getName());
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
        Training saved = dao.save(buildTraining("Cardio Session"));

        assertTrue(dao.existsById(saved.getId()));
    }

    @Test
    void existsById_returnsFalse_whenUnknownOrNull() {
        assertFalse(dao.existsById(999));
        assertFalse(dao.existsById(null));
    }

    @Test
    void deleteById_removesTraining() {
        Training saved = dao.save(buildTraining("HIIT"));

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
        dao.save(buildTraining("Push Day"));
        dao.save(buildTraining("Pull Day"));
        dao.save(buildTraining("Leg Day"));

        List<Training> result = dao.findAll();

        assertEquals(3, result.size());
        assertEquals("Push Day", result.get(0).getName());
        assertEquals("Pull Day", result.get(1).getName());
        assertEquals("Leg Day", result.get(2).getName());
    }

    @Test
    void clear_removesAllAndResetsSequence() {
        dao.save(buildTraining("Workout"));

        dao.clear();

        assertTrue(dao.findAll().isEmpty());
        assertEquals(1, dao.save(buildTraining("Workout")).getId());
    }

    @Test
    void save_withExplicitId_updatesSequenceToPreventCollision() {
        Training explicit = buildTraining("Heavy Training");
        explicit.setId(10);
        dao.save(explicit);

        Training nextAuto = dao.save(buildTraining("Morning Training"));
        assertEquals(11, nextAuto.getId());
    }

    @Test
    void save_withSmallerExplicitId_doesNotLowerSequence() {
        Training first = dao.save(buildTraining("Day 1"));
        Training second = dao.save(buildTraining("Day 2"));
        assertEquals(2, second.getId());

        Training explicit = buildTraining("Day 1 Update");
        explicit.setId(1);
        dao.save(explicit);

        Training nextAuto = dao.save(buildTraining("Day 3"));
        assertEquals(3, nextAuto.getId());
    }
}
