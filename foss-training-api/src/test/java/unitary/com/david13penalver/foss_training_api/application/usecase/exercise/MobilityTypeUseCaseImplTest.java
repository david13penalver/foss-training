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

import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.FindAllMobilityTypesUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.mobility.impl.FindMobilityTypeByNameUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;

@ExtendWith(MockitoExtension.class)
class MobilityTypeUseCaseImplTest {

    @Mock
    private MobilityTypeService mobilityTypeService;

    @InjectMocks
    private FindAllMobilityTypesUseCaseImpl findAllMobilityTypesUseCase;

    @InjectMocks
    private FindMobilityTypeByNameUseCaseImpl findMobilityTypeByNameUseCase;

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
        MobilityType expected = MobilityType.YOGA;
        when(mobilityTypeService.findById("YOGA")).thenReturn(Optional.of(expected));

        Optional<MobilityType> result = findMobilityTypeByNameUseCase.execute("YOGA");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(mobilityTypeService).findById("YOGA");
    }
}
