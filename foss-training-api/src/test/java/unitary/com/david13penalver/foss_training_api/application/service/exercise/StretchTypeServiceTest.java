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

import com.david13penalver.foss_training_api.application.services.exercise.StretchTypeServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.StretchTypeRepository;

@ExtendWith(MockitoExtension.class)
class StretchTypeServiceTest {

    @Mock
    private StretchTypeRepository stretchTypeRepository;

    @InjectMocks
    private StretchTypeServiceImpl stretchTypeService;

    @Test
    void testFindAll() {
        // Given
        List<StretchType> expectedStretchTypes = List.of(StretchType.STATIC, StretchType.DYNAMIC);
        when(stretchTypeRepository.findAll()).thenReturn(expectedStretchTypes);

        // When
        List<StretchType> result = stretchTypeService.findAll();

        // Then
        assertEquals(expectedStretchTypes, result);
        verify(stretchTypeRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "STATIC";
        StretchType expectedStretchType = StretchType.STATIC;
        when(stretchTypeRepository.findById(name)).thenReturn(Optional.of(expectedStretchType));

        // When
        Optional<StretchType> result = stretchTypeService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedStretchType, result.get());
        verify(stretchTypeRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(stretchTypeRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<StretchType> result = stretchTypeService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(stretchTypeRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        StretchType stretchType = StretchType.STATIC;
        when(stretchTypeRepository.save(stretchType)).thenReturn(stretchType);

        // When
        StretchType result = stretchTypeService.save(stretchType);

        // Then
        assertEquals(stretchType, result);
        verify(stretchTypeRepository).save(stretchType);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "STATIC";
        when(stretchTypeRepository.existsById(name)).thenReturn(true);
        doNothing().when(stretchTypeRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> stretchTypeService.deleteById(name));

        // Then
        verify(stretchTypeRepository).existsById(name);
        verify(stretchTypeRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(stretchTypeRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> stretchTypeService.deleteById(name));
        assertEquals("Stretch type not found with name: " + name, exception.getMessage());
        verify(stretchTypeRepository).existsById(name);
        verify(stretchTypeRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "STATIC";
        when(stretchTypeRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = stretchTypeService.existsById(name);

        // Then
        assertTrue(result);
        verify(stretchTypeRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(stretchTypeRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = stretchTypeService.existsById(name);

        // Then
        assertFalse(result);
        verify(stretchTypeRepository).existsById(name);
    }
}
