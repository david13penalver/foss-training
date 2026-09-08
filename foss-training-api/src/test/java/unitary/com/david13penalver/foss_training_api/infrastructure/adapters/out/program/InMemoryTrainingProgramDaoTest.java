package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.program;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.program.InMemoryTrainingProgramDao;

class InMemoryTrainingProgramDaoTest {

    private InMemoryTrainingProgramDao dao;

    @BeforeEach
    void setUp() {
        dao = new InMemoryTrainingProgramDao();
    }

    @Test
    void save_assignsId_andFindByIdReturnsProgram() {
        TrainingProgram program = TrainingProgram.builder()
                .name("PPL")
                .durationWeeks(8)
                .periodizationType(PeriodizationType.LINEAR)
                .level(ProgramLevel.INTERMEDIATE)
                .build();

        TrainingProgram saved = dao.save(program);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getId());

        Optional<TrainingProgram> found = dao.findById(1);
        assertTrue(found.isPresent());
        assertEquals("PPL", found.get().getName());
    }

    @Test
    void save_withExistingId_updatesProgramAndSyncsSequence() {
        TrainingProgram program = TrainingProgram.builder()
                .id(10)
                .name("Custom Program")
                .durationWeeks(4)
                .build();

        dao.save(program);

        Optional<TrainingProgram> found = dao.findById(10);
        assertTrue(found.isPresent());

        TrainingProgram next = TrainingProgram.builder().name("Next").durationWeeks(4).build();
        TrainingProgram nextSaved = dao.save(next);
        assertEquals(11, nextSaved.getId());
    }

    @Test
    void findAll_andDeleteById() {
        dao.save(TrainingProgram.builder().name("P1").durationWeeks(4).build());
        dao.save(TrainingProgram.builder().name("P2").durationWeeks(4).build());

        List<TrainingProgram> all = dao.findAll();
        assertEquals(2, all.size());

        assertTrue(dao.existsById(1));
        dao.deleteById(1);
        assertFalse(dao.existsById(1));
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void clear_resetsStoreAndSequence() {
        dao.save(TrainingProgram.builder().name("P1").durationWeeks(4).build());
        dao.clear();

        assertTrue(dao.findAll().isEmpty());
        TrainingProgram newProgram = dao.save(TrainingProgram.builder().name("P2").durationWeeks(4).build());
        assertEquals(1, newProgram.getId());
    }
}
