package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
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
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionExerciseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DistanceDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.PaceDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.WeightDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.EnduranceIntervalDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.EnduranceSessionExerciseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.MobilitySessionExerciseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.MobilitySetDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.ResistanceSessionExerciseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.ResistanceSetDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;

class SessionDtoMapperTest {

    private SessionDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SessionDtoMapper(new ExerciseDtoMapper());
    }

    @Test
    void toEntity_and_toResponseDto_roundTripsAllSessionExerciseTypes() {
        SessionRequestDto req = new SessionRequestDto();
        req.setId(10);
        req.setName("Full Workout");
        req.setDescription("Complete session");
        req.setSessionStatus(SessionStatusEnum.PLANNED);
        req.setStartTime(LocalDateTime.now().minusHours(1));
        req.setEndTime(LocalDateTime.now());
        req.setNotes("Good session");
        req.setRpe(new RpeDto(7.5));

        // Resistance exercise
        ResistanceSessionExerciseDto rDto = new ResistanceSessionExerciseDto();
        rDto.setId(1);
        rDto.setOrderIndex(1);
        rDto.setNotes("Bench");
        ExerciseResponseDto ex1 = new ExerciseResponseDto();
        ex1.setId(101);
        ex1.setName("Bench Press");
        ex1.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        rDto.setExercise(ex1);

        ResistanceSetDto setDto = new ResistanceSetDto(1, SetType.WORKING, new WeightDto(80.0, WeightUnit.KG), 10, new RpeDto(8.0), 90);
        rDto.setSets(List.of(setDto));

        // Endurance exercise
        EnduranceSessionExerciseDto eDto = new EnduranceSessionExerciseDto();
        eDto.setId(2);
        eDto.setOrderIndex(2);
        ExerciseResponseDto ex2 = new ExerciseResponseDto();
        ex2.setId(102);
        ex2.setName("Running");
        ex2.setPrimaryCategory(ExerciseCategory.ENDURANCE);
        eDto.setExercise(ex2);

        EnduranceIntervalDto intervalDto = new EnduranceIntervalDto(1, new DistanceDto(1000.0, DistanceUnit.METERS),
                new DurationDto(300), new PaceDto(300, DistanceUnit.KILOMETERS), 150, 170, 250.0, 180.0, 60);
        eDto.setIntervals(List.of(intervalDto));

        // Mobility exercise
        MobilitySessionExerciseDto mDto = new MobilitySessionExerciseDto();
        mDto.setId(3);
        mDto.setOrderIndex(3);
        ExerciseResponseDto ex3 = new ExerciseResponseDto();
        ex3.setId(103);
        ex3.setName("Hamstring Stretch");
        ex3.setPrimaryCategory(ExerciseCategory.MOBILITY);
        mDto.setExercise(ex3);

        MobilitySetDto mSetDto = new MobilitySetDto(1, new DurationDto(45), 3, true);
        mDto.setSets(List.of(mSetDto));

        req.setSessionExercises(List.of(rDto, eDto, mDto));

        // To Entity
        Session entity = mapper.toEntity(req);
        assertNotNull(entity);
        assertEquals("Full Workout", entity.getName());
        assertEquals(3, entity.getSessionExercises().size());
        assertTrue(entity.getSessionExercises().get(0) instanceof ResistanceSessionExercise);
        assertTrue(entity.getSessionExercises().get(1) instanceof EnduranceSessionExercise);
        assertTrue(entity.getSessionExercises().get(2) instanceof MobilitySessionExercise);

        // To Response DTO
        SessionResponseDto resp = mapper.toResponseDto(entity);
        assertNotNull(resp);
        assertEquals("Full Workout", resp.getName());
        assertEquals(3, resp.getSessionExercises().size());
        assertTrue(resp.getSessionExercises().get(0) instanceof ResistanceSessionExerciseDto);
        assertTrue(resp.getSessionExercises().get(1) instanceof EnduranceSessionExerciseDto);
        assertTrue(resp.getSessionExercises().get(2) instanceof MobilitySessionExerciseDto);
        assertEquals(800.0, resp.getTotalVolume());
        assertNotNull(resp.getDuration());
    }

    @Test
    void nullHandling_returnsNullOrEmpty() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toResponseDto(null));
        assertNull(mapper.toExerciseEntity(null));
        assertNull(mapper.toExerciseResponseDto(null));
        assertTrue(mapper.toResponseDtoList(null).isEmpty());
    }

    @Test
    void toResponseDtoList_mapsList() {
        Session s = new Session();
        s.setId(1);
        s.setName("S1");
        List<SessionResponseDto> dtos = mapper.toResponseDtoList(List.of(s));
        assertEquals(1, dtos.size());
        assertEquals("S1", dtos.getFirst().getName());
    }

    @Test
    void unknownSubtypes_and_nullExercise_returnExpected() {
        assertNull(mapper.toExerciseEntity(new SessionExerciseDto() {}));
        assertNull(mapper.toExerciseResponseDto(new SessionExercise() {}));

        ResistanceSessionExerciseDto rDto = new ResistanceSessionExerciseDto();
        rDto.setExercise(null);
        SessionExercise exercise = mapper.toExerciseEntity(rDto);
        assertNotNull(exercise);
        assertNull(exercise.getExercise());
    }
}
