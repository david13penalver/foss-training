package com.david13penalver.foss_training_api.application.usecases.exercise.exercise.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.SearchExercisesUseCase;
import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchExercisesService implements SearchExercisesUseCase {

    private final ExerciseRepository exerciseRepository;

    @Override
    public List<Exercise> execute(ExerciseSearchCriteria criteria) {
        log.debug("Executing SearchExercisesUseCase with criteria: {}", criteria);
        return exerciseRepository.findByCriteria(criteria);
    }

    @Override
    public PagedResult<Exercise> execute(ExerciseSearchCriteria criteria, PageQuery pageQuery) {
        log.debug("Executing SearchExercisesUseCase with criteria: {} and pageQuery: {}", criteria, pageQuery);
        return exerciseRepository.findByCriteria(criteria, pageQuery);
    }
}
