package com.david13penalver.foss_training_api.infrastructure.adapters.out.training;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepository {

    private final InMemoryTrainingDao trainingDao;

    @Override
    public List<Training> findAll() {
        return trainingDao.findAll();
    }

    @Override
    public Optional<Training> findById(Integer id) {
        return trainingDao.findById(id);
    }

    @Override
    public Training save(Training training) {
        return trainingDao.save(training);
    }

    @Override
    public void deleteById(Integer id) {
        trainingDao.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return trainingDao.existsById(id);
    }
}
