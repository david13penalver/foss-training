package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;

class TrainingProgramTest {

    @Test
    void validate_validProgram() {
        TrainingProgram program = TrainingProgram.builder()
                .id(1)
                .name("Push Pull Legs")
                .description("Hypertrophy focus")
                .durationWeeks(8)
                .periodizationType(PeriodizationType.LINEAR)
                .level(ProgramLevel.INTERMEDIATE)
                .isActive(true)
                .build();

        Session session = new Session();
        session.setName("Push Day");

        ProgramWorkout workout = ProgramWorkout.builder()
                .dayOfWeek(1)
                .focus("Chest and Triceps")
                .session(session)
                .build();

        program.addWorkout(workout);

        assertDoesNotThrow(program::validate);
        assertEquals(1, program.getWorkouts().size());
        assertEquals(8, program.getDurationWeeks());
    }

    @Test
    void addWorkout_null_isIgnored() {
        TrainingProgram program = new TrainingProgram();
        program.setWorkouts(null);
        program.addWorkout(null);
        assertTrue(program.getWorkouts().isEmpty());
    }

    @Test
    void validate_blankName_throwsException() {
        TrainingProgram program = TrainingProgram.builder()
                .name("")
                .durationWeeks(4)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, program::validate);
        assertEquals("Program name cannot be null or blank", ex.getMessage());

        program.setName(null);
        assertThrows(IllegalArgumentException.class, program::validate);
    }

    @Test
    void validate_zeroOrNegativeDuration_throwsException() {
        TrainingProgram program = TrainingProgram.builder()
                .name("Strength Block")
                .durationWeeks(0)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, program::validate);
        assertEquals("Program duration must be greater than zero weeks", ex.getMessage());

        program.setDurationWeeks(-4);
        assertThrows(IllegalArgumentException.class, program::validate);
    }

    @Test
    void programWorkout_validate_invalidDayOfWeek() {
        Session session = new Session();
        ProgramWorkout workout = ProgramWorkout.builder()
                .dayOfWeek(0)
                .session(session)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, workout::validate);
        assertEquals("Day of week must be between 1 (Monday) and 7 (Sunday)", ex.getMessage());

        workout.setDayOfWeek(8);
        assertThrows(IllegalArgumentException.class, workout::validate);

        workout.setDayOfWeek(null);
        assertThrows(IllegalArgumentException.class, workout::validate);
    }

    @Test
    void programWorkout_validate_nullSession() {
        ProgramWorkout workout = ProgramWorkout.builder()
                .dayOfWeek(3)
                .session(null)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, workout::validate);
        assertEquals("Workout session template must not be null", ex.getMessage());
    }

    @Test
    void gettersAndSetters() {
        TrainingProgram program = new TrainingProgram();
        program.setId(10);
        program.setName("5/3/1");
        program.setDescription("Wendler");
        program.setDurationWeeks(4);
        program.setPeriodizationType(PeriodizationType.UNDULATING);
        program.setLevel(ProgramLevel.ADVANCED);
        program.setIsActive(true);

        assertEquals(10, program.getId());
        assertEquals("5/3/1", program.getName());
        assertEquals("Wendler", program.getDescription());
        assertEquals(4, program.getDurationWeeks());
        assertEquals(PeriodizationType.UNDULATING, program.getPeriodizationType());
        assertEquals(ProgramLevel.ADVANCED, program.getLevel());
        assertTrue(program.getIsActive());
        assertNotNull(program.getWorkouts());
    }
}
