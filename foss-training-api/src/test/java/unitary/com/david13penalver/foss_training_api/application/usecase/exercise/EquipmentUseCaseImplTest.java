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

import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.FindAllEquipmentUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.equipment.impl.FindEquipmentByNameUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;

@ExtendWith(MockitoExtension.class)
class EquipmentUseCaseImplTest {

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private FindAllEquipmentUseCaseImpl findAllEquipmentUseCase;

    @InjectMocks
    private FindEquipmentByNameUseCaseImpl findEquipmentByNameUseCase;

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
        Equipment expected = Equipment.BARBELL;
        when(equipmentService.findById("BARBELL")).thenReturn(Optional.of(expected));

        Optional<Equipment> result = findEquipmentByNameUseCase.execute("BARBELL");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(equipmentService).findById("BARBELL");
    }
}
