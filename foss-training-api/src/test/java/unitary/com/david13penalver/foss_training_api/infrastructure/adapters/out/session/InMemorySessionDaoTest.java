package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.session;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.InMemorySessionDao;

class InMemorySessionDaoTest {

    private InMemorySessionDao dao;

    @BeforeEach
    void setUp() {
        dao = new InMemorySessionDao();
    }

    private Session buildSession(String name) {
        Session session = new Session();
        session.setName(name);
        session.setSessionStatus(SessionStatusEnum.PLANNED);
        return session;
    }

    @Test
    void findAll_returnsEmpty_whenNothingSaved() {
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void save_assignsId_whenIdIsNull() {
        Session session = buildSession("Push Day");

        Session saved = dao.save(session);

        assertEquals(1, saved.getId());
        assertSame(session, saved);
        assertTrue(dao.findById(1).isPresent());
    }

    @Test
    void save_incrementsId_forSequentialSaves() {
        Session first = dao.save(buildSession("Push Day"));
        Session second = dao.save(buildSession("Pull Day"));

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void save_upserts_whenIdIsPresent() {
        Session original = dao.save(buildSession("Push Day"));
        Session updated = buildSession("Push Day - Updated");
        updated.setId(original.getId());

        dao.save(updated);

        assertEquals("Push Day - Updated", dao.findById(original.getId()).orElseThrow().getName());
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void findById_returnsSavedSession() {
        Session saved = dao.save(buildSession("Leg Day"));

        Optional<Session> found = dao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Leg Day", found.get().getName());
    }

    @Test
    void findById_returnsEmpty_whenUnknown() {
        assertEquals(Optional.empty(), dao.findById(999));
    }

    @Test
    void findById_returnsEmpty_whenNull() {
        assertEquals(Optional.empty(), dao.findById(null));
    }

    @Test
    void existsById_returnsTrue_whenSaved() {
        Session saved = dao.save(buildSession("Cardio Day"));

        assertTrue(dao.existsById(saved.getId()));
    }

    @Test
    void existsById_returnsFalse_whenUnknownOrNull() {
        assertFalse(dao.existsById(999));
        assertFalse(dao.existsById(null));
    }

    @Test
    void deleteById_removesSession() {
        Session saved = dao.save(buildSession("Recovery Day"));

        dao.deleteById(saved.getId());

        assertFalse(dao.existsById(saved.getId()));
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void deleteById_doesNothing_whenUnknown() {
        assertDoesNotThrow(() -> dao.deleteById(999));
    }

    @Test
    void deleteById_doesNothing_whenNull() {
        assertDoesNotThrow(() -> dao.deleteById(null));
    }

    @Test
    void findAll_returnsAllInInsertionOrder() {
        dao.save(buildSession("Push Day"));
        dao.save(buildSession("Pull Day"));
        dao.save(buildSession("Leg Day"));

        List<Session> result = dao.findAll();

        assertEquals(3, result.size());
        assertEquals("Push Day", result.get(0).getName());
        assertEquals("Pull Day", result.get(1).getName());
        assertEquals("Leg Day", result.get(2).getName());
    }

    @Test
    void clear_removesAllAndResetsSequence() {
        dao.save(buildSession("Push Day"));

        dao.clear();

        assertTrue(dao.findAll().isEmpty());
        assertEquals(1, dao.save(buildSession("Push Day")).getId());
    }
}