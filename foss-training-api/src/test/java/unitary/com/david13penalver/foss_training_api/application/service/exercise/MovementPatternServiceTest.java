package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.services.exercise.MovementPatternServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MovementPatternRepository;

@ExtendWith(MockitoExtension.class)
class MovementPatternServiceTest {

    @Mock
    private MovementPatternRepository movementPatternRepository;

    @InjectMocks
    private MovementPatternServiceImpl movementPatternService;

    @Test
    void testFindAll() {
        // Given
        List<MovementPattern> expectedMovementPatterns = List.of(MovementPattern.PUSH, MovementPattern.SQUAT);
        when(movementPatternRepository.findAll()).thenReturn(expectedMovementPatterns);

        // When
        List<MovementPattern> result = movementPatternService.findAll();

        // Then
        assertEquals(expectedMovementPatterns, result);
        verify(movementPatternRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "PUSH";
        MovementPattern expectedMovementPattern = MovementPattern.PUSH;
        when(movementPatternRepository.findById(name)).thenReturn(Optional.of(expectedMovementPattern));

        // When
        Optional<MovementPattern> result = movementPatternService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedMovementPattern, result.get());
        verify(movementPatternRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(movementPatternRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<MovementPattern> result = movementPatternService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(movementPatternRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        MovementPattern movementPattern = MovementPattern.PUSH;
        when(movementPatternRepository.save(movementPattern)).thenReturn(movementPattern);

        // When
        MovementPattern result = movementPatternService.save(movementPattern);

        // Then
        assertEquals(movementPattern, result);
        verify(movementPatternRepository).save(movementPattern);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "PUSH";
        when(movementPatternRepository.existsById(name)).thenReturn(true);
        doNothing().when(movementPatternRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> movementPatternService.deleteById(name));

        // Then
        verify(movementPatternRepository).existsById(name);
        verify(movementPatternRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(movementPatternRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> movementPatternService.deleteById(name));
        assertEquals("Movement pattern not found with name: " + name, exception.getMessage());
        verify(movementPatternRepository).existsById(name);
        verify(movementPatternRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "PUSH";
        when(movementPatternRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = movementPatternService.existsById(name);

        // Then
        assertTrue(result);
        verify(movementPatternRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(movementPatternRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = movementPatternService.existsById(name);

        // Then
        assertFalse(result);
        verify(movementPatternRepository).existsById(name);
    }
}
