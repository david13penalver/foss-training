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

import com.david13penalver.foss_training_api.application.services.exercise.MobilityTypeServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MobilityTypeRepository;

@ExtendWith(MockitoExtension.class)
class MobilityTypeServiceTest {

    @Mock
    private MobilityTypeRepository mobilityTypeRepository;

    @InjectMocks
    private MobilityTypeServiceImpl mobilityTypeService;

    @Test
    void testFindAll() {
        // Given
        List<MobilityType> expectedMobilityTypes = List.of(MobilityType.YOGA, MobilityType.STATIC_STRETCHING);
        when(mobilityTypeRepository.findAll()).thenReturn(expectedMobilityTypes);

        // When
        List<MobilityType> result = mobilityTypeService.findAll();

        // Then
        assertEquals(expectedMobilityTypes, result);
        verify(mobilityTypeRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "YOGA";
        MobilityType expectedMobilityType = MobilityType.YOGA;
        when(mobilityTypeRepository.findById(name)).thenReturn(Optional.of(expectedMobilityType));

        // When
        Optional<MobilityType> result = mobilityTypeService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedMobilityType, result.get());
        verify(mobilityTypeRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(mobilityTypeRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<MobilityType> result = mobilityTypeService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(mobilityTypeRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        MobilityType mobilityType = MobilityType.YOGA;
        when(mobilityTypeRepository.save(mobilityType)).thenReturn(mobilityType);

        // When
        MobilityType result = mobilityTypeService.save(mobilityType);

        // Then
        assertEquals(mobilityType, result);
        verify(mobilityTypeRepository).save(mobilityType);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "YOGA";
        when(mobilityTypeRepository.existsById(name)).thenReturn(true);
        doNothing().when(mobilityTypeRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> mobilityTypeService.deleteById(name));

        // Then
        verify(mobilityTypeRepository).existsById(name);
        verify(mobilityTypeRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(mobilityTypeRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mobilityTypeService.deleteById(name));
        assertEquals("Mobility type not found with name: " + name, exception.getMessage());
        verify(mobilityTypeRepository).existsById(name);
        verify(mobilityTypeRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "YOGA";
        when(mobilityTypeRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = mobilityTypeService.existsById(name);

        // Then
        assertTrue(result);
        verify(mobilityTypeRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(mobilityTypeRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = mobilityTypeService.existsById(name);

        // Then
        assertFalse(result);
        verify(mobilityTypeRepository).existsById(name);
    }
}
