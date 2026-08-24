package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.FindAllMobilityTypesService;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.FindMobilityTypeByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

class MobilityTypeUseCaseTest {

    private FindAllMobilityTypesService findAllMobilityTypesService;
    private FindMobilityTypeByNameService findMobilityTypeByNameService;

    @BeforeEach
    void setUp() {
        findAllMobilityTypesService = new FindAllMobilityTypesService();
        findMobilityTypeByNameService = new FindMobilityTypeByNameService();
    }

    @Test
    void testFindAll() {
        List<MobilityType> result = findAllMobilityTypesService.execute();

        assertNotNull(result);
        assertEquals(MobilityType.values().length, result.size());
        assertTrue(result.contains(MobilityType.YOGA));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<MobilityType> result = findMobilityTypeByNameService.execute("YOGA");

        assertTrue(result.isPresent());
        assertEquals(MobilityType.YOGA, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<MobilityType> result = findMobilityTypeByNameService.execute("yoga");

        assertTrue(result.isPresent());
        assertEquals(MobilityType.YOGA, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<MobilityType> result = findMobilityTypeByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
