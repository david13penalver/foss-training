package com.david13penalver.foss_training_api.application.usecases.analytics.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.analytics.CalculateWorkloadRatioUseCase;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatio;
import com.david13penalver.foss_training_api.domain.model.analytics.WorkloadRatioCalculator;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateWorkloadRatioService implements CalculateWorkloadRatioUseCase {

    private final TrainingRepository trainingRepository;

    @Override
    public WorkloadRatio execute(LocalDate targetDate) {
        LocalDate effectiveTarget = targetDate != null ? targetDate : LocalDate.now();
        log.debug("Executing CalculateWorkloadRatioUseCase for target date: {}", effectiveTarget);
        List<Training> trainings = trainingRepository.findAll();
        return WorkloadRatioCalculator.compute(trainings, effectiveTarget);
    }
}
