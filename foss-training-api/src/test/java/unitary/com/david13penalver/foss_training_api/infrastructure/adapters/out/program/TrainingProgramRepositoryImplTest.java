package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.program;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.program.TrainingProgramRepositoryImpl;

import org.springframework.context.annotation.Import;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@Import(TestDatabaseCleaner.class)
class TrainingProgramRepositoryImplTest {

    @Autowired
    private TrainingProgramRepositoryImpl repository;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
    }

    @Test
    void testDelegationToDatabase() {
        TrainingProgram program = TrainingProgram.builder().name("Block Periodization").durationWeeks(6).build();

        TrainingProgram saved = repository.save(program);
        assertNotNull(saved.getId());
        assertEquals("Block Periodization", saved.getName());

        Optional<TrainingProgram> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Block Periodization", found.get().getName());

        assertTrue(repository.existsById(saved.getId()));
        assertFalse(repository.existsById(999999));
        assertFalse(repository.existsById(null));
        assertEquals(Optional.empty(), repository.findById(null));

        List<TrainingProgram> all = repository.findAll();
        assertEquals(1, all.size());

        repository.deleteById(saved.getId());
        assertFalse(repository.existsById(saved.getId()));

        assertDoesNotThrow(() -> repository.deleteById(null));
        assertDoesNotThrow(() -> repository.deleteById(999999));
    }
}
