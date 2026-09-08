package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.TrainingProgramJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.TrainingProgramPersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionPersistenceMapper;

class TrainingProgramPersistenceMapperTest {

    private TrainingProgramPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        SessionPersistenceMapper sessionMapper = new SessionPersistenceMapper();
        mapper = new TrainingProgramPersistenceMapper(sessionMapper);
    }

    @Test
    void testNullMappings() {
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toDomain(null));
    }

    @Test
    void testFullProgramRoundTrip() {
        Session s1 = new Session();
        s1.setName("Push Day Template");
        s1.setSessionStatus(SessionStatusEnum.PLANNED);

        ProgramWorkout w1 = ProgramWorkout.builder()
                .dayOfWeek(1)
                .focus("Chest & Triceps")
                .session(s1)
                .build();

        TrainingProgram program = TrainingProgram.builder()
                .id(77)
                .name("Hypertrophy Wave")
                .description("12-week hypertrophy program")
                .durationWeeks(12)
                .periodizationType(PeriodizationType.UNDULATING)
                .level(ProgramLevel.ADVANCED)
                .workouts(List.of(w1))
                .isActive(true)
                .build();

        TrainingProgramJpaEntity entity = mapper.toJpaEntity(program);
        assertNotNull(entity);
        assertEquals(77, entity.getId());
        assertEquals("Hypertrophy Wave", entity.getName());
        assertEquals("UNDULATING", entity.getPeriodizationType());
        assertEquals("ADVANCED", entity.getLevel());
        assertTrue(entity.isActive());
        assertNotNull(entity.getWorkoutsJson());

        TrainingProgram roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals(77, roundTrip.getId());
        assertEquals("Hypertrophy Wave", roundTrip.getName());
        assertEquals(PeriodizationType.UNDULATING, roundTrip.getPeriodizationType());
        assertEquals(ProgramLevel.ADVANCED, roundTrip.getLevel());
        assertTrue(roundTrip.getIsActive());
        assertEquals(1, roundTrip.getWorkouts().size());
        assertEquals(1, roundTrip.getWorkouts().get(0).getDayOfWeek());
        assertEquals("Chest & Triceps", roundTrip.getWorkouts().get(0).getFocus());
        assertEquals("Push Day Template", roundTrip.getWorkouts().get(0).getSession().getName());
    }

    @Test
    void testProgramWithNullOptionalFields() {
        TrainingProgram program = new TrainingProgram();
        program.setName("Minimal Program");
        program.setDurationWeeks(4);
        program.setIsActive(null);
        program.setWorkouts(null);

        TrainingProgramJpaEntity entity = mapper.toJpaEntity(program);
        assertNotNull(entity);
        assertTrue(entity.isActive()); // defaults to true
        assertNull(entity.getPeriodizationType());
        assertNull(entity.getLevel());
        assertNull(entity.getWorkoutsJson());

        TrainingProgram roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals("Minimal Program", roundTrip.getName());
        assertNull(roundTrip.getPeriodizationType());
        assertNull(roundTrip.getLevel());
        assertTrue(roundTrip.getWorkouts().isEmpty());
    }

    @Test
    void testWorkoutsFromJsonErrorHandling() {
        TrainingProgramJpaEntity entity = new TrainingProgramJpaEntity();
        entity.setWorkoutsJson("invalid json [");
        assertThrows(RuntimeException.class, () -> mapper.toDomain(entity));
    }

    @Test
    void testWorkoutsToJsonErrorHandling() throws Exception {
        java.lang.reflect.Field omField = TrainingProgramPersistenceMapper.class.getDeclaredField("objectMapper");
        omField.setAccessible(true);
        omField.set(mapper, new com.fasterxml.jackson.databind.ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
                throw new com.fasterxml.jackson.core.JsonParseException(null, "forced serialization error");
            }
        });

        TrainingProgram program = new TrainingProgram();
        program.setName("Test");
        program.setWorkouts(List.of(ProgramWorkout.builder().dayOfWeek(1).focus("Arms").build()));

        assertThrows(RuntimeException.class, () -> mapper.toJpaEntity(program));
    }
}
