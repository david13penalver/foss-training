package unitary.com.david13penalver.foss_training_api.application.service.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.services.exercise.EquipmentServiceImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

class EquipmentServiceTest {

    private EquipmentServiceImpl equipmentService;

    @BeforeEach
    void setUp() {
        equipmentService = new EquipmentServiceImpl();
    }

    @Test
    void testFindAll() {
        List<Equipment> result = equipmentService.findAll();

        assertNotNull(result);
        assertEquals(Equipment.values().length, result.size());
        assertTrue(result.contains(Equipment.BARBELL));
        assertTrue(result.contains(Equipment.DUMBBELL));
    }

    @Test
    void testFindById_WhenExists() {
        Optional<Equipment> result = equipmentService.findById("BARBELL");

        assertTrue(result.isPresent());
        assertEquals(Equipment.BARBELL, result.get());
    }

    @Test
    void testFindById_CaseInsensitive() {
        Optional<Equipment> result = equipmentService.findById("barbell");

        assertTrue(result.isPresent());
        assertEquals(Equipment.BARBELL, result.get());
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<Equipment> result = equipmentService.findById("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
