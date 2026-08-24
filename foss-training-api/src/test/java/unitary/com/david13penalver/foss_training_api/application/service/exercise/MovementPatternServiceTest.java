package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.MovementPatternServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MovementPattern;

class MovementPatternServiceTest {

    private MovementPatternServiceImpl movementPatternService;

    @BeforeEach
    void setUp() {
        movementPatternService = new MovementPatternServiceImpl();
    }

    @Test
    void testFindAll() {
        List<MovementPattern> result = movementPatternService.findAll();

        assertNotNull(result);
        assertEquals(MovementPattern.values().length, result.size());
        assertTrue(result.contains(MovementPattern.PUSH));
        assertTrue(result.contains(MovementPattern.SQUAT));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<MovementPattern> result = movementPatternService.findById("PUSH");

        assertTrue(result.isPresent());
        assertEquals(MovementPattern.PUSH, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<MovementPattern> result = movementPatternService.findById("push");

        assertTrue(result.isPresent());
        assertEquals(MovementPattern.PUSH, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<MovementPattern> result = movementPatternService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
