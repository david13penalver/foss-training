package unitary.com.david13penalver.foss_training_api.application.usecase.program;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.program.impl.DeleteTrainingProgramService;
import com.david13penalver.foss_training_api.application.usecases.program.impl.FindAllTrainingProgramsService;
import com.david13penalver.foss_training_api.application.usecases.program.impl.FindTrainingProgramByIdService;
import com.david13penalver.foss_training_api.application.usecases.program.impl.GenerateProgramScheduleService;
import com.david13penalver.foss_training_api.application.usecases.program.impl.SaveTrainingProgramService;
import com.david13penalver.foss_training_api.application.usecases.program.impl.TrainingProgramExistsService;
import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class TrainingProgramUseCaseTest {

    @Mock
    private TrainingProgramRepository programRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private FindAllTrainingProgramsService findAllService;

    @InjectMocks
    private FindTrainingProgramByIdService findByIdService;

    @InjectMocks
    private SaveTrainingProgramService saveService;

    @InjectMocks
    private DeleteTrainingProgramService deleteService;

    @InjectMocks
    private TrainingProgramExistsService existsService;

    @InjectMocks
    private GenerateProgramScheduleService generateScheduleService;

    @Test
    void testFindAll() {
        List<TrainingProgram> expected = List.of(new TrainingProgram());
        when(programRepository.findAll()).thenReturn(expected);

        List<TrainingProgram> result = findAllService.execute();

        assertSame(expected, result);
        verify(programRepository).findAll();
    }

    @Test
    void testFindById_whenExists() {
        TrainingProgram expected = new TrainingProgram();
        when(programRepository.findById(1)).thenReturn(Optional.of(expected));

        Optional<TrainingProgram> result = findByIdService.execute(1);

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(programRepository).findById(1);
    }

    @Test
    void testFindById_whenNotExists() {
        when(programRepository.findById(999)).thenReturn(Optional.empty());

        Optional<TrainingProgram> result = findByIdService.execute(999);

        assertFalse(result.isPresent());
        verify(programRepository).findById(999);
    }

    @Test
    void testSave_success() {
        TrainingProgram program = TrainingProgram.builder()
                .name("PPL")
                .durationWeeks(4)
                .build();
        when(programRepository.save(program)).thenReturn(program);

        TrainingProgram result = saveService.execute(program);

        assertSame(program, result);
        verify(programRepository).save(program);
    }

    @Test
    void testDelete_whenExists() {
        when(programRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> deleteService.execute(1));
        verify(programRepository).deleteById(1);
    }

    @Test
    void testDelete_whenNotExists() {
        when(programRepository.existsById(999)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> deleteService.execute(999));
        assertEquals("Training program not found with id: 999", ex.getMessage());
        verify(programRepository, never()).deleteById(any());
    }

    @Test
    void testExists() {
        when(programRepository.existsById(1)).thenReturn(true);
        assertTrue(existsService.execute(1));
    }

    @Test
    void testGenerateSchedule_success() {
        Session pushSession = new Session();
        pushSession.setName("Push Day");

        ProgramWorkout w1 = ProgramWorkout.builder()
                .dayOfWeek(1)
                .focus("Chest")
                .session(pushSession)
                .build();

        ProgramWorkout w2 = ProgramWorkout.builder()
                .dayOfWeek(3)
                .focus("Legs")
                .session(null)
                .build();

        TrainingProgram program = TrainingProgram.builder()
                .id(10)
                .name("Hypertrophy")
                .durationWeeks(2)
                .periodizationType(PeriodizationType.LINEAR)
                .level(ProgramLevel.INTERMEDIATE)
                .workouts(List.of(w1, w2))
                .build();

        when(programRepository.findById(10)).thenReturn(Optional.of(program));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDate startDate = LocalDate.of(2026, 9, 14); // Monday
        List<Training> trainings = generateScheduleService.execute(10, startDate);

        assertNotNull(trainings);
        assertEquals(4, trainings.size()); // 2 weeks * 2 workouts = 4

        Training first = trainings.get(0);
        assertEquals("Hypertrophy - W1D1: Push Day", first.getName());
        assertEquals("Chest", first.getDescription());
        assertEquals(startDate, first.getTrainingDate());
        assertEquals(TrainingStatusEnum.PLANNED, first.getStatus());

        Training second = trainings.get(1);
        assertEquals("Hypertrophy - W1D3: Workout", second.getName());
        assertEquals(startDate.plusDays(2), second.getTrainingDate());

        // Test with null startDate (defaults to now)
        List<Training> trainingsNow = generateScheduleService.execute(10, null);
        assertEquals(4, trainingsNow.size());
    }

    @Test
    void testGenerateSchedule_programNotFound() {
        when(programRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generateScheduleService.execute(999, LocalDate.now()));
        assertEquals("Training program not found with id: 999", ex.getMessage());
    }

    @Test
    void testGenerateSchedule_noWorkoutsConfigured() {
        TrainingProgram program = TrainingProgram.builder()
                .id(10)
                .name("Empty Program")
                .durationWeeks(2)
                .workouts(List.of())
                .build();

        when(programRepository.findById(10)).thenReturn(Optional.of(program));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generateScheduleService.execute(10, LocalDate.now()));
        assertEquals("Program has no workout days configured", ex.getMessage());
    }
}
