package com.david13penalver.foss_training_api.application.usecases.athlete.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.athlete.LogBodyweightUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogBodyweightService implements LogBodyweightUseCase {

    private final BodyweightRepository bodyweightRepository;

    @Override
    public BodyweightEntry execute(BodyweightEntry entry) {
        log.debug("Executing LogBodyweightUseCase for date: {}, weight: {}", entry.getEntryDate(), entry.getWeightKg());
        if (entry.getEntryDate() == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }
        if (entry.getWeightKg() == null || entry.getWeightKg() <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        return bodyweightRepository.save(entry);
    }
}
