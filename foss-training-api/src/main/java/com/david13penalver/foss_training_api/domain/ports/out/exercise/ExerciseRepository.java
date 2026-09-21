package com.david13penalver.foss_training_api.domain.ports.out.exercise;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria;

public interface ExerciseRepository {

    List<Exercise> findAll();

    Optional<Exercise> findById(Integer id);

    Exercise save(Exercise exercise);

    void deleteById(Integer id);

    boolean existsById(Integer id);

    List<Exercise> findByCriteria(ExerciseSearchCriteria criteria);

    PagedResult<Exercise> findByCriteria(ExerciseSearchCriteria criteria, PageQuery pageQuery);
}