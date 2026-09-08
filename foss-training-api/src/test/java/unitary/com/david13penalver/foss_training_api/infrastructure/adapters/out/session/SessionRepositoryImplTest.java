package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.session;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.SessionRepositoryImpl;

import org.springframework.context.annotation.Import;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@Import(TestDatabaseCleaner.class)
class SessionRepositoryImplTest {

    @Autowired
    private SessionRepositoryImpl sessionRepository;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
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
    void findById_returnsEmpty_whenMissingOrNull() {
        assertEquals(Optional.empty(), sessionRepository.findById(999));
        assertEquals(Optional.empty(), sessionRepository.findById(null));
    }

    @Test
    void save_assignsId() {
        Session result = sessionRepository.save(buildSession("Push Day"));

        assertNotNull(result.getId());
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
    void deleteById_doesNothing_whenUnknownOrNull() {
        assertDoesNotThrow(() -> sessionRepository.deleteById(999));
        assertDoesNotThrow(() -> sessionRepository.deleteById(null));
    }

    @Test
    void existsById_returnsFalse_whenMissingOrNull() {
        assertFalse(sessionRepository.existsById(999));
        assertFalse(sessionRepository.existsById(null));
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
