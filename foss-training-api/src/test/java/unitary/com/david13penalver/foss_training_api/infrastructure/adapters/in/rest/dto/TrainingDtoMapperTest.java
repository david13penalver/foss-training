package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingResponseDto;

class TrainingDtoMapperTest {

    private TrainingDtoMapper mapper;

    @BeforeEach
    void setUp() {
        ExerciseDtoMapper exerciseDtoMapper = new ExerciseDtoMapper();
        SessionDtoMapper sessionDtoMapper = new SessionDtoMapper(exerciseDtoMapper);
        mapper = new TrainingDtoMapper(sessionDtoMapper);
    }

    @Test
    void nullHandling() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toResponseDto(null));
        assertTrue(mapper.toResponseDtoList(null).isEmpty());
    }

    @Test
    void toEntity_fullMapping() {
        SessionRequestDto sessionDto = new SessionRequestDto();
        sessionDto.setName("Leg Routine");
        sessionDto.setSessionStatus(SessionStatusEnum.PLANNED);

        LocalDate date = LocalDate.of(2026, 9, 8);
        LocalDateTime start = LocalDateTime.of(2026, 9, 8, 8, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 8, 9, 30);

        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setId(1);
        dto.setName("Morning Legs");
        dto.setDescription("High intensity");
        dto.setSession(sessionDto);
        dto.setTrainingDate(date);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setStatus(TrainingStatusEnum.IN_PROGRESS);
        dto.setNotes("Squat focused");
        dto.setRpe(new RpeDto(8.5));

        Training entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("Morning Legs", entity.getName());
        assertEquals("High intensity", entity.getDescription());
        assertNotNull(entity.getSession());
        assertEquals("Leg Routine", entity.getSession().getName());
        assertEquals(date, entity.getTrainingDate());
        assertEquals(start, entity.getStartTime());
        assertEquals(end, entity.getEndTime());
        assertEquals(TrainingStatusEnum.IN_PROGRESS, entity.getStatus());
        assertEquals("Squat focused", entity.getNotes());
        assertNotNull(entity.getRpe());
        assertEquals(8.5, entity.getRpe().getValue());
    }

    @Test
    void toEntity_defaultsStatusToPlanned_whenNull() {
        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setName("Basic Training");
        dto.setTrainingDate(LocalDate.now());

        Training entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(TrainingStatusEnum.PLANNED, entity.getStatus());
    }

    @Test
    void toResponseDto_fullMapping() {
        Session session = new Session();
        session.setId(5);
        session.setName("Upper Body");

        LocalDate date = LocalDate.of(2026, 9, 8);
        LocalDateTime start = LocalDateTime.of(2026, 9, 8, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 8, 11, 0, 0);

        Training entity = new Training();
        entity.setId(10);
        entity.setName("Upper Body Workout");
        entity.setDescription("Chest and back");
        entity.setSession(session);
        entity.setTrainingDate(date);
        entity.setStartTime(start);
        entity.setEndTime(end);
        entity.setStatus(TrainingStatusEnum.COMPLETED);
        entity.setNotes("Good pump");
        entity.setRpe(new Rpe(9.0));

        TrainingResponseDto response = mapper.toResponseDto(entity);

        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals("Upper Body Workout", response.getName());
        assertEquals("Chest and back", response.getDescription());
        assertNotNull(response.getSession());
        assertEquals("Upper Body", response.getSession().getName());
        assertEquals(date, response.getTrainingDate());
        assertEquals(start, response.getStartTime());
        assertEquals(end, response.getEndTime());
        assertEquals(TrainingStatusEnum.COMPLETED, response.getStatus());
        assertEquals("Good pump", response.getNotes());
        assertNotNull(response.getDuration());
        assertEquals(3600, response.getDuration().getTotalSeconds());
        assertNotNull(response.getRpe());
        assertEquals(9.0, response.getRpe().getValue());
    }

    @Test
    void toResponseDtoList_mapsAll() {
        Training t1 = new Training();
        t1.setId(1);
        t1.setName("T1");
        Training t2 = new Training();
        t2.setId(2);
        t2.setName("T2");

        List<TrainingResponseDto> dtos = mapper.toResponseDtoList(List.of(t1, t2));

        assertEquals(2, dtos.size());
        assertEquals("T1", dtos.get(0).getName());
        assertEquals("T2", dtos.get(1).getName());
    }
}
