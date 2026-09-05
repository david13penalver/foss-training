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

import com.david13penalver.foss_training_api.application.usecases.session.impl.DeleteSessionService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.FindAllSessionsService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.FindSessionByIdService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.SaveSessionService;
import com.david13penalver.foss_training_api.application.usecases.session.impl.SessionExistsService;
import com.david13penalver.foss_training_api.domain.model.session.Session;
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
}