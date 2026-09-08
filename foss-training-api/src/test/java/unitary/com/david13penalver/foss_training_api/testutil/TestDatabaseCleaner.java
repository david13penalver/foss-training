package unitary.com.david13penalver.foss_training_api.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.InMemoryExerciseDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.SpringDataExerciseRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.SpringDataTrainingProgramRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SpringDataSessionRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.SpringDataTrainingRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.program.InMemoryTrainingProgramDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.InMemorySessionDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.training.InMemoryTrainingDao;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDatabaseCleaner {

    private final SpringDataTrainingRepository trainingRepository;
    private final SpringDataTrainingProgramRepository programRepository;
    private final SpringDataSessionRepository sessionRepository;
    private final SpringDataExerciseRepository exerciseRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private InMemoryExerciseDao exerciseDao;

    @Autowired(required = false)
    private InMemorySessionDao sessionDao;

    @Autowired(required = false)
    private InMemoryTrainingDao trainingDao;

    @Autowired(required = false)
    private InMemoryTrainingProgramDao programDao;

    @Transactional
    public void clearAll() {
        trainingRepository.deleteAll();
        programRepository.deleteAll();
        sessionRepository.deleteAll();
        exerciseRepository.deleteAll();

        if (jdbcTemplate != null) {
            jdbcTemplate.execute("ALTER TABLE trainings ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE training_programs ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE sessions ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE exercises ALTER COLUMN id RESTART WITH 1");
        }

        if (exerciseDao != null) exerciseDao.clear();
        if (sessionDao != null) sessionDao.clear();
        if (trainingDao != null) trainingDao.clear();
        if (programDao != null) programDao.clear();
    }
}
