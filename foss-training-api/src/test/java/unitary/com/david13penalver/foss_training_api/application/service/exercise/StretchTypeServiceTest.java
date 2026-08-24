package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.StretchTypeServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

class StretchTypeServiceTest {

    private StretchTypeServiceImpl stretchTypeService;

    @BeforeEach
    void setUp() {
        stretchTypeService = new StretchTypeServiceImpl();
    }

    @Test
    void testFindAll() {
        List<StretchType> result = stretchTypeService.findAll();

        assertNotNull(result);
        assertEquals(StretchType.values().length, result.size());
        assertTrue(result.contains(StretchType.STATIC));
        assertTrue(result.contains(StretchType.DYNAMIC));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<StretchType> result = stretchTypeService.findById("STATIC");

        assertTrue(result.isPresent());
        assertEquals(StretchType.STATIC, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<StretchType> result = stretchTypeService.findById("static");

        assertTrue(result.isPresent());
        assertEquals(StretchType.STATIC, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<StretchType> result = stretchTypeService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
