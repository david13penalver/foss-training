package unitary.com.david13penalver.foss_training_api.infrastructure.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;
import com.david13penalver.foss_training_api.infrastructure.configuration.DatabaseDataSeeder;

class DatabaseDataSeederTest {

    private ExerciseRepository exerciseRepository;
    private SessionRepository sessionRepository;
    private DatabaseDataSeeder seeder;

    @BeforeEach
    void setUp() {
        exerciseRepository = mock(ExerciseRepository.class);
        sessionRepository = mock(SessionRepository.class);
        seeder = new DatabaseDataSeeder(exerciseRepository, sessionRepository);
    }

    @Test
    @DisplayName("Seeds exercises and session templates when database is empty")
    void run_seedsExercisesAndTemplates_whenEmpty() {
        when(exerciseRepository.findAll()).thenReturn(new ArrayList<>());
        when(sessionRepository.findAll()).thenReturn(new ArrayList<>());

        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> {
            Exercise ex = invocation.getArgument(0);
            ex.setId(1);
            return ex;
        });

        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            Session s = invocation.getArgument(0);
            s.setId(100);
            return s;
        });

        seeder.run(new DefaultApplicationArguments(new String[0]));

        verify(exerciseRepository, times(25)).save(any(Exercise.class));
        verify(sessionRepository, times(5)).save(any(Session.class));
    }

    @Test
    @DisplayName("Does not duplicate exercises or session templates when already present")
    void run_skipsExistingExercisesAndTemplates() {
        Exercise existingExercise = new Exercise();
        existingExercise.setId(1);
        existingExercise.setName("Barbell Flat Bench Press");

        Session existingSession = new Session();
        existingSession.setId(10);
        existingSession.setName("Push Day: Hypertrophy & Pressing");

        when(exerciseRepository.findAll()).thenReturn(List.of(existingExercise));
        when(sessionRepository.findAll()).thenReturn(List.of(existingSession));

        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run(new DefaultApplicationArguments(new String[0]));

        // Should seed 24 exercises (25 total catalog minus 1 existing)
        verify(exerciseRepository, times(24)).save(any(Exercise.class));
        // Should seed 4 templates (5 total minus 1 existing)
        verify(sessionRepository, times(4)).save(any(Session.class));
    }
}
