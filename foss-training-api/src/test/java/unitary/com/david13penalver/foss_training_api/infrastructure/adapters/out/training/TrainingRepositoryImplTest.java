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
import com.david13penalver.foss_training_api.infrastructure.adapters.out.training.TrainingRepositoryImpl;

class TrainingRepositoryImplTest {

    private TrainingRepositoryImpl trainingRepository;
    private InMemoryTrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        trainingDao = new InMemoryTrainingDao();
        trainingRepository = new TrainingRepositoryImpl(trainingDao);
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
    void findById_returnsEmpty_whenMissing() {
        assertEquals(Optional.empty(), trainingRepository.findById(999));
    }

    @Test
    void save_assignsId() {
        Training result = trainingRepository.save(buildTraining("Push Day"));

        assertEquals(1, result.getId());
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
    void deleteById_doesNothing_whenUnknown() {
        assertDoesNotThrow(() -> trainingRepository.deleteById(999));
    }

    @Test
    void existsById_returnsFalse_whenMissing() {
        assertFalse(trainingRepository.existsById(999));
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
