package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.program;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.program.InMemoryTrainingProgramDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.program.TrainingProgramRepositoryImpl;

class TrainingProgramRepositoryImplTest {

    private InMemoryTrainingProgramDao dao;
    private TrainingProgramRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        dao = new InMemoryTrainingProgramDao();
        repository = new TrainingProgramRepositoryImpl(dao);
    }

    @Test
    void testDelegationToDao() {
        TrainingProgram program = TrainingProgram.builder().name("Block Periodization").durationWeeks(6).build();

        TrainingProgram saved = repository.save(program);
        assertSame(program, saved);
        assertEquals(1, saved.getId());

        Optional<TrainingProgram> found = repository.findById(1);
        assertTrue(found.isPresent());
        assertEquals("Block Periodization", found.get().getName());

        assertTrue(repository.existsById(1));

        List<TrainingProgram> all = repository.findAll();
        assertEquals(1, all.size());

        repository.deleteById(1);
        assertFalse(repository.existsById(1));
    }
}
