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

import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.FindAllJointsUseCaseImpl;
import com.david13penalver.foss_training_api.application.usecases.exercise.joint.impl.FindJointByNameUseCaseImpl;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;

@ExtendWith(MockitoExtension.class)
class JointUseCaseImplTest {

    @Mock
    private JointService jointService;

    @InjectMocks
    private FindAllJointsUseCaseImpl findAllJointsUseCase;

    @InjectMocks
    private FindJointByNameUseCaseImpl findJointByNameUseCase;

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
}
