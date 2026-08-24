package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.FindAllStretchTypesService;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.FindStretchTypeByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

class StretchTypeUseCaseTest {

    private FindAllStretchTypesService findAllStretchTypesService;
    private FindStretchTypeByNameService findStretchTypeByNameService;

    @BeforeEach
    void setUp() {
        findAllStretchTypesService = new FindAllStretchTypesService();
        findStretchTypeByNameService = new FindStretchTypeByNameService();
    }

    @Test
    void testFindAll() {
        List<StretchType> result = findAllStretchTypesService.execute();

        assertNotNull(result);
        assertEquals(StretchType.values().length, result.size());
        assertTrue(result.contains(StretchType.STATIC));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<StretchType> result = findStretchTypeByNameService.execute("STATIC");

        assertTrue(result.isPresent());
        assertEquals(StretchType.STATIC, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<StretchType> result = findStretchTypeByNameService.execute("static");

        assertTrue(result.isPresent());
        assertEquals(StretchType.STATIC, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<StretchType> result = findStretchTypeByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
