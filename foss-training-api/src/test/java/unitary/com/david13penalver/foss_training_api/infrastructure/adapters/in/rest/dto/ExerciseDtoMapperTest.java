package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;

class ExerciseDtoMapperTest {

    private ExerciseDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExerciseDtoMapper();
    }

    @Test
    void toEntity_mapsAllFieldsCorrectly() {
        ExerciseRequestDto dto = new ExerciseRequestDto();
        dto.setId(1);
        dto.setName("Squat");
        dto.setDescription("Leg exercise");
        dto.setImages(List.of("img1.png"));
        dto.setVideo("video.mp4");
        dto.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        dto.setSecondaryCategories(List.of(ExerciseCategory.CORE));
        dto.setResistanceMetrics(new ResistanceMetrics());
        dto.setEquipmentRequired(List.of(Equipment.BARBELL));
        dto.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        dto.setStepByStepInstructions(List.of("Step 1"));
        dto.setCommonMistakes(List.of("Mistake 1"));
        dto.setSafetyTips(List.of("Tip 1"));
        dto.setAlternativeExercises(List.of("2"));
        dto.setTags(List.of("legs"));

        Exercise entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("Squat", entity.getName());
        assertEquals("Leg exercise", entity.getDescription());
        assertEquals(List.of("img1.png"), entity.getImages());
        assertEquals("video.mp4", entity.getVideo());
        assertEquals(ExerciseCategory.RESISTANCE, entity.getPrimaryCategory());
        assertEquals(List.of(ExerciseCategory.CORE), entity.getSecondaryCategories());
        assertNotNull(entity.getResistanceMetrics());
        assertEquals(List.of(Equipment.BARBELL), entity.getEquipmentRequired());
        assertEquals(DifficultyLevel.INTERMEDIATE, entity.getDifficultyLevel());
        assertEquals(List.of("Step 1"), entity.getStepByStepInstructions());
        assertEquals(List.of("Mistake 1"), entity.getCommonMistakes());
        assertEquals(List.of("Tip 1"), entity.getSafetyTips());
        assertEquals(List.of("2"), entity.getAlternativeExercises());
        assertEquals(List.of("legs"), entity.getTags());
        assertTrue(entity.isActive());
    }

    @Test
    void toEntity_handlesNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toResponseDto_mapsAllFieldsCorrectly() {
        Exercise entity = new Exercise();
        entity.setId(5);
        entity.setName("Bench Press");
        entity.setDescription("Chest exercise");
        entity.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        entity.setCreatedBy("user123");
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setActive(true);

        ExerciseResponseDto dto = mapper.toResponseDto(entity);

        assertNotNull(dto);
        assertEquals(5, dto.getId());
        assertEquals("Bench Press", dto.getName());
        assertEquals("Chest exercise", dto.getDescription());
        assertEquals(ExerciseCategory.RESISTANCE, dto.getPrimaryCategory());
        assertEquals("user123", dto.getCreatedBy());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertTrue(dto.isActive());
    }

    @Test
    void toResponseDto_handlesNull() {
        assertNull(mapper.toResponseDto(null));
    }

    @Test
    void toResponseDtoList_mapsListAndHandlesNull() {
        Exercise entity = new Exercise();
        entity.setId(1);
        entity.setName("Pull-up");

        List<ExerciseResponseDto> dtos = mapper.toResponseDtoList(List.of(entity));
        assertEquals(1, dtos.size());
        assertEquals("Pull-up", dtos.getFirst().getName());

        assertTrue(mapper.toResponseDtoList(null).isEmpty());
    }
}
