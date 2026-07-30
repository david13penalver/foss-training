package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.EquipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.DeleteEquipmentUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.EquipmentExistsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.FindAllEquipmentUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.FindEquipmentByNameUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.SaveEquipmentUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

@ExtendWith(MockitoExtension.class)
class EquipmentUseCaseImplTest {

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private FindAllEquipmentUseCaseImpl findAllEquipmentUseCase;

    @InjectMocks
    private FindEquipmentByNameUseCaseImpl findEquipmentByNameUseCase;

    @InjectMocks
    private SaveEquipmentUseCaseImpl saveEquipmentUseCase;

    @InjectMocks
    private DeleteEquipmentUseCaseImpl deleteEquipmentUseCase;

    @InjectMocks
    private EquipmentExistsUseCaseImpl equipmentExistsUseCase;

    @Test
    void testFindAll() {
        List<Equipment> expected = List.of(Equipment.BARBELL);
        when(equipmentService.findAll()).thenReturn(expected);

        List<Equipment> result = findAllEquipmentUseCase.execute();

        assertSame(expected, result);
        verify(equipmentService).findAll();
    }

    @Test
    void testFindByName() {
        Equipment expected = Equipment.DUMBBELL;
        when(equipmentService.findById("DUMBBELL")).thenReturn(Optional.of(expected));

        Optional<Equipment> result = findEquipmentByNameUseCase.execute("DUMBBELL");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(equipmentService).findById("DUMBBELL");
    }

    @Test
    void testSave() {
        Equipment equipment = Equipment.BARBELL;
        when(equipmentService.save(equipment)).thenReturn(equipment);

        Equipment result = saveEquipmentUseCase.execute(equipment);

        assertSame(equipment, result);
        verify(equipmentService).save(equipment);
    }

    @Test
    void testDeleteByName() {
        doNothing().when(equipmentService).deleteById("BARBELL");

        deleteEquipmentUseCase.execute("BARBELL");

        verify(equipmentService).deleteById("BARBELL");
    }

    @Test
    void testExistsByName() {
        when(equipmentService.existsById("BARBELL")).thenReturn(true);

        boolean result = equipmentExistsUseCase.execute("BARBELL");

        assertTrue(result);
        verify(equipmentService).existsById("BARBELL");
    }
}
