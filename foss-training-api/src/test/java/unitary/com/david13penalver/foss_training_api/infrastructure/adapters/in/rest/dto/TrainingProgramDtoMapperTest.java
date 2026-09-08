package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.ProgramWorkoutRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.ProgramWorkoutResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;

@ExtendWith(MockitoExtension.class)
class TrainingProgramDtoMapperTest {

    @Mock
    private SessionDtoMapper sessionDtoMapper;

    @InjectMocks
    private TrainingProgramDtoMapper mapper;

    @Test
    void toEntity_andToResponseDto() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toResponseDto(null));

        Session session = new Session();
        session.setName("Session 1");
        SessionRequestDto sessionReq = new SessionRequestDto();
        sessionReq.setName("Session 1");
        SessionResponseDto sessionResp = new SessionResponseDto();
        sessionResp.setName("Session 1");

        when(sessionDtoMapper.toEntity(sessionReq)).thenReturn(session);
        when(sessionDtoMapper.toResponseDto(session)).thenReturn(sessionResp);

        ProgramWorkoutRequestDto workoutReq = ProgramWorkoutRequestDto.builder()
                .dayOfWeek(1)
                .focus("Chest")
                .session(sessionReq)
                .build();

        List<ProgramWorkoutRequestDto> workoutsList = new ArrayList<>();
        workoutsList.add(workoutReq);
        workoutsList.add(null);

        TrainingProgramRequestDto reqDto = TrainingProgramRequestDto.builder()
                .id(1)
                .name("PPL")
                .description("Routine")
                .durationWeeks(8)
                .periodizationType(PeriodizationType.LINEAR)
                .level(ProgramLevel.INTERMEDIATE)
                .workouts(workoutsList)
                .isActive(true)
                .build();

        TrainingProgram entity = mapper.toEntity(reqDto);
        assertNotNull(entity);
        assertEquals("PPL", entity.getName());
        assertEquals(1, entity.getWorkouts().size());

        TrainingProgramResponseDto respDto = mapper.toResponseDto(entity);
        assertNotNull(respDto);
        assertEquals("PPL", respDto.getName());
        assertEquals(1, respDto.getWorkouts().size());
        assertEquals("Session 1", respDto.getWorkouts().get(0).getSession().getName());
    }

    @Test
    void toEntity_nullWorkouts() {
        TrainingProgramRequestDto reqDto = TrainingProgramRequestDto.builder()
                .name("Basic")
                .durationWeeks(4)
                .workouts(null)
                .build();

        TrainingProgram entity = mapper.toEntity(reqDto);
        assertNotNull(entity);
        assertTrue(entity.getWorkouts().isEmpty());
    }

    @Test
    void toResponseDtoList() {
        assertTrue(mapper.toResponseDtoList(null).isEmpty());

        TrainingProgram program = TrainingProgram.builder().id(1).name("PPL").workouts(null).build();
        List<TrainingProgramResponseDto> dtos = mapper.toResponseDtoList(List.of(program));

        assertEquals(1, dtos.size());
        assertEquals("PPL", dtos.get(0).getName());
        assertTrue(dtos.get(0).getWorkouts().isEmpty());
    }
}
