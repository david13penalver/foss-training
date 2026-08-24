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

import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.FindAllEnduranceTypesUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.endurance.impl.FindEnduranceTypeByNameUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;

@ExtendWith(MockitoExtension.class)
class EnduranceTypeUseCaseImplTest {

    @Mock
    private EnduranceTypeService enduranceTypeService;

    @InjectMocks
    private FindAllEnduranceTypesUseCaseImpl findAllEnduranceTypesUseCase;

    @InjectMocks
    private FindEnduranceTypeByNameUseCaseImpl findEnduranceTypeByNameUseCase;

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
}
