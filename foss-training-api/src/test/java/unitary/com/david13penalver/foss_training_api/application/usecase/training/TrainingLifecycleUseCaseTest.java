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
import com.david13penalver.foss_training_api.application.usecases.training.impl.DeleteTrainingIntervalService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.DeleteTrainingSetService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.GetWorkoutSummaryService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.LogTrainingIntervalService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.LogTrainingSetService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.PauseTrainingService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.ResumeTrainingService;
import com.david13penalver.foss_training_api.application.usecases.training.impl.StartTrainingService;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.model.training.WorkoutSummary;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TrainingLifecycleUseCaseTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private StartTrainingService startTrainingService;

    @InjectMocks
    private PauseTrainingService pauseTrainingService;

    @InjectMocks
    private ResumeTrainingService resumeTrainingService;

    @InjectMocks
    private CompleteTrainingService completeTrainingService;

    @InjectMocks
    private CancelTrainingService cancelTrainingService;

    @InjectMocks
    private CreateTrainingFromSessionService createTrainingFromSessionService;

    @InjectMocks
    private LogTrainingSetService logTrainingSetService;

    @InjectMocks
    private DeleteTrainingSetService deleteTrainingSetService;

    @InjectMocks
    private LogTrainingIntervalService logTrainingIntervalService;

    @InjectMocks
    private DeleteTrainingIntervalService deleteTrainingIntervalService;

    @InjectMocks
    private GetWorkoutSummaryService getWorkoutSummaryService;

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

    @Test
    void testPauseTraining_WhenInProgress() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = pauseTrainingService.execute(1);

        assertSame(training, result);
        assertEquals(TrainingStatusEnum.PAUSED, result.getStatus());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testResumeTraining_WhenPaused() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.PAUSED);
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = resumeTrainingService.execute(1);

        assertSame(training, result);
        assertEquals(TrainingStatusEnum.IN_PROGRESS, result.getStatus());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testCompleteTraining_WithRpeAndNotes() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = completeTrainingService.execute(1, 8.5, "Strong finish");

        assertSame(training, result);
        assertEquals(TrainingStatusEnum.COMPLETED, result.getStatus());
        assertEquals(8.5, result.getRpe().getValue());
        assertEquals("Strong finish", result.getNotes());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testLogTrainingSet_WhenActive() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);

        Exercise squat = new Exercise();
        squat.setId(20);
        squat.setName("Squat");

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(50);
        rse.setExercise(squat);

        Session session = new Session();
        session.setSessionExercises(List.of(rse));
        training.setSession(session);

        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        ResistanceSet set = new ResistanceSet(null, SetType.WORKING, Weight.kg(100.0), 5, null, 120);
        Training result = logTrainingSetService.execute(1, 20, set);

        assertSame(training, result);
        assertEquals(1, rse.getSets().size());
        assertEquals(1, rse.getSets().get(0).getSetNumber());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testDeleteTrainingSet_WhenActive() {
        Training training = new Training();
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);

        Exercise squat = new Exercise();
        squat.setId(20);
        squat.setName("Squat");

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(50);
        rse.setExercise(squat);
        rse.addSet(new ResistanceSet(1, SetType.WORKING, Weight.kg(100.0), 5, null, 120));

        Session session = new Session();
        session.setSessionExercises(List.of(rse));
        training.setSession(session);

        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = deleteTrainingSetService.execute(1, 20, 1);

        assertSame(training, result);
        assertEquals(0, rse.getSets().size());
        verify(trainingRepository).findById(1);
        verify(trainingRepository).save(training);
    }

    @Test
    void testGetWorkoutSummary_ReturnsSummary() {
        Training training = new Training();
        training.setId(1);
        training.setName("Live Push");
        training.setStatus(TrainingStatusEnum.IN_PROGRESS);

        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));

        WorkoutSummary summary = getWorkoutSummaryService.execute(1);

        assertNotNull(summary);
        assertEquals(1, summary.getTrainingId());
        assertEquals("Live Push", summary.getTrainingName());
        verify(trainingRepository).findById(1);
    }
}
