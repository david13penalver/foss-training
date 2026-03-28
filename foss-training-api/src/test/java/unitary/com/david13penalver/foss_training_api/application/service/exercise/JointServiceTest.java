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

import com.david13penalver.foss_training_api.application.services.exercise.JointServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.JointRepository;

@ExtendWith(MockitoExtension.class)
class JointServiceTest {

    @Mock
    private JointRepository jointRepository;

    @InjectMocks
    private JointServiceImpl jointService;

    @Test
    void testFindAll() {
        // Given
        List<Joint> expectedJoints = List.of(Joint.SHOULDER, Joint.HIP);
        when(jointRepository.findAll()).thenReturn(expectedJoints);

        // When
        List<Joint> result = jointService.findAll();

        // Then
        assertEquals(expectedJoints, result);
        verify(jointRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Given
        String name = "SHOULDER";
        Joint expectedJoint = Joint.SHOULDER;
        when(jointRepository.findById(name)).thenReturn(Optional.of(expectedJoint));

        // When
        Optional<Joint> result = jointService.findById(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedJoint, result.get());
        verify(jointRepository).findById(name);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(jointRepository.findById(name)).thenReturn(Optional.empty());

        // When
        Optional<Joint> result = jointService.findById(name);

        // Then
        assertFalse(result.isPresent());
        verify(jointRepository).findById(name);
    }

    @Test
    void testSave() {
        // Given
        Joint joint = Joint.SHOULDER;
        when(jointRepository.save(joint)).thenReturn(joint);

        // When
        Joint result = jointService.save(joint);

        // Then
        assertEquals(joint, result);
        verify(jointRepository).save(joint);
    }

    @Test
    void testDeleteById_WhenExists() {
        // Given
        String name = "SHOULDER";
        when(jointRepository.existsById(name)).thenReturn(true);
        doNothing().when(jointRepository).deleteById(name);

        // When
        assertDoesNotThrow(() -> jointService.deleteById(name));

        // Then
        verify(jointRepository).existsById(name);
        verify(jointRepository).deleteById(name);
    }

    @Test
    void testDeleteById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(jointRepository.existsById(name)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jointService.deleteById(name));
        assertEquals("Joint not found with name: " + name, exception.getMessage());
        verify(jointRepository).existsById(name);
        verify(jointRepository, never()).deleteById(name);
    }

    @Test
    void testExistsById_WhenExists() {
        // Given
        String name = "SHOULDER";
        when(jointRepository.existsById(name)).thenReturn(true);

        // When
        boolean result = jointService.existsById(name);

        // Then
        assertTrue(result);
        verify(jointRepository).existsById(name);
    }

    @Test
    void testExistsById_WhenNotExists() {
        // Given
        String name = "NON_EXISTENT";
        when(jointRepository.existsById(name)).thenReturn(false);

        // When
        boolean result = jointService.existsById(name);

        // Then
        assertFalse(result);
        verify(jointRepository).existsById(name);
    }
}
