package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.FindAllEquipmentService;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.FindEquipmentByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

class EquipmentUseCaseTest {

    private FindAllEquipmentService findAllEquipmentService;
    private FindEquipmentByNameService findEquipmentByNameService;

    @BeforeEach
    void setUp() {
        findAllEquipmentService = new FindAllEquipmentService();
        findEquipmentByNameService = new FindEquipmentByNameService();
    }

    @Test
    void testFindAll() {
        List<Equipment> result = findAllEquipmentService.execute();

        assertNotNull(result);
        assertEquals(Equipment.values().length, result.size());
        assertTrue(result.contains(Equipment.BARBELL));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<Equipment> result = findEquipmentByNameService.execute("BARBELL");

        assertTrue(result.isPresent());
        assertEquals(Equipment.BARBELL, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<Equipment> result = findEquipmentByNameService.execute("barbell");

        assertTrue(result.isPresent());
        assertEquals(Equipment.BARBELL, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<Equipment> result = findEquipmentByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
