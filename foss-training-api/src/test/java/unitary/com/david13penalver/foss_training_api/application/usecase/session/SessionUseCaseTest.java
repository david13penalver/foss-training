package unitary.com.david13penalver.foss_training_api.application.usecase.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.session.impl.CloneSessionService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.DeleteSessionService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.FindAllSessionsService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.FindSessionByIdService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.SaveSessionService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.SessionExistsService;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SessionUseCaseTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private FindAllSessionsService findAllSessionsService;

    @InjectMocks
    private FindSessionByIdService findSessionByIdService;

    @InjectMocks
    private SaveSessionService saveSessionService;

    @InjectMocks
    private DeleteSessionService deleteSessionService;

    @InjectMocks
    private SessionExistsService sessionExistsService;

    @InjectMocks
    private CloneSessionService cloneSessionService;

    @Test
    void testFindAll() {
        List<Session> expected = List.of(new Session());
        when(sessionRepository.findAll()).thenReturn(expected);

        List<Session> result = findAllSessionsService.execute();

        assertSame(expected, result);
        verify(sessionRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        Session expected = new Session();
        when(sessionRepository.findById(1)).thenReturn(Optional.of(expected));

        Optional<Session> result = findSessionByIdService.execute(1);

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(sessionRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists() {
        when(sessionRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Session> result = findSessionByIdService.execute(999);

        assertFalse(result.isPresent());
        verify(sessionRepository).findById(999);
    }

    @Test
    void testSave() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = saveSessionService.execute(session);

        assertSame(session, result);
        verify(sessionRepository).save(session);
    }

    @Test
    void testDeleteById_WhenExists() {
        when(sessionRepository.existsById(1)).thenReturn(true);
        doNothing().when(sessionRepository).deleteById(1);

        assertDoesNotThrow(() -> deleteSessionService.execute(1));

        verify(sessionRepository).existsById(1);
        verify(sessionRepository).deleteById(1);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        when(sessionRepository.existsById(999)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> deleteSessionService.execute(999));
        assertEquals("Session not found with id: 999", exception.getMessage());
        verify(sessionRepository).existsById(999);
        verify(sessionRepository, never()).deleteById(999);
    }

    @Test
    void testExistsById() {
        when(sessionRepository.existsById(1)).thenReturn(true);

        boolean result = sessionExistsService.execute(1);

        assertTrue(result);
        verify(sessionRepository).existsById(1);
    }

    @Test
    void testCloneSession_withDefaultName_shouldAppendCopy() {
        Session original = new Session();
        original.setId(1);
        original.setName("Push Day Template");
        original.setDescription("Heavy chest and tri");
        original.setSessionStatus(SessionStatusEnum.COMPLETED);

        when(sessionRepository.findById(1)).thenReturn(Optional.of(original));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> {
            Session s = inv.getArgument(0);
            s.setId(10);
            return s;
        });

        Session cloned = cloneSessionService.execute(1, null);

        assertNotNull(cloned);
        assertEquals(10, cloned.getId());
        assertEquals("Push Day Template (Copy)", cloned.getName());
        assertEquals("Heavy chest and tri", cloned.getDescription());
        assertEquals(SessionStatusEnum.PLANNED, cloned.getSessionStatus());
        assertNull(cloned.getStartTime());
        assertNull(cloned.getEndTime());
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void testCloneSession_withCustomName_shouldUseCustomName() {
        Session original = new Session();
        original.setId(2);
        original.setName("Upper Body");

        when(sessionRepository.findById(2)).thenReturn(Optional.of(original));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        Session cloned = cloneSessionService.execute(2, "Upper Body - Variation B");

        assertEquals("Upper Body - Variation B", cloned.getName());
    }

    @Test
    void testCloneSession_deepCopiesAllExerciseTypes() {
        Exercise ex = new Exercise();
        ex.setId(100);
        ex.setName("Bench Press");

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setExercise(ex);
        rse.setOrderIndex(1);
        rse.setNotes("Pause reps");
        ResistanceSet set = new ResistanceSet();
        set.setSetNumber(1);
        set.setRepetitions(8);
        set.setCompleted(true);
        rse.setSets(List.of(set));

        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setExercise(ex);
        ese.setOrderIndex(2);
        EnduranceInterval interval = new EnduranceInterval();
        interval.setIntervalNumber(1);
        ese.setIntervals(List.of(interval));

        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.setExercise(ex);
        mse.setOrderIndex(3);
        MobilitySet mSet = new MobilitySet();
        mSet.setSetNumber(1);
        mSet.setRepetitions(10);
        mse.setSets(List.of(mSet));

        Session original = new Session();
        original.setId(3);
        original.setName("Full Workout");
        original.setSessionExercises(List.of(rse, ese, mse));

        when(sessionRepository.findById(3)).thenReturn(Optional.of(original));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        Session cloned = cloneSessionService.execute(3, null);

        assertNotNull(cloned.getSessionExercises());
        assertEquals(3, cloned.getSessionExercises().size());

        ResistanceSessionExercise clonedRse = (ResistanceSessionExercise) cloned.getSessionExercises().get(0);
        assertEquals(1, clonedRse.getOrderIndex());
        assertEquals("Pause reps", clonedRse.getNotes());
        assertEquals(1, clonedRse.getSets().size());
        assertEquals(8, clonedRse.getSets().get(0).getRepetitions());
        assertFalse(clonedRse.getSets().get(0).isCompleted()); // Reset completed flag!

        EnduranceSessionExercise clonedEse = (EnduranceSessionExercise) cloned.getSessionExercises().get(1);
        assertEquals(2, clonedEse.getOrderIndex());
        assertEquals(1, clonedEse.getIntervals().size());

        MobilitySessionExercise clonedMse = (MobilitySessionExercise) cloned.getSessionExercises().get(2);
        assertEquals(3, clonedMse.getOrderIndex());
        assertEquals(1, clonedMse.getSets().size());
        assertEquals(10, clonedMse.getSets().get(0).getRepetitions());
    }

    @Test
    void testCloneSession_notFound_throwsException() {
        when(sessionRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cloneSessionService.execute(999, null));
        assertEquals("Session not found with id: 999", ex.getMessage());
    }

    @Test
    void testCloneSession_nullId_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cloneSessionService.execute(null, null));
        assertEquals("Session ID must not be null", ex.getMessage());
    }
}