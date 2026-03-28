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

import com.david13penalver.foss_training_api.application.services.exercise.EnduranceTypeServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.EnduranceTypeRepository;

@ExtendWith(MockitoExtension.class)
class EnduranceTypeServiceTest {

    @Mock
    private EnduranceTypeRepository enduranceTypeRepository;

    @InjectMocks
    private EnduranceTypeServiceImpl enduranceTypeService;

    @Test
    void testFindAll() {
        // Given
        List<EnduranceType> expectedTypes = List.of(EnduranceType.AEROBIC, EnduranceType.HIIT);
        when(enduranceTypeRepository.findAll()).thenReturn(expectedTypes);

        // When
        List<EnduranceType> result = enduranceTypeService.findAll();

        // Then
        assertEquals(expectedTypes, result);
        verify(enduranceTypeRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "AEROBIC";
        EnduranceType expectedType = EnduranceType.AEROBIC;
        when(enduranceTypeRepository.findById(name)).thenReturn(Optional.of(expectedType));

        // When
        Optional<EnduranceType> result = enduranceTypeService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedType, result.get());
        verify(enduranceTypeRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(enduranceTypeRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<EnduranceType> result = enduranceTypeService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(enduranceTypeRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        EnduranceType enduranceType = EnduranceType.AEROBIC;
        when(enduranceTypeRepository.save(enduranceType)).thenReturn(enduranceType);

        // When
        EnduranceType result = enduranceTypeService.save(enduranceType);

        // Then
        assertEquals(enduranceType, result);
        verify(enduranceTypeRepository).save(enduranceType);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "AEROBIC";
        when(enduranceTypeRepository.existsById(name)).thenReturn(true);
        doNothing().when(enduranceTypeRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> enduranceTypeService.deleteById(name));

        // Then
        verify(enduranceTypeRepository).existsById(name);
        verify(enduranceTypeRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(enduranceTypeRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> enduranceTypeService.deleteById(name));
        assertEquals("Endurance type not found with name: " + name, exception.getMessage());
        verify(enduranceTypeRepository).existsById(name);
        verify(enduranceTypeRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "AEROBIC";
        when(enduranceTypeRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = enduranceTypeService.existsById(name);

        // Then
        assertTrue(result);
        verify(enduranceTypeRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(enduranceTypeRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = enduranceTypeService.existsById(name);

        // Then
        assertFalse(result);
        verify(enduranceTypeRepository).existsById(name);
    }
}
