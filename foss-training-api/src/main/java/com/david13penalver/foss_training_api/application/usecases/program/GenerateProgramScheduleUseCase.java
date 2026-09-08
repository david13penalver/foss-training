package com.david13penalver.foss_training_api.application.usecases.program;

import java.time.LocalDate;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.training.Training;

public interface GenerateProgramScheduleUseCase {
    List<Training> execute(Integer programId, LocalDate startDate);
}
