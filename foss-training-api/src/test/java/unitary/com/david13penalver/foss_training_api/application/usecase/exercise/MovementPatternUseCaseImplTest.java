package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.MovementPatternService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.DeleteMovementPatternUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.FindAllMovementPatternsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.FindMovementPatternByNameUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.MovementPatternExistsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.SaveMovementPatternUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

@ExtendWith(MockitoExtension.class)
class MovementPatternUseCaseImplTest {

    @Mock
    private MovementPatternService movementPatternService;

    @InjectMocks
    private FindAllMovementPatternsUseCaseImpl findAllMovementPatternsUseCase;

    @InjectMocks
    private FindMovementPatternByNameUseCaseImpl findMovementPatternByNameUseCase;

    @InjectMocks
    private SaveMovementPatternUseCaseImpl saveMovementPatternUseCase;

    @InjectMocks
    private DeleteMovementPatternUseCaseImpl deleteMovementPatternUseCase;

    @InjectMocks
    private MovementPatternExistsUseCaseImpl movementPatternExistsUseCase;

    @Test
    void testFindAll() {
        List<MovementPattern> expected = List.of(MovementPattern.PUSH);
        when(movementPatternService.findAll()).thenReturn(expected);

        List<MovementPattern> result = findAllMovementPatternsUseCase.execute();

        assertSame(expected, result);
        verify(movementPatternService).findAll();
    }

    @Test
    void testFindByName() {
        MovementPattern expected = MovementPattern.SQUAT;
        when(movementPatternService.findById("SQUAT")).thenReturn(Optional.of(expected));

        Optional<MovementPattern> result = findMovementPatternByNameUseCase.execute("SQUAT");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(movementPatternService).findById("SQUAT");
    }

    @Test
    void testSave() {
        MovementPattern pattern = MovementPattern.PUSH;
        when(movementPatternService.save(pattern)).thenReturn(pattern);

        MovementPattern result = saveMovementPatternUseCase.execute(pattern);

        assertSame(pattern, result);
        verify(movementPatternService).save(pattern);
    }

    @Test
    void testDeleteByName() {
        doNothing().when(movementPatternService).deleteById("PULL");

        deleteMovementPatternUseCase.execute("PULL");

        verify(movementPatternService).deleteById("PULL");
    }

    @Test
    void testExistsByName() {
        when(movementPatternService.existsById("PUSH")).thenReturn(true);

        boolean result = movementPatternExistsUseCase.execute("PUSH");

        assertTrue(result);
        verify(movementPatternService).existsById("PUSH");
    }
}
