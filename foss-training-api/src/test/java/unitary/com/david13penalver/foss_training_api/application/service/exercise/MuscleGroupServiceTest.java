package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.MuscleGroupServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

class MuscleGroupServiceTest {

    private MuscleGroupServiceImpl muscleGroupService;

    @BeforeEach
    void setUp() {
        muscleGroupService = new MuscleGroupServiceImpl();
    }

    @Test
    void testFindAll() {
        List<MuscleGroup> result = muscleGroupService.findAll();

        assertNotNull(result);
        assertEquals(MuscleGroup.values().length, result.size());
        assertTrue(result.contains(MuscleGroup.CHEST));
        assertTrue(result.contains(MuscleGroup.QUADRICEPS));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<MuscleGroup> result = muscleGroupService.findById("CHEST");

        assertTrue(result.isPresent());
        assertEquals(MuscleGroup.CHEST, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<MuscleGroup> result = muscleGroupService.findById("chest");

        assertTrue(result.isPresent());
        assertEquals(MuscleGroup.CHEST, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<MuscleGroup> result = muscleGroupService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
