package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.DeleteExerciseService;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.ExerciseExistsService;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.FindAllExercisesService;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.FindExerciseByIdService;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.SaveExerciseService;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseUseCaseTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private FindAllExercisesService findAllExercisesService;

    @InjectMocks
    private FindExerciseByIdService findExerciseByIdService;

    @InjectMocks
    private SaveExerciseService saveExerciseService;

    @InjectMocks
    private DeleteExerciseService deleteExerciseService;

    @InjectMocks
    private ExerciseExistsService exerciseExistsService;

    @InjectMocks
    private com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl.SearchExercisesService searchExercisesService;

    @Test
    void testSearchExercises_withCriteria() {
        var criteria = new com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria(
                "Squat", null, null, null, null);
        Exercise exercise = new Exercise();
        exercise.setName("Barbell Squat");
        when(exerciseRepository.findByCriteria(criteria)).thenReturn(List.of(exercise));

        List<Exercise> result = searchExercisesService.execute(criteria);

        assertEquals(1, result.size());
        assertEquals("Barbell Squat", result.get(0).getName());
        verify(exerciseRepository).findByCriteria(criteria);
    }

    @Test
    void testSearchExercises_withCriteriaAndPageQuery() {
        var criteria = com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria.empty();
        var pageQuery = com.david13penalver.foss_training_api.domain.model.common.PageQuery.of(0, 10);
        Exercise exercise = new Exercise();
        exercise.setName("Barbell Squat");
        var pagedResult = new com.david13penalver.foss_training_api.domain.model.common.PagedResult<>(
                List.of(exercise), 0, 10, 1, 1);
        when(exerciseRepository.findByCriteria(criteria, pageQuery)).thenReturn(pagedResult);

        var result = searchExercisesService.execute(criteria, pageQuery);

        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        verify(exerciseRepository).findByCriteria(criteria, pageQuery);
    }

    @Test
    void testFindAll() {
        List<Exercise> expected = List.of(new Exercise());
        when(exerciseRepository.findAll()).thenReturn(expected);

        List<Exercise> result = findAllExercisesService.execute();

        assertSame(expected, result);
        verify(exerciseRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        Exercise expected = new Exercise();
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(expected));

        Optional<Exercise> result = findExerciseByIdService.execute(1);

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(exerciseRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists() {
        when(exerciseRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Exercise> result = findExerciseByIdService.execute(999);

        assertFalse(result.isPresent());
        verify(exerciseRepository).findById(999);
    }

    @Test
    void testSave() {
        Exercise exercise = new Exercise();
        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        Exercise result = saveExerciseService.execute(exercise);

        assertSame(exercise, result);
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void testDeleteById_WhenExists() {
        when(exerciseRepository.existsById(1)).thenReturn(true);
        doNothing().when(exerciseRepository).deleteById(1);

        assertDoesNotThrow(() -> deleteExerciseService.execute(1));

        verify(exerciseRepository).existsById(1);
        verify(exerciseRepository).deleteById(1);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        when(exerciseRepository.existsById(999)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> deleteExerciseService.execute(999));
        assertEquals("Exercise not found with id: 999", exception.getMessage());
        verify(exerciseRepository).existsById(999);
        verify(exerciseRepository, never()).deleteById(999);
    }

    @Test
    void testExistsById() {
        when(exerciseRepository.existsById(1)).thenReturn(true);

        boolean result = exerciseExistsService.execute(1);

        assertTrue(result);
        verify(exerciseRepository).existsById(1);
    }
}
