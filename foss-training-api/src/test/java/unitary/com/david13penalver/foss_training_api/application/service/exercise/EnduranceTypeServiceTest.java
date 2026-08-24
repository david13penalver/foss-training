package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.EnduranceTypeServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

class EnduranceTypeServiceTest {

    private EnduranceTypeServiceImpl enduranceTypeService;

    @BeforeEach
    void setUp() {
        enduranceTypeService = new EnduranceTypeServiceImpl();
    }

    @Test
    void testFindAll() {
        List<EnduranceType> result = enduranceTypeService.findAll();

        assertNotNull(result);
        assertEquals(EnduranceType.values().length, result.size());
        assertTrue(result.contains(EnduranceType.AEROBIC));
        assertTrue(result.contains(EnduranceType.HIIT));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<EnduranceType> result = enduranceTypeService.findById("AEROBIC");

        assertTrue(result.isPresent());
        assertEquals(EnduranceType.AEROBIC, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<EnduranceType> result = enduranceTypeService.findById("aerobic");

        assertTrue(result.isPresent());
        assertEquals(EnduranceType.AEROBIC, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<EnduranceType> result = enduranceTypeService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
