package unitary.com.david13penalver.foss_training_api.application.usecase.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.analytics.impl.GetWeeklyMuscleVolumeService;
import com.david13penalver.foss_training_api.domain.model.analytics.WeeklyMuscleVolume;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class GetWeeklyMuscleVolumeServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private GetWeeklyMuscleVolumeService service;

    @Test
    void execute_queriesRepositoriesAndReturnsWeeklyMuscleVolume() {
        LocalDate start = LocalDate.of(2026, 9, 15);
        LocalDate end = LocalDate.of(2026, 9, 21);

        when(exerciseRepository.findAll()).thenReturn(List.of());
        when(trainingRepository.findAll()).thenReturn(List.of());

        WeeklyMuscleVolume result = service.execute(start, end);

        assertNotNull(result);
        assertEquals(start, result.getStartDate());
        assertEquals(end, result.getEndDate());
        verify(exerciseRepository).findAll();
        verify(trainingRepository).findAll();
    }

    @Test
    void execute_withNullDates_defaultsToRollingWeek() {
        when(exerciseRepository.findAll()).thenReturn(List.of());
        when(trainingRepository.findAll()).thenReturn(List.of());

        WeeklyMuscleVolume result = service.execute(null, null);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getEndDate());
        assertEquals(LocalDate.now().minusDays(6), result.getStartDate());
        verify(exerciseRepository).findAll();
        verify(trainingRepository).findAll();
    }
}
