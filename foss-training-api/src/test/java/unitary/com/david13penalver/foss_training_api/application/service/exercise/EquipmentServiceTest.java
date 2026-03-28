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

import com.david13penalver.foss_training_api.application.services.exercise.EquipmentServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.EquipmentRepository;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    @Test
    void testFindAll() {
        // Given
        List<Equipment> expectedEquipment = List.of(Equipment.BARBELL, Equipment.DUMBBELL);
        when(equipmentRepository.findAll()).thenReturn(expectedEquipment);

        // When
        List<Equipment> result = equipmentService.findAll();

        // Then
        assertEquals(expectedEquipment, result);
        verify(equipmentRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "BARBELL";
        Equipment expectedEquipment = Equipment.BARBELL;
        when(equipmentRepository.findById(name)).thenReturn(Optional.of(expectedEquipment));

        // When
        Optional<Equipment> result = equipmentService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedEquipment, result.get());
        verify(equipmentRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(equipmentRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<Equipment> result = equipmentService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(equipmentRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        Equipment equipment = Equipment.BARBELL;
        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        // When
        Equipment result = equipmentService.save(equipment);

        // Then
        assertEquals(equipment, result);
        verify(equipmentRepository).save(equipment);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "BARBELL";
        when(equipmentRepository.existsById(name)).thenReturn(true);
        doNothing().when(equipmentRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> equipmentService.deleteById(name));

        // Then
        verify(equipmentRepository).existsById(name);
        verify(equipmentRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(equipmentRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> equipmentService.deleteById(name));
        assertEquals("Equipment not found with name: " + name, exception.getMessage());
        verify(equipmentRepository).existsById(name);
        verify(equipmentRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "BARBELL";
        when(equipmentRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = equipmentService.existsById(name);

        // Then
        assertTrue(result);
        verify(equipmentRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(equipmentRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = equipmentService.existsById(name);

        // Then
        assertFalse(result);
        verify(equipmentRepository).existsById(name);
    }
}
