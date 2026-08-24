package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.FindAllJointsService;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.FindJointByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

class JointUseCaseTest {

    private FindAllJointsService findAllJointsService;
    private FindJointByNameService findJointByNameService;

    @BeforeEach
    void setUp() {
        findAllJointsService = new FindAllJointsService();
        findJointByNameService = new FindJointByNameService();
    }

    @Test
    void testFindAll() {
        List<Joint> result = findAllJointsService.execute();

        assertNotNull(result);
        assertEquals(Joint.values().length, result.size());
        assertTrue(result.contains(Joint.SHOULDER));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<Joint> result = findJointByNameService.execute("SHOULDER");

        assertTrue(result.isPresent());
        assertEquals(Joint.SHOULDER, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<Joint> result = findJointByNameService.execute("shoulder");

        assertTrue(result.isPresent());
        assertEquals(Joint.SHOULDER, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<Joint> result = findJointByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
