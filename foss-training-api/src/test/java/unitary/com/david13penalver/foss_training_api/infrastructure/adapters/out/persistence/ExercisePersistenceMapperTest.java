package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.MovementPattern;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.RecommendedTiming;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExerciseJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExercisePersistenceMapper;
import com.fasterxml.jackson.core.type.TypeReference;

class ExercisePersistenceMapperTest {

    private ExercisePersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExercisePersistenceMapper();
    }

    @Test
    void testNullMappings() {
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toDomain(null));
    }

    @Test
    void testResistanceExerciseRoundTrip() {
        Exercise exercise = new Exercise();
        exercise.setId(10);
        exercise.setName("Back Squat");
        exercise.setDescription("Deep barbell squat");
        exercise.setImages(List.of("http://image1.png", "http://image2.png"));
        exercise.setVideo("http://video.mp4");
        exercise.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        exercise.setSecondaryCategories(List.of(ExerciseCategory.MOBILITY));

        ResistanceMetrics rm = new ResistanceMetrics();
        rm.setMovementPattern(MovementPattern.SQUAT);
        rm.setPrimaryMuscles(List.of(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES));
        rm.setSecondaryMuscles(List.of(MuscleGroup.HAMSTRINGS));
        rm.setRecommendedSets(4);
        rm.setRecommendedRepsMin(6);
        rm.setRecommendedRepsMax(8);
        rm.setRecommendedRestSeconds(90);
        rm.setTempoRecommendation("3-1-1-0");
        exercise.setResistanceMetrics(rm);

        exercise.setEquipmentRequired(List.of(Equipment.BARBELL));
        exercise.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        exercise.setStepByStepInstructions(List.of("Step 1", "Step 2"));
        exercise.setCommonMistakes(List.of("Knees caving"));
        exercise.setSafetyTips(List.of("Keep chest up"));
        exercise.setAlternativeExercises(List.of("Front Squat"));
        exercise.setCreatedBy("coach");
        exercise.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        exercise.setUpdatedAt(LocalDateTime.of(2026, 1, 2, 10, 0));
        exercise.setActive(true);
        exercise.setTags(List.of("compound", "legs"));

        ExerciseJpaEntity entity = mapper.toJpaEntity(exercise);
        assertNotNull(entity);
        assertEquals("Back Squat", entity.getName());
        assertEquals("RESISTANCE", entity.getPrimaryCategory());

        Exercise roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals(10, roundTrip.getId());
        assertEquals("Back Squat", roundTrip.getName());
        assertEquals(ExerciseCategory.RESISTANCE, roundTrip.getPrimaryCategory());
        assertEquals(1, roundTrip.getSecondaryCategories().size());
        assertEquals(ExerciseCategory.MOBILITY, roundTrip.getSecondaryCategories().get(0));
        assertNotNull(roundTrip.getResistanceMetrics());
        assertEquals(MovementPattern.SQUAT, roundTrip.getResistanceMetrics().getMovementPattern());
        assertEquals(1, roundTrip.getEquipmentRequired().size());
        assertEquals(Equipment.BARBELL, roundTrip.getEquipmentRequired().get(0));
        assertEquals(DifficultyLevel.INTERMEDIATE, roundTrip.getDifficultyLevel());
        assertTrue(roundTrip.isActive());
        assertEquals(2, roundTrip.getTags().size());
    }

    @Test
    void testEnduranceExerciseRoundTrip() {
        Exercise exercise = new Exercise();
        exercise.setId(20);
        exercise.setName("5k Run");
        exercise.setPrimaryCategory(ExerciseCategory.ENDURANCE);

        EnduranceMetrics em = new EnduranceMetrics();
        em.setEnduranceType(EnduranceType.AEROBIC);
        em.setTrackDistance(5000.0);
        em.setTrackDuration(1500);
        exercise.setEnduranceMetrics(em);

        ExerciseJpaEntity entity = mapper.toJpaEntity(exercise);
        Exercise roundTrip = mapper.toDomain(entity);

        assertNotNull(roundTrip);
        assertEquals("5k Run", roundTrip.getName());
        assertEquals(ExerciseCategory.ENDURANCE, roundTrip.getPrimaryCategory());
        assertNotNull(roundTrip.getEnduranceMetrics());
        assertEquals(EnduranceType.AEROBIC, roundTrip.getEnduranceMetrics().getEnduranceType());
    }

    @Test
    void testMobilityExerciseRoundTrip() {
        Exercise exercise = new Exercise();
        exercise.setId(30);
        exercise.setName("Hamstring Stretch");
        exercise.setPrimaryCategory(ExerciseCategory.MOBILITY);

        MobilityMetrics mm = new MobilityMetrics();
        mm.setMobilityType(MobilityType.STATIC_STRETCHING);
        mm.setStretchType(StretchType.STATIC);
        mm.setTargetJoints(List.of(Joint.HIP));
        mm.setRecommendedHoldTimeSeconds(30);
        mm.setRecommendedRepetitions(3);
        mm.setTiming(RecommendedTiming.POST_WORKOUT);
        mm.setPerformBilaterally(true);
        exercise.setMobilityMetrics(mm);

        ExerciseJpaEntity entity = mapper.toJpaEntity(exercise);
        Exercise roundTrip = mapper.toDomain(entity);

        assertNotNull(roundTrip);
        assertEquals("Hamstring Stretch", roundTrip.getName());
        assertEquals(ExerciseCategory.MOBILITY, roundTrip.getPrimaryCategory());
        assertNotNull(roundTrip.getMobilityMetrics());
        assertEquals(MobilityType.STATIC_STRETCHING, roundTrip.getMobilityMetrics().getMobilityType());
        assertTrue(roundTrip.getMobilityMetrics().isPerformBilaterally());
    }

    @Test
    void testMinimalFields() {
        Exercise exercise = new Exercise();
        exercise.setName("Minimal");
        exercise.setActive(false);

        ExerciseJpaEntity entity = mapper.toJpaEntity(exercise);
        assertNotNull(entity);
        assertNull(entity.getPrimaryCategory());
        assertNull(entity.getSecondaryCategoriesJson());
        assertNull(entity.getEquipmentRequiredJson());
        assertNull(entity.getDifficultyLevel());

        Exercise roundTrip = mapper.toDomain(entity);
        assertNotNull(roundTrip);
        assertEquals("Minimal", roundTrip.getName());
        assertFalse(roundTrip.isActive());
        assertNull(roundTrip.getPrimaryCategory());
        assertNull(roundTrip.getSecondaryCategories());
        assertNull(roundTrip.getEquipmentRequired());
        assertNull(roundTrip.getDifficultyLevel());
    }

    @Test
    void testFromJsonErrorHandling() throws Exception {
        Method fromJsonMethod = ExercisePersistenceMapper.class.getDeclaredMethod("fromJson", String.class, TypeReference.class);
        fromJsonMethod.setAccessible(true);

        assertNull(fromJsonMethod.invoke(mapper, null, new TypeReference<List<String>>() {}));
        assertNull(fromJsonMethod.invoke(mapper, "", new TypeReference<List<String>>() {}));
        assertNull(fromJsonMethod.invoke(mapper, "   ", new TypeReference<List<String>>() {}));

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                fromJsonMethod.invoke(mapper, "invalid json [", new TypeReference<List<String>>() {}));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }

    @Test
    void testToJsonErrorHandling() throws Exception {
        java.lang.reflect.Field omField = ExercisePersistenceMapper.class.getDeclaredField("objectMapper");
        omField.setAccessible(true);
        omField.set(mapper, new com.fasterxml.jackson.databind.ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
                throw new com.fasterxml.jackson.core.JsonParseException(null, "forced serialization error");
            }
        });

        Exercise exercise = new Exercise();
        exercise.setName("Test");
        exercise.setImages(List.of("img.png"));

        assertThrows(RuntimeException.class, () -> mapper.toJpaEntity(exercise));
    }
}
