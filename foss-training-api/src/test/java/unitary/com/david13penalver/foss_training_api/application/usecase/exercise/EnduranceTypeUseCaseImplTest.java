package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.EnduranceTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.DeleteEnduranceTypeUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.EnduranceTypeExistsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.FindAllEnduranceTypesUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.FindEnduranceTypeByNameUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.SaveEnduranceTypeUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

@ExtendWith(MockitoExtension.class)
class EnduranceTypeUseCaseImplTest {

    @Mock
    private EnduranceTypeService enduranceTypeService;

    @InjectMocks
    private FindAllEnduranceTypesUseCaseImpl findAllEnduranceTypesUseCase;

    @InjectMocks
    private FindEnduranceTypeByNameUseCaseImpl findEnduranceTypeByNameUseCase;

    @InjectMocks
    private SaveEnduranceTypeUseCaseImpl saveEnduranceTypeUseCase;

    @InjectMocks
    private DeleteEnduranceTypeUseCaseImpl deleteEnduranceTypeUseCase;

    @InjectMocks
    private EnduranceTypeExistsUseCaseImpl enduranceTypeExistsUseCase;

    @Test
    void testFindAll() {
        List<EnduranceType> expected = List.of(EnduranceType.AEROBIC);
        when(enduranceTypeService.findAll()).thenReturn(expected);

        List<EnduranceType> result = findAllEnduranceTypesUseCase.execute();

        assertSame(expected, result);
        verify(enduranceTypeService).findAll();
    }

    @Test
    void testFindByName() {
        EnduranceType expected = EnduranceType.HIIT;
        when(enduranceTypeService.findById("HIIT")).thenReturn(Optional.of(expected));

        Optional<EnduranceType> result = findEnduranceTypeByNameUseCase.execute("HIIT");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(enduranceTypeService).findById("HIIT");
    }

    @Test
    void testSave() {
        EnduranceType type = EnduranceType.AEROBIC;
        when(enduranceTypeService.save(type)).thenReturn(type);

        EnduranceType result = saveEnduranceTypeUseCase.execute(type);

        assertSame(type, result);
        verify(enduranceTypeService).save(type);
    }

    @Test
    void testDeleteByName() {
        doNothing().when(enduranceTypeService).deleteById("HIIT");

        deleteEnduranceTypeUseCase.execute("HIIT");

        verify(enduranceTypeService).deleteById("HIIT");
    }

    @Test
    void testExistsByName() {
        when(enduranceTypeService.existsById("HIIT")).thenReturn(true);

        boolean result = enduranceTypeExistsUseCase.execute("HIIT");

        assertTrue(result);
        verify(enduranceTypeService).existsById("HIIT");
    }
}
