package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.StretchTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.DeleteStretchTypeUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.FindAllStretchTypesUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.FindStretchTypeByNameUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.SaveStretchTypeUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.stretchtype.impl.StretchTypeExistsUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;

@ExtendWith(MockitoExtension.class)
class StretchTypeUseCaseImplTest {

    @Mock
    private StretchTypeService stretchTypeService;

    @InjectMocks
    private FindAllStretchTypesUseCaseImpl findAllStretchTypesUseCase;

    @InjectMocks
    private FindStretchTypeByNameUseCaseImpl findStretchTypeByNameUseCase;

    @InjectMocks
    private SaveStretchTypeUseCaseImpl saveStretchTypeUseCase;

    @InjectMocks
    private DeleteStretchTypeUseCaseImpl deleteStretchTypeUseCase;

    @InjectMocks
    private StretchTypeExistsUseCaseImpl stretchTypeExistsUseCase;

    @Test
    void testFindAll() {
        List<StretchType> expected = List.of(StretchType.STATIC);
        when(stretchTypeService.findAll()).thenReturn(expected);

        List<StretchType> result = findAllStretchTypesUseCase.execute();

        assertSame(expected, result);
        verify(stretchTypeService).findAll();
    }

    @Test
    void testFindByName() {
        StretchType expected = StretchType.DYNAMIC;
        when(stretchTypeService.findById("DYNAMIC")).thenReturn(Optional.of(expected));

        Optional<StretchType> result = findStretchTypeByNameUseCase.execute("DYNAMIC");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(stretchTypeService).findById("DYNAMIC");
    }

    @Test
    void testSave() {
        StretchType type = StretchType.STATIC;
        when(stretchTypeService.save(type)).thenReturn(type);

        StretchType result = saveStretchTypeUseCase.execute(type);

        assertSame(type, result);
        verify(stretchTypeService).save(type);
    }

    @Test
    void testDeleteByName() {
        doNothing().when(stretchTypeService).deleteById("PNF");

        deleteStretchTypeUseCase.execute("PNF");

        verify(stretchTypeService).deleteById("PNF");
    }

    @Test
    void testExistsByName() {
        when(stretchTypeService.existsById("STATIC")).thenReturn(true);

        boolean result = stretchTypeExistsUseCase.execute("STATIC");

        assertTrue(result);
        verify(stretchTypeService).existsById("STATIC");
    }
}
