package com.david13penalver.foss_training_api.application.usecases.athlete;

import com.david13penalver.foss_training_api.domain.model.athlete.Gender;
import com.david13penalver.foss_training_api.domain.model.athlete.RelativeStrengthScore;

public interface CalculateRelativeStrengthUseCase {

    RelativeStrengthScore execute(Double totalWeightKg, Double bodyweightKg, Gender gender);
}
