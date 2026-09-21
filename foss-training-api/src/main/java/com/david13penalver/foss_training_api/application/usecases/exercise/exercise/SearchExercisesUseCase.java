package com.david13penalver.foss_training_api.application.usecases.exercise.exercise;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria;

public interface SearchExercisesUseCase {

    List<Exercise> execute(ExerciseSearchCriteria criteria);

    PagedResult<Exercise> execute(ExerciseSearchCriteria criteria, PageQuery pageQuery);
}
