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

import com.david13penalver.foss_training_api.application.services.exercise.MuscleGroupServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.MuscleGroupRepository;

@ExtendWith(MockitoExtension.class)
class MuscleGroupServiceTest {

    @Mock
    private MuscleGroupRepository muscleGroupRepository;

    @InjectMocks
    private MuscleGroupServiceImpl muscleGroupService;

    @Test
    void testFindAll() {
        // Given
        List<MuscleGroup> expectedMuscleGroups = List.of(MuscleGroup.CHEST, MuscleGroup.QUADRICEPS);
        when(muscleGroupRepository.findAll()).thenReturn(expectedMuscleGroups);

        // When
        List<MuscleGroup> result = muscleGroupService.findAll();

        // Then
        assertEquals(expectedMuscleGroups, result);
        verify(muscleGroupRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "CHEST";
        MuscleGroup expectedMuscleGroup = MuscleGroup.CHEST;
        when(muscleGroupRepository.findById(name)).thenReturn(Optional.of(expectedMuscleGroup));

        // When
        Optional<MuscleGroup> result = muscleGroupService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedMuscleGroup, result.get());
        verify(muscleGroupRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(muscleGroupRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<MuscleGroup> result = muscleGroupService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(muscleGroupRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        MuscleGroup muscleGroup = MuscleGroup.CHEST;
        when(muscleGroupRepository.save(muscleGroup)).thenReturn(muscleGroup);

        // When
        MuscleGroup result = muscleGroupService.save(muscleGroup);

        // Then
        assertEquals(muscleGroup, result);
        verify(muscleGroupRepository).save(muscleGroup);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "CHEST";
        when(muscleGroupRepository.existsById(name)).thenReturn(true);
        doNothing().when(muscleGroupRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> muscleGroupService.deleteById(name));

        // Then
        verify(muscleGroupRepository).existsById(name);
        verify(muscleGroupRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(muscleGroupRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> muscleGroupService.deleteById(name));
        assertEquals("Muscle group not found with name: " + name, exception.getMessage());
        verify(muscleGroupRepository).existsById(name);
        verify(muscleGroupRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "CHEST";
        when(muscleGroupRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = muscleGroupService.existsById(name);

        // Then
        assertTrue(result);
        verify(muscleGroupRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(muscleGroupRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = muscleGroupService.existsById(name);

        // Then
        assertFalse(result);
        verify(muscleGroupRepository).existsById(name);
    }
}
