package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherence;
import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherenceCalculator;
import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherenceStatus;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class ProgramAdherenceTest {

    private TrainingProgram program;
    private final LocalDate today = LocalDate.of(2026, 9, 21);

    @BeforeEach
    void setUp() {
        program = TrainingProgram.builder()
                .id(1)
                .name("Hypertrophy Cycle")
                .durationWeeks(4)
                .periodizationType(PeriodizationType.UNDULATING)
                .level(ProgramLevel.INTERMEDIATE)
                .workouts(List.of(
                        ProgramWorkout.builder().dayOfWeek(1).focus("Push").session(new Session()).build(),
                        ProgramWorkout.builder().dayOfWeek(3).focus("Pull").session(new Session()).build(),
                        ProgramWorkout.builder().dayOfWeek(5).focus("Legs").session(new Session()).build()
                ))
                .build();
    }

    @Test
    void testNullProgramThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ProgramAdherenceCalculator.calculate(null, List.of()));
    }

    @Test
    void testEmptyTrainingsReturnsNotStarted() {
        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, List.of(), today);

        assertNotNull(adherence);
        assertEquals(1, adherence.getProgramId());
        assertEquals("Hypertrophy Cycle", adherence.getProgramName());
        assertEquals(4, adherence.getDurationWeeks());
        assertEquals(12, adherence.getTotalScheduledWorkouts()); // 4 weeks * 3 workouts
        assertEquals(0, adherence.getCompletedWorkouts());
        assertEquals(12, adherence.getPlannedWorkouts());
        assertEquals(0.0, adherence.getOverallCompletionRate());
        assertEquals(0.0, adherence.getCurrentAdherenceRate());
        assertEquals(ProgramAdherenceStatus.NOT_STARTED, adherence.getStatus());
        assertTrue(adherence.getWeeklyBreakdowns().isEmpty());
    }

    @Test
    void testAllCompletedReturnsCompletedStatus() {
        LocalDate start = today.minusWeeks(4);
        List<Training> trainings = List.of(
                createTraining(1, "W1D1", start, TrainingStatusEnum.COMPLETED, start.atTime(10, 0)),
                createTraining(2, "W1D3", start.plusDays(2), TrainingStatusEnum.COMPLETED, start.plusDays(2).atTime(10, 0)),
                createTraining(3, "W1D5", start.plusDays(4), TrainingStatusEnum.COMPLETED, start.plusDays(4).atTime(10, 0))
        );
        program.setDurationWeeks(1);

        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, trainings, today);

        assertEquals(3, adherence.getTotalScheduledWorkouts());
        assertEquals(3, adherence.getCompletedWorkouts());
        assertEquals(0, adherence.getMissedWorkouts());
        assertEquals(100.0, adherence.getOverallCompletionRate());
        assertEquals(100.0, adherence.getCurrentAdherenceRate());
        assertEquals(3, adherence.getCurrentStreak());
        assertEquals(3, adherence.getLongestStreak());
        assertEquals(ProgramAdherenceStatus.COMPLETED, adherence.getStatus());
    }

    @Test
    void testOnTrackAdherenceStatus() {
        LocalDate start = today.minusDays(14);
        List<Training> trainings = List.of(
                createTraining(1, "W1D1", start, TrainingStatusEnum.COMPLETED, start.atTime(10, 0)),
                createTraining(2, "W1D3", start.plusDays(2), TrainingStatusEnum.COMPLETED, start.plusDays(2).atTime(10, 0)),
                createTraining(3, "W1D5", start.plusDays(4), TrainingStatusEnum.COMPLETED, start.plusDays(4).atTime(10, 0)),
                createTraining(4, "W2D1", start.plusDays(7), TrainingStatusEnum.COMPLETED, start.plusDays(7).atTime(10, 0)),
                createTraining(5, "W2D3", start.plusDays(9), TrainingStatusEnum.CANCELLED, null), // 1 missed
                createTraining(6, "W2D5", start.plusDays(11), TrainingStatusEnum.COMPLETED, start.plusDays(11).atTime(10, 0)),
                createTraining(7, "W3D1", today.plusDays(1), TrainingStatusEnum.PLANNED, null)
        );

        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, trainings, today);

        assertEquals(5, adherence.getCompletedWorkouts());
        assertEquals(1, adherence.getCancelledWorkouts());
        assertEquals(1, adherence.getPlannedWorkouts());
        // 5 completed out of 6 elapsed = 83.3%
        assertEquals(83.3, adherence.getCurrentAdherenceRate());
        assertEquals(ProgramAdherenceStatus.ON_TRACK, adherence.getStatus());
        assertEquals(1, adherence.getCurrentStreak());
        assertEquals(4, adherence.getLongestStreak());
    }

    @Test
    void testBehindScheduleAdherenceStatus() {
        LocalDate start = today.minusDays(10);
        List<Training> trainings = List.of(
                createTraining(1, "W1D1", start, TrainingStatusEnum.COMPLETED, start.atTime(10, 0)),
                createTraining(2, "W1D3", start.plusDays(2), TrainingStatusEnum.SKIPPED, null),
                createTraining(3, "W1D5", start.plusDays(4), TrainingStatusEnum.COMPLETED, start.plusDays(4).atTime(10, 0)),
                createTraining(4, "W2D1", start.plusDays(7), TrainingStatusEnum.PLANNED, null) // past date => overdue/missed
        );

        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, trainings, today);

        // 2 completed out of 4 expected = 50.0%
        assertEquals(2, adherence.getCompletedWorkouts());
        assertEquals(2, adherence.getMissedWorkouts());
        assertEquals(50.0, adherence.getCurrentAdherenceRate());
        assertEquals(ProgramAdherenceStatus.BEHIND_SCHEDULE, adherence.getStatus());
    }

    @Test
    void testAtRiskAdherenceStatus() {
        LocalDate start = today.minusDays(10);
        List<Training> trainings = List.of(
                createTraining(1, "W1D1", start, TrainingStatusEnum.COMPLETED, start.atTime(10, 0)),
                createTraining(2, "W1D3", start.plusDays(2), TrainingStatusEnum.CANCELLED, null),
                createTraining(3, "W1D5", start.plusDays(4), TrainingStatusEnum.SKIPPED, null),
                createTraining(4, "W2D1", start.plusDays(7), TrainingStatusEnum.CANCELLED, null)
        );

        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, trainings, today);

        // 1 completed out of 4 expected = 25.0%
        assertEquals(25.0, adherence.getCurrentAdherenceRate());
        assertEquals(ProgramAdherenceStatus.AT_RISK, adherence.getStatus());
    }

    @Test
    void testWeeklyBreakdownsCalculations() {
        LocalDate start = today.minusWeeks(2);
        List<Training> trainings = List.of(
                createTraining(1, "W1D1", start, TrainingStatusEnum.COMPLETED, start.atTime(10, 0)),
                createTraining(2, "W1D3", start.plusDays(2), TrainingStatusEnum.COMPLETED, start.plusDays(2).atTime(10, 0)),
                createTraining(3, "W1D5", start.plusDays(4), TrainingStatusEnum.COMPLETED, start.plusDays(4).atTime(10, 0)),
                createTraining(4, "W2D1", start.plusDays(7), TrainingStatusEnum.COMPLETED, start.plusDays(7).atTime(10, 0)),
                createTraining(5, "W2D3", start.plusDays(9), TrainingStatusEnum.CANCELLED, null)
        );

        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, trainings, today);

        assertEquals(4, adherence.getWeeklyBreakdowns().size());
        assertEquals(1, adherence.getWeeklyBreakdowns().get(0).getWeekNumber());
        assertEquals(3, adherence.getWeeklyBreakdowns().get(0).getCompletedWorkouts());
        assertTrue(adherence.getWeeklyBreakdowns().get(0).isCompleted());

        assertEquals(2, adherence.getWeeklyBreakdowns().get(1).getWeekNumber());
        assertEquals(1, adherence.getWeeklyBreakdowns().get(1).getCompletedWorkouts());
        assertEquals(1, adherence.getWeeklyBreakdowns().get(1).getMissedWorkouts());
        assertFalse(adherence.getWeeklyBreakdowns().get(1).isCompleted());
    }

    @Test
    void testOnTimeEvaluationWithGracePeriod() {
        LocalDate schedDate = LocalDate.of(2026, 9, 10);
        // Completed 1 day later (within grace period)
        Training onTimeTraining = createTraining(1, "Workout", schedDate, TrainingStatusEnum.COMPLETED, schedDate.plusDays(1).atTime(18, 0));
        // Completed 3 days later (outside grace period)
        Training lateTraining = createTraining(2, "Workout Late", schedDate, TrainingStatusEnum.COMPLETED, schedDate.plusDays(3).atTime(18, 0));

        ProgramAdherence adherence = ProgramAdherenceCalculator.calculate(program, List.of(onTimeTraining, lateTraining), today);

        assertTrue(adherence.getWorkoutDetails().get(0).isOnTime());
        assertFalse(adherence.getWorkoutDetails().get(1).isOnTime());
    }

    private Training createTraining(Integer id, String name, LocalDate schedDate, TrainingStatusEnum status, LocalDateTime completedAt) {
        Training t = new Training();
        t.setId(id);
        t.setName(name);
        t.setTrainingDate(schedDate);
        t.setStatus(status);
        if (completedAt != null) {
            t.setStartTime(completedAt.minusHours(1));
            t.setEndTime(completedAt);
        }
        t.setRpe(new Rpe(8.0));
        return t;
    }
}
