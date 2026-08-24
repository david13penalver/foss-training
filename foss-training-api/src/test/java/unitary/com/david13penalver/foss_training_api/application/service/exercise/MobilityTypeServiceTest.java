package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.MobilityTypeServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

class MobilityTypeServiceTest {

    private MobilityTypeServiceImpl mobilityTypeService;

    @BeforeEach
    void setUp() {
        mobilityTypeService = new MobilityTypeServiceImpl();
    }

    @Test
    void testFindAll() {
        List<MobilityType> result = mobilityTypeService.findAll();

        assertNotNull(result);
        assertEquals(MobilityType.values().length, result.size());
        assertTrue(result.contains(MobilityType.STATIC_STRETCHING));
        assertTrue(result.contains(MobilityType.YOGA));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<MobilityType> result = mobilityTypeService.findById("YOGA");

        assertTrue(result.isPresent());
        assertEquals(MobilityType.YOGA, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<MobilityType> result = mobilityTypeService.findById("yoga");

        assertTrue(result.isPresent());
        assertEquals(MobilityType.YOGA, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<MobilityType> result = mobilityTypeService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
