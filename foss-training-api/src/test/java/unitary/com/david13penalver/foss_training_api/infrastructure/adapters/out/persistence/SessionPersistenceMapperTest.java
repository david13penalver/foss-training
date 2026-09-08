package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionPersistenceMapper;

class SessionPersistenceMapperTest {

    private SessionPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SessionPersistenceMapper();
    }

    @Test
    void testNullMappings() {
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toDomain(null));
        assertNull(mapper.sessionToJson(null));
        assertNull(mapper.sessionFromJson(null));
        assertNull(mapper.sessionFromJson(""));
        assertNull(mapper.sessionFromJson("   "));
    }

    @Test
    void testSessionWithAllPolymorphicExercisesRoundTrip() {
        Session session = new Session();
        session.setId(100);
        session.setName("Full Hybrid Session");
        session.setDescription("Complete session with all 3 types");
        session.setSessionStatus(SessionStatusEnum.IN_PROGRESS);
        session.setStartTime(LocalDateTime.of(2026, 9, 8, 8, 0));
        session.setEndTime(LocalDateTime.of(2026, 9, 8, 9, 30));
        session.setNotes("Felt great");
        session.setRpe(new Rpe(8.0));

        Exercise ex1 = new Exercise();
        ex1.setId(1);
        ex1.setName("Bench Press");
        ex1.setPrimaryCategory(ExerciseCategory.RESISTANCE);

        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setId(10);
        rse.setExercise(ex1);
        rse.setOrderIndex(1);
        rse.setNotes("Heavy");
        ResistanceSet rs = new ResistanceSet();
        rs.setSetNumber(1);
        rs.setSetType(SetType.WORKING);
        rs.setWeight(new Weight(100.0, WeightUnit.KG));
        rs.setRepetitions(5);
        rs.setRpe(new Rpe(8.0));
        rs.setRestSeconds(120);
        rse.addSet(rs);

        Exercise ex2 = new Exercise();
        ex2.setId(2);
        ex2.setName("Treadmill Interval");
        ex2.setPrimaryCategory(ExerciseCategory.ENDURANCE);

        EnduranceSessionExercise ese = new EnduranceSessionExercise();
        ese.setId(20);
        ese.setExercise(ex2);
        ese.setOrderIndex(2);
        ese.setNotes("Sprint");
        EnduranceInterval ei = new EnduranceInterval();
        ei.setIntervalNumber(1);
        ei.setDistance(new Distance(400.0, DistanceUnit.METERS));
        ei.setDuration(new Duration(90));
        ei.setAvgHeartRate(165);
        ei.setMaxHeartRate(180);
        ei.setAvgPower(300.0);
        ei.setCadence(175.0);
        ei.setRestSeconds(60);
        ese.addInterval(ei);

        Exercise ex3 = new Exercise();
        ex3.setId(3);
        ex3.setName("Pigeon Pose");
        ex3.setPrimaryCategory(ExerciseCategory.MOBILITY);

        MobilitySessionExercise mse = new MobilitySessionExercise();
        mse.setId(30);
        mse.setExercise(ex3);
        mse.setOrderIndex(3);
        mse.setNotes("Deep hip opening");
        MobilitySet ms = new MobilitySet();
        ms.setSetNumber(1);
        ms.setHoldDuration(new Duration(45));
        ms.setRepetitions(1);
        ms.setBilateral(true);
        mse.addSet(ms);

        session.setSessionExercises(List.of(rse, ese, mse));

        SessionJpaEntity entity = mapper.toJpaEntity(session);
        assertNotNull(entity);
        assertEquals(100, entity.getId());
        assertEquals("Full Hybrid Session", entity.getName());
        assertEquals("IN_PROGRESS", entity.getSessionStatus());
        assertEquals(8.0, entity.getRpeValue());

        Session roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals(100, roundTrip.getId());
        assertEquals("Full Hybrid Session", roundTrip.getName());
        assertEquals(SessionStatusEnum.IN_PROGRESS, roundTrip.getSessionStatus());
        assertEquals(8.0, roundTrip.getRpe().getValue());
        assertEquals(3, roundTrip.getSessionExercises().size());

        assertTrue(roundTrip.getSessionExercises().get(0) instanceof ResistanceSessionExercise);
        ResistanceSessionExercise rOut = (ResistanceSessionExercise) roundTrip.getSessionExercises().get(0);
        assertEquals("Bench Press", rOut.getExercise().getName());
        assertEquals(1, rOut.getSets().size());
        assertEquals(100.0, rOut.getSets().get(0).getWeight().getValue());

        assertTrue(roundTrip.getSessionExercises().get(1) instanceof EnduranceSessionExercise);
        EnduranceSessionExercise eOut = (EnduranceSessionExercise) roundTrip.getSessionExercises().get(1);
        assertEquals("Treadmill Interval", eOut.getExercise().getName());
        assertEquals(1, eOut.getIntervals().size());
        assertEquals(400.0, eOut.getIntervals().get(0).getDistance().getValue());

        assertTrue(roundTrip.getSessionExercises().get(2) instanceof MobilitySessionExercise);
        MobilitySessionExercise mOut = (MobilitySessionExercise) roundTrip.getSessionExercises().get(2);
        assertEquals("Pigeon Pose", mOut.getExercise().getName());
        assertEquals(1, mOut.getSets().size());
        assertTrue(mOut.getSets().get(0).isBilateral());

        // Test sessionToJson and sessionFromJson
        String json = mapper.sessionToJson(session);
        assertNotNull(json);
        Session fromJson = mapper.sessionFromJson(json);
        assertNotNull(fromJson);
        assertEquals("Full Hybrid Session", fromJson.getName());
    }

    @Test
    void testSessionWithoutOptionalFields() {
        Session session = new Session();
        session.setName("Empty Session");

        SessionJpaEntity entity = mapper.toJpaEntity(session);
        assertNotNull(entity);
        assertNull(entity.getSessionStatus());
        assertNull(entity.getRpeValue());
        assertNull(entity.getSessionExercisesJson());

        Session roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals("Empty Session", roundTrip.getName());
        assertNull(roundTrip.getSessionStatus());
        assertNull(roundTrip.getRpe());
        assertTrue(roundTrip.getSessionExercises().isEmpty());
    }

    @Test
    void testSessionFromJsonErrorHandling() {
        assertThrows(RuntimeException.class, () -> mapper.sessionFromJson("invalid json {"));
    }

    @Test
    void testExercisesFromJsonErrorHandling() {
        SessionJpaEntity entity = new SessionJpaEntity();
        entity.setSessionExercisesJson("invalid json [");
        assertThrows(RuntimeException.class, () -> mapper.toDomain(entity));
    }

    @Test
    void testSessionToJsonErrorHandling() throws Exception {
        java.lang.reflect.Field omField = SessionPersistenceMapper.class.getDeclaredField("objectMapper");
        omField.setAccessible(true);
        omField.set(mapper, new com.fasterxml.jackson.databind.ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
                throw new com.fasterxml.jackson.core.JsonParseException(null, "forced serialization error");
            }
        });

        assertThrows(RuntimeException.class, () -> mapper.sessionToJson(new Session()));
    }

    @Test
    void testExercisesToJsonErrorHandling() throws Exception {
        java.lang.reflect.Field omField = SessionPersistenceMapper.class.getDeclaredField("objectMapper");
        omField.setAccessible(true);
        omField.set(mapper, new com.fasterxml.jackson.databind.ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
                throw new com.fasterxml.jackson.core.JsonParseException(null, "forced serialization error");
            }
        });

        Session session = new Session();
        session.setName("Session with exercises");
        session.setSessionExercises(List.of(new ResistanceSessionExercise()));

        assertThrows(RuntimeException.class, () -> mapper.toJpaEntity(session));
    }
}
