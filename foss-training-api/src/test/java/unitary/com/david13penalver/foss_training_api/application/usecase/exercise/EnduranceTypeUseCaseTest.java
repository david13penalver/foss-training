package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.FindAllEnduranceTypesService;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.FindEnduranceTypeByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

class EnduranceTypeUseCaseTest {

    private FindAllEnduranceTypesService findAllEnduranceTypesService;
    private FindEnduranceTypeByNameService findEnduranceTypeByNameService;

    @BeforeEach
    void setUp() {
        findAllEnduranceTypesService = new FindAllEnduranceTypesService();
        findEnduranceTypeByNameService = new FindEnduranceTypeByNameService();
    }

    @Test
    void testFindAll() {
        List<EnduranceType> result = findAllEnduranceTypesService.execute();

        assertNotNull(result);
        assertEquals(EnduranceType.values().length, result.size());
        assertTrue(result.contains(EnduranceType.AEROBIC));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<EnduranceType> result = findEnduranceTypeByNameService.execute("AEROBIC");

        assertTrue(result.isPresent());
        assertEquals(EnduranceType.AEROBIC, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<EnduranceType> result = findEnduranceTypeByNameService.execute("aerobic");

        assertTrue(result.isPresent());
        assertEquals(EnduranceType.AEROBIC, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<EnduranceType> result = findEnduranceTypeByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
