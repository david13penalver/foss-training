package unitary.com.david13penalver.foss_training_api.application.usecase.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.analytics.impl.FindPersonalRecordsByExerciseService;
import com.david13penalver.foss_training_api.application.usecases.analytics.impl.FindPersonalRecordsService;
import com.david13penalver.foss_training_api.domain.model.analytics.PersonalRecord;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class FindPersonalRecordsServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private FindPersonalRecordsService findPersonalRecordsService;

    @InjectMocks
    private FindPersonalRecordsByExerciseService findPersonalRecordsByExerciseService;

    @Test
    void testFindAllPersonalRecords() {
        Exercise exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Bench Press");

        when(exerciseRepository.findAll()).thenReturn(List.of(exercise));
        when(trainingRepository.findAll()).thenReturn(List.of());

        List<PersonalRecord> results = findPersonalRecordsService.execute();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Bench Press", results.get(0).getExerciseName());
        verify(exerciseRepository).findAll();
        verify(trainingRepository).findAll();
    }

    @Test
    void testFindByExercise_whenExists() {
        Exercise exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Bench Press");

        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(trainingRepository.findAll()).thenReturn(List.of());

        Optional<PersonalRecord> result = findPersonalRecordsByExerciseService.execute(1);

        assertTrue(result.isPresent());
        assertEquals("Bench Press", result.get().getExerciseName());
        verify(exerciseRepository).findById(1);
        verify(trainingRepository).findAll();
    }

    @Test
    void testFindByExercise_whenNotExists() {
        when(exerciseRepository.findById(999)).thenReturn(Optional.empty());

        Optional<PersonalRecord> result = findPersonalRecordsByExerciseService.execute(999);

        assertFalse(result.isPresent());
        verify(exerciseRepository).findById(999);
    }
}
