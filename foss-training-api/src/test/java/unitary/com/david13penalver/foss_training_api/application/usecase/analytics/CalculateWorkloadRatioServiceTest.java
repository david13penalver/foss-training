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

import com.david13penalver.foss_training_api.application.usecases.analytics.impl.CalculateWorkloadRatioService;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class CalculateWorkloadRatioServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private CalculateWorkloadRatioService service;

    @Test
    void execute_queriesRepositoryAndComputesWorkloadRatio() {
        LocalDate targetDate = LocalDate.of(2026, 9, 21);
        when(trainingRepository.findAll()).thenReturn(List.of());

        WorkloadRatio result = service.execute(targetDate);

        assertNotNull(result);
        assertEquals(targetDate, result.getTargetDate());
        verify(trainingRepository).findAll();
    }

    @Test
    void execute_withNullDate_defaultsToToday() {
        when(trainingRepository.findAll()).thenReturn(List.of());

        WorkloadRatio result = service.execute(null);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getTargetDate());
        verify(trainingRepository).findAll();
    }
}
