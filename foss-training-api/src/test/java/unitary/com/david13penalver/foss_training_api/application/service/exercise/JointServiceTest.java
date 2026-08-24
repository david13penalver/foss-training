package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.JointServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

class JointServiceTest {

    private JointServiceImpl jointService;

    @BeforeEach
    void setUp() {
        jointService = new JointServiceImpl();
    }

    @Test
    void testFindAll() {
        List<Joint> result = jointService.findAll();

        assertNotNull(result);
        assertEquals(Joint.values().length, result.size());
        assertTrue(result.contains(Joint.SHOULDER));
        assertTrue(result.contains(Joint.KNEE));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<Joint> result = jointService.findById("SHOULDER");

        assertTrue(result.isPresent());
        assertEquals(Joint.SHOULDER, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<Joint> result = jointService.findById("shoulder");

        assertTrue(result.isPresent());
        assertEquals(Joint.SHOULDER, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<Joint> result = jointService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
