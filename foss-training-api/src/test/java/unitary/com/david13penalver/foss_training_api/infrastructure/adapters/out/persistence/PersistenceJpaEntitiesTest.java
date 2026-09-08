package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExerciseJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.PersistenceProgramWorkout;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.TrainingProgramJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.PersistenceSessionExercise;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingJpaEntity;

class PersistenceJpaEntitiesTest {

    @Test
    void testExerciseJpaEntity() {
        ExerciseJpaEntity e = new ExerciseJpaEntity();
        e.setId(1);
        e.setName("E");
        e.setDescription("Desc");
        e.setImagesJson("[]");
        e.setVideo("v");
        e.setPrimaryCategory("RESISTANCE");
        e.setSecondaryCategoriesJson("[]");
        e.setResistanceMetricsJson("{}");
        e.setEnduranceMetricsJson("{}");
        e.setMobilityMetricsJson("{}");
        e.setEquipmentRequiredJson("[]");
        e.setDifficultyLevel("INTERMEDIATE");
        e.setInstructionsJson("[]");
        e.setCommonMistakesJson("[]");
        e.setSafetyTipsJson("[]");
        e.setAlternativeExercisesJson("[]");
        e.setCreatedBy("user");
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        e.setActive(true);
        e.setTagsJson("[]");

        assertEquals(1, e.getId());
        assertEquals("E", e.getName());
        assertEquals("Desc", e.getDescription());
        assertEquals("[]", e.getImagesJson());
        assertEquals("v", e.getVideo());
        assertEquals("RESISTANCE", e.getPrimaryCategory());
        assertEquals("[]", e.getSecondaryCategoriesJson());
        assertEquals("{}", e.getResistanceMetricsJson());
        assertEquals("{}", e.getEnduranceMetricsJson());
        assertEquals("{}", e.getMobilityMetricsJson());
        assertEquals("[]", e.getEquipmentRequiredJson());
        assertEquals("INTERMEDIATE", e.getDifficultyLevel());
        assertEquals("[]", e.getInstructionsJson());
        assertEquals("[]", e.getCommonMistakesJson());
        assertEquals("[]", e.getSafetyTipsJson());
        assertEquals("[]", e.getAlternativeExercisesJson());
        assertEquals("user", e.getCreatedBy());
        assertNotNull(e.getCreatedAt());
        assertNotNull(e.getUpdatedAt());
        assertTrue(e.isActive());
        assertEquals("[]", e.getTagsJson());
        assertNotNull(e.toString());

        ExerciseJpaEntity e2 = ExerciseJpaEntity.builder().id(1).name("E").build();
        assertEquals(e.getId(), e2.getId());
    }

    @Test
    void testSessionJpaEntity() {
        SessionJpaEntity s = new SessionJpaEntity();
        s.setId(2);
        s.setName("S");
        s.setDescription("D");
        s.setSessionStatus("PLANNED");
        s.setStartTime(LocalDateTime.now());
        s.setEndTime(LocalDateTime.now());
        s.setNotes("Notes");
        s.setRpeValue(7.0);
        s.setSessionExercisesJson("[]");

        assertEquals(2, s.getId());
        assertEquals("S", s.getName());
        assertEquals("D", s.getDescription());
        assertEquals("PLANNED", s.getSessionStatus());
        assertNotNull(s.getStartTime());
        assertNotNull(s.getEndTime());
        assertEquals("Notes", s.getNotes());
        assertEquals(7.0, s.getRpeValue());
        assertEquals("[]", s.getSessionExercisesJson());
        assertNotNull(s.toString());

        SessionJpaEntity s2 = SessionJpaEntity.builder().id(2).name("S").build();
        assertEquals(s.getId(), s2.getId());
    }

    @Test
    void testTrainingJpaEntity() {
        TrainingJpaEntity t = new TrainingJpaEntity();
        t.setId(3);
        t.setName("T");
        t.setDescription("D");
        t.setTrainingDate(LocalDate.now());
        t.setStartTime(LocalDateTime.now());
        t.setEndTime(LocalDateTime.now());
        t.setStatus("PLANNED");
        t.setNotes("N");
        t.setRpeValue(8.0);
        t.setSessionJson("{}");

        assertEquals(3, t.getId());
        assertEquals("T", t.getName());
        assertEquals("D", t.getDescription());
        assertNotNull(t.getTrainingDate());
        assertNotNull(t.getStartTime());
        assertNotNull(t.getEndTime());
        assertEquals("PLANNED", t.getStatus());
        assertEquals("N", t.getNotes());
        assertEquals(8.0, t.getRpeValue());
        assertEquals("{}", t.getSessionJson());
        assertNotNull(t.toString());

        TrainingJpaEntity t2 = TrainingJpaEntity.builder().id(3).name("T").build();
        assertEquals(t.getId(), t2.getId());
    }

    @Test
    void testTrainingProgramJpaEntity() {
        TrainingProgramJpaEntity p = new TrainingProgramJpaEntity();
        p.setId(4);
        p.setName("P");
        p.setDescription("D");
        p.setDurationWeeks(8);
        p.setPeriodizationType("LINEAR");
        p.setLevel("ADVANCED");
        p.setWorkoutsJson("[]");
        p.setActive(true);

        assertEquals(4, p.getId());
        assertEquals("P", p.getName());
        assertEquals("D", p.getDescription());
        assertEquals(8, p.getDurationWeeks());
        assertEquals("LINEAR", p.getPeriodizationType());
        assertEquals("ADVANCED", p.getLevel());
        assertEquals("[]", p.getWorkoutsJson());
        assertTrue(p.isActive());
        assertNotNull(p.toString());

        TrainingProgramJpaEntity p2 = TrainingProgramJpaEntity.builder().id(4).name("P").build();
        assertEquals(p.getId(), p2.getId());
    }

    @Test
    void testPersistenceSessionExercise() {
        PersistenceSessionExercise pe = new PersistenceSessionExercise();
        pe.setType("RESISTANCE");
        pe.setId(5);
        pe.setExercise(new Exercise());
        pe.setOrderIndex(1);
        pe.setNotes("N");
        pe.setResistanceSets(List.of(new ResistanceSet()));
        pe.setEnduranceIntervals(List.of(new EnduranceInterval()));
        pe.setMobilitySets(List.of(new MobilitySet()));

        assertEquals("RESISTANCE", pe.getType());
        assertEquals(5, pe.getId());
        assertNotNull(pe.getExercise());
        assertEquals(1, pe.getOrderIndex());
        assertEquals("N", pe.getNotes());
        assertEquals(1, pe.getResistanceSets().size());
        assertEquals(1, pe.getEnduranceIntervals().size());
        assertEquals(1, pe.getMobilitySets().size());
        assertNotNull(pe.toString());

        PersistenceSessionExercise pe2 = PersistenceSessionExercise.builder().type("RESISTANCE").build();
        assertEquals("RESISTANCE", pe2.getType());
    }

    @Test
    void testPersistenceProgramWorkout() {
        PersistenceProgramWorkout pw = new PersistenceProgramWorkout();
        pw.setDayOfWeek(1);
        pw.setFocus("Legs");
        pw.setSessionJson("{}");

        assertEquals(1, pw.getDayOfWeek());
        assertEquals("Legs", pw.getFocus());
        assertEquals("{}", pw.getSessionJson());
        assertNotNull(pw.toString());

        PersistenceProgramWorkout pw2 = PersistenceProgramWorkout.builder().dayOfWeek(2).focus("Push").sessionJson("{}").build();
        assertEquals(2, pw2.getDayOfWeek());
    }
}
