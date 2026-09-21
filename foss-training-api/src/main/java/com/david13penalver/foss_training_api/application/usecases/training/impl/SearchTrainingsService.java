package com.david13penalver.foss_training_api.application.usecases.training.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.SearchTrainingsUseCase;
import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchTrainingsService implements SearchTrainingsUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public List<Training> execute(TrainingSearchCriteria criteria) {
        log.debug("Executing SearchTrainingsUseCase with criteria: {}", criteria);
        return trainingRepository.findByCriteria(criteria);
    }

    @Override
    public PagedResult<Training> execute(TrainingSearchCriteria criteria, PageQuery pageQuery) {
        log.debug("Executing SearchTrainingsUseCase with criteria: {} and pageQuery: {}", criteria, pageQuery);
        return trainingRepository.findByCriteria(criteria, pageQuery);
    }
}
