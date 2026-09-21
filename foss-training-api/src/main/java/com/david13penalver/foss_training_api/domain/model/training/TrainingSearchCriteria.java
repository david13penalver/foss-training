package com.david13penalver.foss_training_api.domain.model.training;

import java.time.LocalDate;

public record TrainingSearchCriteria(
        LocalDate startDate,
        LocalDate endDate,
        TrainingStatusEnum status,
        String query,
        Integer programId
) {

    public static TrainingSearchCriteria empty() {
        return new TrainingSearchCriteria(null, null, null, null, null);
    }
}
