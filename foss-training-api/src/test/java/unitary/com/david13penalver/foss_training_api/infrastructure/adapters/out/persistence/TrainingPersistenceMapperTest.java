package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionPersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingPersistenceMapper;

class TrainingPersistenceMapperTest {

    private TrainingPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        SessionPersistenceMapper sessionMapper = new SessionPersistenceMapper();
        mapper = new TrainingPersistenceMapper(sessionMapper);
    }

    @Test
    void testNullMappings() {
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toDomain(null));
    }

    @Test
    void testFullTrainingRoundTrip() {
        Training training = new Training();
        training.setId(50);
        training.setName("Upper Body Strength");
        training.setDescription("Bench and rows");
        training.setTrainingDate(LocalDate.of(2026, 9, 8));
        training.setStartTime(LocalDateTime.of(2026, 9, 8, 7, 0));
        training.setEndTime(LocalDateTime.of(2026, 9, 8, 8, 15));
        training.setStatus(TrainingStatusEnum.COMPLETED);
        training.setNotes("Great intensity");
        training.setRpe(new Rpe(9));

        Session session = new Session();
        session.setId(10);
        session.setName("Routine A");
        session.setSessionStatus(SessionStatusEnum.COMPLETED);
        training.setSession(session);

        TrainingJpaEntity entity = mapper.toJpaEntity(training);
        assertNotNull(entity);
        assertEquals(50, entity.getId());
        assertEquals("Upper Body Strength", entity.getName());
        assertEquals("COMPLETED", entity.getStatus());
        assertEquals(9.0, entity.getRpeValue());
        assertNotNull(entity.getSessionJson());

        Training roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals(50, roundTrip.getId());
        assertEquals("Upper Body Strength", roundTrip.getName());
        assertEquals(TrainingStatusEnum.COMPLETED, roundTrip.getStatus());
        assertEquals(9.0, roundTrip.getRpe().getValue());
        assertNotNull(roundTrip.getSession());
        assertEquals("Routine A", roundTrip.getSession().getName());
    }

    @Test
    void testTrainingWithNullOptionalFields() {
        Training training = new Training();
        training.setName("Minimal Training");

        TrainingJpaEntity entity = mapper.toJpaEntity(training);
        assertNotNull(entity);
        assertNull(entity.getStatus());
        assertNull(entity.getRpeValue());
        assertNull(entity.getSessionJson());

        Training roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals("Minimal Training", roundTrip.getName());
        assertNull(roundTrip.getStatus());
        assertNull(roundTrip.getRpe());
        assertNull(roundTrip.getSession());
    }
}
