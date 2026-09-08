package com.david13penalver.foss_training_api.infrastructure.adapters.out.program;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainingProgramRepositoryImpl implements TrainingProgramRepository {

    private final InMemoryTrainingProgramDao trainingProgramDao;

    @Override
    public List<TrainingProgram> findAll() {
        return trainingProgramDao.findAll();
    }

    @Override
    public Optional<TrainingProgram> findById(Integer id) {
        return trainingProgramDao.findById(id);
    }

    @Override
    public TrainingProgram save(TrainingProgram program) {
        return trainingProgramDao.save(program);
    }

    @Override
    public void deleteById(Integer id) {
        trainingProgramDao.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return trainingProgramDao.existsById(id);
    }
}
