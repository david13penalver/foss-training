package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.training;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.training.TrainingRepositoryImpl;

import org.springframework.context.annotation.Import;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@Import(TestDatabaseCleaner.class)
class TrainingRepositoryImplTest {

    @Autowired
    private TrainingRepositoryImpl trainingRepository;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
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
        assertTrue(trainingRepository.findAll().isEmpty());
    }

    @Test
    void findById_returnsSavedTraining() {
        Training saved = trainingRepository.save(buildTraining("Leg Day"));

        Optional<Training> result = trainingRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Leg Day", result.get().getName());
    }

    @Test
    void findById_returnsEmpty_whenMissingOrNull() {
        assertEquals(Optional.empty(), trainingRepository.findById(999));
        assertEquals(Optional.empty(), trainingRepository.findById(null));
    }

    @Test
    void save_assignsId() {
        Training result = trainingRepository.save(buildTraining("Push Day"));

        assertNotNull(result.getId());
        assertEquals(1, trainingRepository.findAll().size());
    }

    @Test
    void save_returnsDifferentCountAcrossSaves() {
        trainingRepository.save(buildTraining("Push Day"));
        trainingRepository.save(buildTraining("Pull Day"));

        assertEquals(2, trainingRepository.findAll().size());
    }

    @Test
    void deleteById_removesTraining() {
        Training saved = trainingRepository.save(buildTraining("Push Day"));

        trainingRepository.deleteById(saved.getId());

        assertFalse(trainingRepository.existsById(saved.getId()));
    }

    @Test
    void deleteById_doesNothing_whenUnknownOrNull() {
        assertDoesNotThrow(() -> trainingRepository.deleteById(999));
        assertDoesNotThrow(() -> trainingRepository.deleteById(null));
    }

    @Test
    void existsById_returnsFalse_whenMissingOrNull() {
        assertFalse(trainingRepository.existsById(999));
        assertFalse(trainingRepository.existsById(null));
    }

    @Test
    void existsById_returnsTrue_whenSaved() {
        Training saved = trainingRepository.save(buildTraining("Push Day"));

        assertTrue(trainingRepository.existsById(saved.getId()));
    }

    @Test
    void findAll_returnsAllSaved() {
        trainingRepository.save(buildTraining("Push Day"));
        trainingRepository.save(buildTraining("Pull Day"));

        List<Training> result = trainingRepository.findAll();

        assertEquals(2, result.size());
    }
}
