package unitary.com.david13penalver.foss_training_api.application.usecase.training;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.training.impl.DeleteTrainingService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.FindAllTrainingsService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.FindTrainingByIdService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.SaveTrainingService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.TrainingExistsService;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class TrainingUseCaseTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private FindAllTrainingsService findAllTrainingsService;

    @InjectMocks
    private FindTrainingByIdService findTrainingByIdService;

    @InjectMocks
    private SaveTrainingService saveTrainingService;

    @InjectMocks
    private DeleteTrainingService deleteTrainingService;

    @InjectMocks
    private TrainingExistsService trainingExistsService;

    @InjectMocks
    private com.david13penalver.foss_training_api.application.usecases.training.impl.SearchTrainingsService searchTrainingsService;

    @Test
    void testSearchTrainings_withCriteria() {
        var criteria = new com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria(
                null, null, null, "Workout", null);
        Training training = new Training();
        training.setName("Test Workout");
        when(trainingRepository.findByCriteria(criteria)).thenReturn(List.of(training));

        List<Training> result = searchTrainingsService.execute(criteria);

        assertEquals(1, result.size());
        assertEquals("Test Workout", result.get(0).getName());
        verify(trainingRepository).findByCriteria(criteria);
    }

    @Test
    void testSearchTrainings_withCriteriaAndPageQuery() {
        var criteria = com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria.empty();
        var pageQuery = com.david13penalver.foss_training_api.domain.model.common.PageQuery.of(0, 10);
        Training training = new Training();
        training.setName("Test Workout");
        var pagedResult = new com.david13penalver.foss_training_api.domain.model.common.PagedResult<>(
                List.of(training), 0, 10, 1, 1);
        when(trainingRepository.findByCriteria(criteria, pageQuery)).thenReturn(pagedResult);

        var result = searchTrainingsService.execute(criteria, pageQuery);

        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(trainingRepository).findByCriteria(criteria, pageQuery);
    }

    @Test
    void testFindAll() {
        List<Training> expected = List.of(new Training());
        when(trainingRepository.findAll()).thenReturn(expected);

        List<Training> result = findAllTrainingsService.execute();

        assertSame(expected, result);
        verify(trainingRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        Training expected = new Training();
        when(trainingRepository.findById(1)).thenReturn(Optional.of(expected));

        Optional<Training> result = findTrainingByIdService.execute(1);

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(trainingRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists() {
        when(trainingRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Training> result = findTrainingByIdService.execute(999);

        assertFalse(result.isPresent());
        verify(trainingRepository).findById(999);
    }

    @Test
    void testSave() {
        Training training = new Training();
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = saveTrainingService.execute(training);

        assertSame(training, result);
        verify(trainingRepository).save(training);
    }

    @Test
    void testDeleteById_WhenExists() {
        when(trainingRepository.existsById(1)).thenReturn(true);
        doNothing().when(trainingRepository).deleteById(1);

        assertDoesNotThrow(() -> deleteTrainingService.execute(1));

        verify(trainingRepository).existsById(1);
        verify(trainingRepository).deleteById(1);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        when(trainingRepository.existsById(999)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> deleteTrainingService.execute(999));
        assertEquals("Training not found with id: 999", exception.getMessage());
        verify(trainingRepository).existsById(999);
        verify(trainingRepository, never()).deleteById(999);
    }

    @Test
    void testExistsById() {
        when(trainingRepository.existsById(1)).thenReturn(true);

        boolean result = trainingExistsService.execute(1);

        assertTrue(result);
        verify(trainingRepository).existsById(1);
    }
}
