package com.david13penalver.foss_training_api.domain.ports.out.training;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria;

public interface TrainingRepository {

    List<Training> findAll();

    Optional<Training> findById(Integer id);

    Training save(Training training);

    void deleteById(Integer id);

    boolean existsById(Integer id);

    List<Training> findByProgramId(Integer programId);

    List<Training> findByCriteria(TrainingSearchCriteria criteria);

    PagedResult<Training> findByCriteria(TrainingSearchCriteria criteria, PageQuery pageQuery);
}
