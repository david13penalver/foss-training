package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.ExerciseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.DeleteExerciseUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.ExerciseExistsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.FindAllExercisesUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.FindExerciseByIdUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.SaveExerciseUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

@ExtendWith(MockitoExtension.class)
class ExerciseUseCaseImplTest {

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private FindAllExercisesUseCaseImpl findAllExercisesUseCase;

    @InjectMocks
    private FindExerciseByIdUseCaseImpl findExerciseByIdUseCase;

    @InjectMocks
    private SaveExerciseUseCaseImpl saveExerciseUseCase;

    @InjectMocks
    private DeleteExerciseUseCaseImpl deleteExerciseUseCase;

    @InjectMocks
    private ExerciseExistsUseCaseImpl exerciseExistsUseCase;

    @Test
    void testFindAll() {
        List<Exercise> expected = List.of(new Exercise());
        when(exerciseService.findAll()).thenReturn(expected);

        List<Exercise> result = findAllExercisesUseCase.execute();

        assertSame(expected, result);
        verify(exerciseService).findAll();
    }

    @Test
    void testFindById() {
        Exercise expected = new Exercise();
        when(exerciseService.findById(1)).thenReturn(Optional.of(expected));

        Optional<Exercise> result = findExerciseByIdUseCase.execute(1);

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(exerciseService).findById(1);
    }

    @Test
    void testSave() {
        Exercise exercise = new Exercise();
        when(exerciseService.save(exercise)).thenReturn(exercise);

        Exercise result = saveExerciseUseCase.execute(exercise);

        assertSame(exercise, result);
        verify(exerciseService).save(exercise);
    }

    @Test
    void testDeleteById() {
        doNothing().when(exerciseService).deleteById(1);

        deleteExerciseUseCase.execute(1);

        verify(exerciseService).deleteById(1);
    }

    @Test
    void testExistsById() {
        when(exerciseService.existsById(1)).thenReturn(true);

        boolean result = exerciseExistsUseCase.execute(1);

        assertTrue(result);
        verify(exerciseService).existsById(1);
    }
}
