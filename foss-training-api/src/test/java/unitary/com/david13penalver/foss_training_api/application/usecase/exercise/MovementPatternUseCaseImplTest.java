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

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.FindAllMovementPatternsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.FindMovementPatternByNameUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

@ExtendWith(MockitoExtension.class)
class MovementPatternUseCaseImplTest {

    @Mock
    private MovementPatternService movementPatternService;

    @InjectMocks
    private FindAllMovementPatternsUseCaseImpl findAllMovementPatternsUseCase;

    @InjectMocks
    private FindMovementPatternByNameUseCaseImpl findMovementPatternByNameUseCase;

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
        MovementPattern expected = MovementPattern.PUSH;
        when(movementPatternService.findById("PUSH")).thenReturn(Optional.of(expected));

        Optional<MovementPattern> result = findMovementPatternByNameUseCase.execute("PUSH");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(movementPatternService).findById("PUSH");
    }
}
