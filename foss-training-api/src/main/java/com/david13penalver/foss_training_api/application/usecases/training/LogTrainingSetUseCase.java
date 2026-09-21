package com.david13penalver.foss_training_api.application.usecases.training;

import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface LogTrainingSetUseCase {

    Training execute(Integer trainingId, Integer exerciseId, ResistanceSet set);
}
