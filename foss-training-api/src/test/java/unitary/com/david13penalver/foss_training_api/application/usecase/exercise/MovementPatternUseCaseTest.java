package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.FindAllMovementPatternsService;
import com.david13penalver.foss_training_api.application.usecases.exercise.movementpattern.impl.FindMovementPatternByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.MovementPattern;

class MovementPatternUseCaseTest {

    private FindAllMovementPatternsService findAllMovementPatternsService;
    private FindMovementPatternByNameService findMovementPatternByNameService;

    @BeforeEach
    void setUp() {
        findAllMovementPatternsService = new FindAllMovementPatternsService();
        findMovementPatternByNameService = new FindMovementPatternByNameService();
    }

    @Test
    void testFindAll() {
        List<MovementPattern> result = findAllMovementPatternsService.execute();

        assertNotNull(result);
        assertEquals(MovementPattern.values().length, result.size());
        assertTrue(result.contains(MovementPattern.PUSH));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<MovementPattern> result = findMovementPatternByNameService.execute("PUSH");

        assertTrue(result.isPresent());
        assertEquals(MovementPattern.PUSH, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<MovementPattern> result = findMovementPatternByNameService.execute("push");

        assertTrue(result.isPresent());
        assertEquals(MovementPattern.PUSH, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<MovementPattern> result = findMovementPatternByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
