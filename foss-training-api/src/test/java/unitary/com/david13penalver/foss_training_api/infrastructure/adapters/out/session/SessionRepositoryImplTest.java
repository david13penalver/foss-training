package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.session;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.InMemorySessionDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.SessionRepositoryImpl;

class SessionRepositoryImplTest {

    private SessionRepositoryImpl sessionRepository;
    private InMemorySessionDao sessionDao;

    @BeforeEach
    void setUp() {
        sessionDao = new InMemorySessionDao();
        sessionRepository = new SessionRepositoryImpl(sessionDao);
    }

    private Session buildSession(String name) {
        Session session = new Session();
        session.setName(name);
        session.setSessionStatus(SessionStatusEnum.PLANNED);
        return session;
    }

    @Test
    void findAll_returnsEmpty_whenNothingSaved() {
        assertTrue(sessionRepository.findAll().isEmpty());
    }

    @Test
    void findById_returnsSavedSession() {
        Session saved = sessionRepository.save(buildSession("Push Day"));

        Optional<Session> result = sessionRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Push Day", result.get().getName());
    }

    @Test
    void findById_returnsEmpty_whenMissing() {
        assertEquals(Optional.empty(), sessionRepository.findById(999));
    }

    @Test
    void save_assignsId() {
        Session result = sessionRepository.save(buildSession("Push Day"));

        assertEquals(1, result.getId());
        assertEquals(1, sessionRepository.findAll().size());
    }

    @Test
    void save_returnsDifferentCountAcrossSaves() {
        sessionRepository.save(buildSession("Push Day"));
        sessionRepository.save(buildSession("Pull Day"));

        assertEquals(2, sessionRepository.findAll().size());
    }

    @Test
    void deleteById_removesSession() {
        Session saved = sessionRepository.save(buildSession("Push Day"));

        sessionRepository.deleteById(saved.getId());

        assertFalse(sessionRepository.existsById(saved.getId()));
    }

    @Test
    void deleteById_doesNothing_whenUnknown() {
        assertDoesNotThrow(() -> sessionRepository.deleteById(999));
    }

    @Test
    void existsById_returnsFalse_whenMissing() {
        assertFalse(sessionRepository.existsById(999));
    }

    @Test
    void existsById_returnsTrue_whenSaved() {
        Session saved = sessionRepository.save(buildSession("Push Day"));

        assertTrue(sessionRepository.existsById(saved.getId()));
    }

    @Test
    void findAll_returnsAllSaved() {
        sessionRepository.save(buildSession("Push Day"));
        sessionRepository.save(buildSession("Pull Day"));

        List<Session> result = sessionRepository.findAll();

        assertEquals(2, result.size());
    }
}