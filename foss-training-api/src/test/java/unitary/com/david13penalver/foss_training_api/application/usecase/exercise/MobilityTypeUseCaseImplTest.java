package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.MobilityTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.DeleteMobilityTypeUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.FindAllMobilityTypesUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.FindMobilityTypeByNameUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.MobilityTypeExistsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.SaveMobilityTypeUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

@ExtendWith(MockitoExtension.class)
class MobilityTypeUseCaseImplTest {

    @Mock
    private MobilityTypeService mobilityTypeService;

    @InjectMocks
    private FindAllMobilityTypesUseCaseImpl findAllMobilityTypesUseCase;

    @InjectMocks
    private FindMobilityTypeByNameUseCaseImpl findMobilityTypeByNameUseCase;

    @InjectMocks
    private SaveMobilityTypeUseCaseImpl saveMobilityTypeUseCase;

    @InjectMocks
    private DeleteMobilityTypeUseCaseImpl deleteMobilityTypeUseCase;

    @InjectMocks
    private MobilityTypeExistsUseCaseImpl mobilityTypeExistsUseCase;

    @Test
    void testFindAll() {
        List<MobilityType> expected = List.of(MobilityType.YOGA);
        when(mobilityTypeService.findAll()).thenReturn(expected);

        List<MobilityType> result = findAllMobilityTypesUseCase.execute();

        assertSame(expected, result);
        verify(mobilityTypeService).findAll();
    }

    @Test
    void testFindByName() {
        MobilityType expected = MobilityType.STATIC_STRETCHING;
        when(mobilityTypeService.findById("STATIC_STRETCHING")).thenReturn(Optional.of(expected));

        Optional<MobilityType> result = findMobilityTypeByNameUseCase.execute("STATIC_STRETCHING");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(mobilityTypeService).findById("STATIC_STRETCHING");
    }

    @Test
    void testSave() {
        MobilityType type = MobilityType.YOGA;
        when(mobilityTypeService.save(type)).thenReturn(type);

        MobilityType result = saveMobilityTypeUseCase.execute(type);

        assertSame(type, result);
        verify(mobilityTypeService).save(type);
    }

    @Test
    void testDeleteByName() {
        doNothing().when(mobilityTypeService).deleteById("FOAM_ROLLING");

        deleteMobilityTypeUseCase.execute("FOAM_ROLLING");

        verify(mobilityTypeService).deleteById("FOAM_ROLLING");
    }

    @Test
    void testExistsByName() {
        when(mobilityTypeService.existsById("YOGA")).thenReturn(true);

        boolean result = mobilityTypeExistsUseCase.execute("YOGA");

        assertTrue(result);
        verify(mobilityTypeService).existsById("YOGA");
    }
}
