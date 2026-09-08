package unitary.com.david13penalver.foss_training_api.application.usecase.training;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.training.impl.CancelTrainingService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.CompleteTrainingService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.CreateTrainingFromSessionService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.StartTrainingService;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class TrainingLifecycleUseCaseTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private StartTrainingService startTrainingService;

    @InjectMocks
    private CompleteTrainingService completeTrainingService;

    @InjectMocks
    private CancelTrainingService cancelTrainingService;

    @InjectMocks
    private CreateTrainingFromSessionService createTrainingFromSessionService;

    @Test
    void testStartTraining_WhenExists() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PLANNED);
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = startTrainingService.execute(1);

        assertSame(training, result);
        assertEquals(TrainingStatusEnum.IN_PROGRESS, result.getStatus());
        assertNotNull(result.getStartTime());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testStartTraining_WhenNotExists() {
        when(trainingRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> startTrainingService.execute(999));
        assertEquals("Training not found with id: 999", ex.getMessage());
        verify(trainingRepository).findById(999);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCompleteTraining_WhenExists() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = completeTrainingService.execute(1);

        assertSame(training, result);
        assertEquals(TrainingStatusEnum.COMPLETED, result.getStatus());
        assertNotNull(result.getEndTime());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testCompleteTraining_WhenNotExists() {
        when(trainingRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> completeTrainingService.execute(999));
        assertEquals("Training not found with id: 999", ex.getMessage());
        verify(trainingRepository).findById(999);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCancelTraining_WhenExists() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PLANNED);
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = cancelTrainingService.execute(1);

        assertSame(training, result);
        assertEquals(TrainingStatusEnum.CANCELLED, result.getStatus());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testCancelTraining_WhenNotExists() {
        when(trainingRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cancelTrainingService.execute(999));
        assertEquals("Training not found with id: 999", ex.getMessage());
        verify(trainingRepository).findById(999);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateFromSession_WithCustomNameAndDate() {
        Session session = new Session();
        session.setId(5);
        session.setName("Leg Routine");
        session.setDescription("Heavy squats");

        LocalDate date = LocalDate.of(2026, 9, 15);
        when(sessionRepository.findById(5)).thenReturn(Optional.of(session));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training result = createTrainingFromSessionService.execute(5, date, "Custom Leg Workout");

        assertNotNull(result);
        assertEquals("Custom Leg Workout", result.getName());
        assertEquals("Heavy squats", result.getDescription());
        assertSame(session, result.getSession());
        assertEquals(date, result.getTrainingDate());
        assertEquals(TrainingStatusEnum.PLANNED, result.getStatus());
        verify(sessionRepository).findById(5);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testCreateFromSession_WithNullDateAndBlankCustomName() {
        Session session = new Session();
        session.setId(5);
        session.setName("Leg Routine");
        session.setDescription("Heavy squats");

        when(sessionRepository.findById(5)).thenReturn(Optional.of(session));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training result = createTrainingFromSessionService.execute(5, null, "   ");

        assertNotNull(result);
        assertEquals("Leg Routine", result.getName());
        assertEquals("Heavy squats", result.getDescription());
        assertSame(session, result.getSession());
        assertEquals(LocalDate.now(), result.getTrainingDate());
        assertEquals(TrainingStatusEnum.PLANNED, result.getStatus());
        verify(sessionRepository).findById(5);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testCreateFromSession_WhenSessionNotExists() {
        when(sessionRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createTrainingFromSessionService.execute(999, null, null));
        assertEquals("Session not found with id: 999", ex.getMessage());
        verify(sessionRepository).findById(999);
        verify(trainingRepository, never()).save(any());
    }
}
