package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.MuscleGroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl.FindAllMuscleGroupsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl.FindMuscleGroupByNameUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

@ExtendWith(MockitoExtension.class)
class MuscleGroupUseCaseImplTest {

    @Mock
    private MuscleGroupService muscleGroupService;

    @InjectMocks
    private FindAllMuscleGroupsUseCaseImpl findAllMuscleGroupsUseCase;

    @InjectMocks
    private FindMuscleGroupByNameUseCaseImpl findMuscleGroupByNameUseCase;

    @Test
    void testFindAll() {
        List<MuscleGroup> expected = List.of(MuscleGroup.CHEST);
        when(muscleGroupService.findAll()).thenReturn(expected);

        List<MuscleGroup> result = findAllMuscleGroupsUseCase.execute();

        assertSame(expected, result);
        verify(muscleGroupService).findAll();
    }

    @Test
    void testFindByName() {
        MuscleGroup expected = MuscleGroup.CHEST;
        when(muscleGroupService.findById("CHEST")).thenReturn(Optional.of(expected));

        Optional<MuscleGroup> result = findMuscleGroupByNameUseCase.execute("CHEST");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(muscleGroupService).findById("CHEST");
    }
}
