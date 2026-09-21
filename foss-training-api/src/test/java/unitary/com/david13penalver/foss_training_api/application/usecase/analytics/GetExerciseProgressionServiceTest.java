package unitary.com.david13penalver.foss_training_api.application.usecase.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.analytics.impl.GetExerciseProgressionService;
import com.david13penalver.foss_training_api.domain.model.analytics.ExerciseProgression;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class GetExerciseProgressionServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private GetExerciseProgressionService service;

    @Test
    void execute_whenExerciseIdNull_returnsEmpty() {
        Optional<ExerciseProgression> result = service.execute(null, null, null, null);
        assertTrue(result.isEmpty());
        verifyNoInteractions(exerciseRepository);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void execute_whenExerciseNotFound_returnsEmpty() {
        when(exerciseRepository.findById(999)).thenReturn(Optional.empty());

        Optional<ExerciseProgression> result = service.execute(999, null, null, null);

        assertTrue(result.isEmpty());
        verify(exerciseRepository).findById(999);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void execute_whenExerciseFound_queriesTrainingsAndReturnsProgression() {
        Exercise exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Bench Press");
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);

        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(trainingRepository.findAll()).thenReturn(List.of());

        LocalDate start = LocalDate.of(2026, 8, 1);
        LocalDate end = LocalDate.of(2026, 9, 1);

        Optional<ExerciseProgression> result = service.execute(1, start, end, OneRepMaxFormula.EPLEY);

        assertTrue(result.isPresent());
        ExerciseProgression progression = result.get();
        assertEquals(1, progression.getExerciseId());
        assertEquals("Bench Press", progression.getExerciseName());
        assertEquals(OneRepMaxFormula.EPLEY, progression.getFormula());
        assertEquals(start, progression.getStartDate());
        assertEquals(end, progression.getEndDate());
        assertEquals(0, progression.getTotalSessions());
        verify(exerciseRepository).findById(1);
        verify(trainingRepository).findAll();
    }
}
