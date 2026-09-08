package com.david13penalver.foss_training_api.domain.ports.out.training;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface TrainingRepository {

    List<Training> findAll();

    Optional<Training> findById(Integer id);

    Training save(Training training);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}
