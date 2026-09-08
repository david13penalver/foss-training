package com.david13penalver.foss_training_api.infrastructure.adapters.out.training;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.SpringDataTrainingRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepository {

    private final SpringDataTrainingRepository trainingRepository;
    private final TrainingPersistenceMapper mapper;

    @Override
    public List<Training> findAll() {
        return trainingRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Training> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return trainingRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Training save(Training training) {
        TrainingJpaEntity entity = mapper.toJpaEntity(training);
        TrainingJpaEntity saved = trainingRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            trainingRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && trainingRepository.existsById(id);
    }
}
