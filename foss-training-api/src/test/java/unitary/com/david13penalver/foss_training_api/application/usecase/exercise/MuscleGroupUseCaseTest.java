package unitary.com.david13penalver.foss_training_api.application.usecase.exercise;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl.FindAllMuscleGroupsService;
import com.david13penalver.foss_training_api.application.usecases.exercise.musclegroup.impl.FindMuscleGroupByNameService;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

class MuscleGroupUseCaseTest {

    private FindAllMuscleGroupsService findAllMuscleGroupsService;
    private FindMuscleGroupByNameService findMuscleGroupByNameService;

    @BeforeEach
    void setUp() {
        findAllMuscleGroupsService = new FindAllMuscleGroupsService();
        findMuscleGroupByNameService = new FindMuscleGroupByNameService();
    }

    @Test
    void testFindAll() {
        List<MuscleGroup> result = findAllMuscleGroupsService.execute();

        assertNotNull(result);
        assertEquals(MuscleGroup.values().length, result.size());
        assertTrue(result.contains(MuscleGroup.CHEST));
    }

    @Test
    void testFindByName_WhenExists() {
        Optional<MuscleGroup> result = findMuscleGroupByNameService.execute("CHEST");

        assertTrue(result.isPresent());
        assertEquals(MuscleGroup.CHEST, result.get());
    }

    @Test
    void testFindByName_CaseInsensitive() {
        Optional<MuscleGroup> result = findMuscleGroupByNameService.execute("chest");

        assertTrue(result.isPresent());
        assertEquals(MuscleGroup.CHEST, result.get());
    }

    @Test
    void testFindByName_WhenNotExists() {
        Optional<MuscleGroup> result = findMuscleGroupByNameService.execute("NON_EXISTENT");

        assertFalse(result.isPresent());
    }
}
