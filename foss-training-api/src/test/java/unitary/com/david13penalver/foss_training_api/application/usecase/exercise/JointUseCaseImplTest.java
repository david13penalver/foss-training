package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.ports.in.exercise.JointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.DeleteJointUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.FindAllJointsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.FindJointByNameUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.JointExistsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.SaveJointUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

@ExtendWith(MockitoExtension.class)
class JointUseCaseImplTest {

    @Mock
    private JointService jointService;

    @InjectMocks
    private FindAllJointsUseCaseImpl findAllJointsUseCase;

    @InjectMocks
    private FindJointByNameUseCaseImpl findJointByNameUseCase;

    @InjectMocks
    private SaveJointUseCaseImpl saveJointUseCase;

    @InjectMocks
    private DeleteJointUseCaseImpl deleteJointUseCase;

    @InjectMocks
    private JointExistsUseCaseImpl jointExistsUseCase;

    @Test
    void testFindAll() {
        List<Joint> expected = List.of(Joint.SHOULDER);
        when(jointService.findAll()).thenReturn(expected);

        List<Joint> result = findAllJointsUseCase.execute();

        assertSame(expected, result);
        verify(jointService).findAll();
    }

    @Test
    void testFindByName() {
        Joint expected = Joint.SHOULDER;
        when(jointService.findById("SHOULDER")).thenReturn(Optional.of(expected));

        Optional<Joint> result = findJointByNameUseCase.execute("SHOULDER");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(jointService).findById("SHOULDER");
    }

    @Test
    void testSave() {
        Joint joint = Joint.SHOULDER;
        when(jointService.save(joint)).thenReturn(joint);

        Joint result = saveJointUseCase.execute(joint);

        assertSame(joint, result);
        verify(jointService).save(joint);
    }

    @Test
    void testDeleteByName() {
        doNothing().when(jointService).deleteById("SHOULDER");

        deleteJointUseCase.execute("SHOULDER");

        verify(jointService).deleteById("SHOULDER");
    }

    @Test
    void testExistsByName() {
        when(jointService.existsById("SHOULDER")).thenReturn(true);

        boolean result = jointExistsUseCase.execute("SHOULDER");

        assertTrue(result);
        verify(jointService).existsById("SHOULDER");
    }
}
