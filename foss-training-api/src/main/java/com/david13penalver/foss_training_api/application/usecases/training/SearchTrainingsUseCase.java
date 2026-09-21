package com.david13penalver.foss_training_api.application.usecases.training;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria;

public interface SearchTrainingsUseCase {

    List<Training> execute(TrainingSearchCriteria criteria);

    PagedResult<Training> execute(TrainingSearchCriteria criteria, PageQuery pageQuery);
}
